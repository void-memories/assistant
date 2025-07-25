/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.utils

import dev.deliteai.NimbleNet
import dev.deliteai.datamodels.NimbleNetConfig
import dev.deliteai.datamodels.NimbleNetTensor
import dev.deliteai.assistant.BuildConfig
import dev.deliteai.assistant.domain.models.AssetDownloadProgress
import dev.deliteai.assistant.domain.models.InputType
import dev.deliteai.assistant.R
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.app.ActivityManager
import android.app.Application
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.deliteai.assistant.domain.models.AppPermission
import dev.deliteai.impl.common.DATATYPE
import dev.deliteai.impl.common.NIMBLENET_VARIANTS
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

const val TAG = "CHAT-APP"
const val MAX_CHAR_LEN = 200

enum class VoiceOverlayState {
    SPEAKING,
    IDLE
}

enum class DeviceTier {
    ONE, //high end devices
    TWO, //medium end devices
    UNSUPPORTED //low end devices, unsupported by us
}

fun byteArrayToFloatArray(
    byteArray: ByteArray?,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN
): FloatArray? {
    if (byteArray == null)
        return null

    if (byteArray.size % 4 != 0) {
        throw IllegalArgumentException("Byte array size must be a multiple of 4")
    }

    val floatArray = FloatArray(byteArray.size / 4)
    val buffer = ByteBuffer.wrap(byteArray).order(byteOrder)

    for (i in floatArray.indices) {
        floatArray[i] = buffer.float
    }

    return floatArray
}

suspend fun initializeNimbleNetAndWaitForIsReady(application: Application, ct: String) {
    val nimblenetConfig = NimbleNetConfig(
        clientId = BuildConfig.NIMBLENET_CONFIG_CLIENT_ID,
        host = BuildConfig.NIMBLENET_CONFIG_HOST,
        deviceId = getInternalDeviceId(application),
        clientSecret = BuildConfig.NIMBLENET_CONFIG_CLIENT_SECRET,
        debug = false,
        compatibilityTag = ct,
        libraryVariant = NIMBLENET_VARIANTS.STATIC,
        showDownloadProgress = true
    )

    val nimbleNetResult = NimbleNet.initialize(application, nimblenetConfig)
    check(nimbleNetResult.status)
    while (!NimbleNet.isReady().status) {
        delay(1000)
    }
    passLexiconToTheWorkflowScript(application.applicationContext)
}

//used only during the app init
private suspend fun passLexiconToTheWorkflowScript(context: Context) {
    val resString = context.assets.open("lexicon.json").bufferedReader().use { it.readText() }
    val lexiconArray = JSONObject(resString)
    val res = NimbleNet.runMethod(
        "init",
        inputs = hashMapOf(
            "lexicon" to NimbleNetTensor(
                shape = null, data = lexiconArray, datatype = DATATYPE.JSON
            )
        )
    )
    check(res.status) { "NimbleNet.runMethod('init') failed with status: ${res.status}" }
}

fun MutableList<String>.mergeChunks(): MutableList<String> {
    val mergedChunks = mutableListOf("")
    var curIdx = 0
    this.forEachIndexed { idx, text ->
        if (text.length + mergedChunks[curIdx].length < MAX_CHAR_LEN / 2)
            mergedChunks[curIdx] += if (idx == 0) text else " $text"
        else {
            mergedChunks.add(text)
            curIdx++
        }
    }
    return mergedChunks
}

fun chunkSentence(input: String): MutableList<String> {
    val chunkedList = mutableListOf<String>()
    if (input.length < MAX_CHAR_LEN) {
        chunkedList.add(input)
    } else if ("," in input) {
        val commaSplits = input.split(",", limit = 3)
        val splitsMerged = mutableListOf("")
        var curIdx = 0
        commaSplits.forEachIndexed { idx, split ->
            when {
                splitsMerged[curIdx].length + split.length < MAX_CHAR_LEN -> {
                    splitsMerged[curIdx] += if (idx > 0) "," else ""
                    splitsMerged[curIdx] += split
                }

                split.length > MAX_CHAR_LEN -> {
                    var spaceSplits = split.split(" ").chunked(6).map { it.joinToString(" ") }
                    splitsMerged[curIdx] += ","
                    spaceSplits = spaceSplits.map { s ->
                        return@map if (splitsMerged[curIdx].length + s.length < MAX_CHAR_LEN) {
                            splitsMerged[curIdx] += s
                            null
                        } else s
                    }.filterNotNull()
                    splitsMerged.addAll(spaceSplits)
                    curIdx = splitsMerged.lastIndex
                }

                else -> {
                    splitsMerged.add(split)
                    curIdx++
                }
            }
        }
        chunkedList.addAll(splitsMerged)
    } else {
        val spaceChunks = input.split(" ").chunked(6).map { it.joinToString(" ") }
        chunkedList.addAll(spaceChunks)
    }
    return chunkedList
}

fun getDeviceName(): String {
    // Try to retrieve the market name property (this is manufacturer/ROM-dependent).
    val marketName = try {
        Class.forName("android.os.SystemProperties")
            .getMethod("get", String::class.java)
            .invoke(null, "ro.product.marketname") as String
    } catch (e: Exception) {
        ""
    }
    return if (marketName.isNotBlank()) {
        marketName
    } else {
        Build.MANUFACTURER.trim() + " " + Build.MODEL.trim()
    }
}

fun getSoc(): String {
    return try {
        val c = Class.forName("android.os.SystemProperties")
        val getMethod = c.getMethod("get", String::class.java)
        var soc = ((getMethod.invoke(null, "ro.soc.manufacturer") as String).trim() + " "
                + (getMethod.invoke(null, "ro.soc.model") as String).trim()).trim()
        if (soc.isBlank()) {
            soc = (getMethod.invoke(null, "ro.board.platform") as String).trim()
        }
        if (soc.isBlank()) {
            soc = (getMethod.invoke(null, "ro.hardware") as String).trim()
        }
        if (soc.isBlank()) {
            val file = File("/proc/cpuinfo")
            if (file.exists()) {
                file.forEachLine { line ->
                    if (line.startsWith("Hardware")) {
                        soc = line.split(":").getOrElse(1) { "" }.trim()
                        return@forEachLine
                    }
                }
            }
        }

        return soc
    } catch (e: Exception) {
        ""
    }
}


fun getRamInGb(context: Context): Int {
    return try {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        ceil(totalMemGb).toInt()
    } catch (e: Exception) {
        0
    }
}

fun getNumCores(): Int {
    return try {
        var coreCount = 0
        for (i in 0..128) {
            val cpuDir = File("/sys/devices/system/cpu/cpu$i")
            if (!cpuDir.exists()) {
                break
            }
            coreCount++
        }
        coreCount
    } catch (e: Exception) {
        0
    }
}

fun getCurrentAppVersionCode(context: Application): Int {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        packageInfo.longVersionCode.toInt()
    else
        packageInfo.versionCode
}

fun openUrlInBrowser(application: Application, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    application.startActivity(intent)
}

fun getActiveDownloadProgress(context: Context): List<AssetDownloadProgress> {
    val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val query = DownloadManager.Query()
        .setFilterByStatus(
            DownloadManager.STATUS_RUNNING or
                    DownloadManager.STATUS_PENDING or
                    DownloadManager.STATUS_PAUSED
        )

    val cursor: Cursor = downloadManager.query(query)
    val result = mutableListOf<AssetDownloadProgress>()

    cursor.use { c ->
        while (c.moveToNext()) {
            val name = c.getString(
                c.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)
            ) ?: "Unknown"

            val totalBytes = c.getLong(
                c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            )
            val downloadedBytes = c.getLong(
                c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            )

            val percent = if (totalBytes > 0) {
                (downloadedBytes * 100 / totalBytes).toInt()
            } else {
                0
            }

            result += AssetDownloadProgress(name, percent)
        }
    }

    return result
}

fun clearAllDownloadManagerJobs(context: Context) {
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val statusFilter = DownloadManager.STATUS_PENDING or
            DownloadManager.STATUS_RUNNING or
            DownloadManager.STATUS_PAUSED or
            DownloadManager.STATUS_SUCCESSFUL or
            DownloadManager.STATUS_FAILED
    val query = DownloadManager.Query().setFilterByStatus(statusFilter)
    val cursor: Cursor = dm.query(query)
    val ids = mutableListOf<Long>()
    val idColumn = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
    while (cursor.moveToNext()) {
        ids += cursor.getLong(idColumn)
    }
    cursor.close()
    if (ids.isNotEmpty()) {
        dm.remove(*ids.toLongArray())
    }
}

fun Context.copyTextToClipboard(text: String?) {
    if(text == null) return

    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
        ClipData.newPlainText(
            "label",
            text
        )
    )
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun formatTimeUsingSimpleDateFormat(date: Date): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

@SuppressLint("HardwareIds")
internal fun getInternalDeviceId(application: Application): String {
    return try {
        Settings.Secure.getString(
            application.contentResolver, Settings.Secure.ANDROID_ID
        )
    } catch (e: Exception) {
        "null"
    }
}

fun Context.isPermissionGranted(permission: AppPermission): Boolean =
    when (permission) {
        AppPermission.POST_NOTIFICATION ->
            ContextCompat.checkSelfPermission(
                this, permission.androidPermission
            ) == PackageManager.PERMISSION_GRANTED

        AppPermission.READ_NOTIFICATION ->
            NotificationManagerCompat
                .getEnabledListenerPackages(this)
                .contains(packageName)
    }

/**
 * Parses a color string that can be in hex format (0xFFFFFFFF) or decimal format.
 * Ensures the color is valid for Compose by using proper ARGB parsing.
 */
fun parseColor(colorString: String): Color {
    return try {
        val hexString = when {
            colorString.startsWith("0x", ignoreCase = true) -> 
                colorString.removePrefix("0x")
            colorString.startsWith("#") -> 
                colorString.removePrefix("#")
            else -> 
                colorString.toULong().toString(16).padStart(8, '0')
        }
        
        // Ensure we have exactly 8 hex characters (ARGB)
        val normalizedHex = hexString.padStart(8, 'f').take(8)
        
        // Parse ARGB components
        val alpha = normalizedHex.substring(0, 2).toInt(16) / 255f
        val red = normalizedHex.substring(2, 4).toInt(16) / 255f
        val green = normalizedHex.substring(4, 6).toInt(16) / 255f
        val blue = normalizedHex.substring(6, 8).toInt(16) / 255f
        
        Color(red, green, blue, alpha)
    } catch (e: Exception) {
        // Fallback to a default color if parsing fails
        Color(0.5f, 0.5f, 0.5f, 1.0f) // Gray
    }
}

/**
 * Formats a color to a consistent hex string format.
 */
fun formatColor(color: Color): String {
    val alpha = (color.alpha * 255).toInt().toString(16).padStart(2, '0')
    val red = (color.red * 255).toInt().toString(16).padStart(2, '0')
    val green = (color.green * 255).toInt().toString(16).padStart(2, '0')
    val blue = (color.blue * 255).toInt().toString(16).padStart(2, '0')
    return "0x$alpha$red$green$blue"
}

/**
 * Resolves drawable resource name to resource ID safely.
 */
fun resolveDrawableResource(imageName: String): Int {
    return try {
        val imageField = R.drawable::class.java.getField(imageName)
        imageField.getInt(null)
    } catch (e: Exception) {
        // Fallback to a default drawable or throw a more descriptive error
        throw IllegalArgumentException("Drawable resource '$imageName' not found", e)
    }
}

/**
 * Resolves resource ID back to resource name safely.
 */
fun resolveResourceName(resourceId: Int): String {
    return R.drawable::class.java.fields
        .firstOrNull { field ->
            try { field.getInt(null) == resourceId } catch (e: Exception) { false }
        }?.name ?: "unknown_resource"
}

/**
 * Determines appropriate icon for a setting based on its name and type.
 */
fun getSettingIcon(settingName: String, inputType: InputType): ImageVector {
    return when {
        settingName.contains("time", ignoreCase = true) -> Icons.Default.Alarm
        settingName.contains("play", ignoreCase = true) ||
        settingName.contains("audio", ignoreCase = true) ||
        settingName.contains("sound", ignoreCase = true) -> Icons.Default.Speaker
        settingName.contains("mail", ignoreCase = true) ||
        settingName.contains("email", ignoreCase = true) -> Icons.Default.MailOutline
        inputType == InputType.BOOL -> Icons.Default.Settings
        else -> Icons.Default.Settings
    }
}
