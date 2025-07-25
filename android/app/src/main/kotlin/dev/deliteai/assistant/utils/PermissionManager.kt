package dev.deliteai.assistant.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import dev.deliteai.assistant.domain.models.AppPermission
import kotlinx.coroutines.CompletableDeferred

class PermissionManager(
    private val context: Context,
    caller: ActivityResultCaller,
) {
    // for normal runtime perms
    private val normalLauncher = caller.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        currentPerm?.let { pending[it]?.complete(granted) }
        currentPerm = null
    }

    // for sending user to notification‑access settings
    private val settingsLauncher = caller.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        currentPerm?.let { perm ->
            // after returning, re‑check
            pending[perm]?.complete(isNotificationAccessGranted())
        }
        currentPerm = null
    }

    private var currentPerm: String? = null
    private val pending = mutableMapOf<String, CompletableDeferred<Boolean>>()

    suspend fun grant(permission: AppPermission) {
        val perm = permission.androidPermission

        when (permission) {
            AppPermission.POST_NOTIFICATION -> {
                val d = CompletableDeferred<Boolean>()
                pending[perm] = d
                currentPerm = perm
                normalLauncher.launch(perm)
                d.await()
            }

            AppPermission.READ_NOTIFICATION -> {
                val d = CompletableDeferred<Boolean>()
                pending[perm] = d
                currentPerm = perm
                settingsLauncher.launch(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                )
                d.await()
            }
        }
    }

    private fun isNotificationAccessGranted(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }
}
