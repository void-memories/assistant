package dev.deliteai.assistant.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.deliteai.assistant.presentation.ui.theme.accentLow1
import dev.deliteai.assistant.utils.formatColor
import dev.deliteai.assistant.utils.getSettingIcon
import dev.deliteai.assistant.utils.parseColor
import dev.deliteai.assistant.utils.resolveDrawableResource
import dev.deliteai.assistant.utils.resolveResourceName
import org.json.JSONArray
import org.json.JSONObject


enum class AppPermission(val androidPermission: String) {
    POST_NOTIFICATION("android.permission.POST_NOTIFICATIONS"),
    READ_NOTIFICATION("android.permission.READ_NOTIFICATION");

    val displayIcon: ImageVector
        get() = when (this) {
            POST_NOTIFICATION -> Icons.Filled.Notifications
            READ_NOTIFICATION -> Icons.Filled.RemoveRedEye
        }
}

enum class InputType {
    TEXT, BOOL, NUMBER, TIME, DATE, DATE_TIME
}

data class Agent(
    val id: String,
    val name: String,
    val description: String,
    val requiredPermissions: Set<PermissionItem> = emptySet(),
    val settings: List<AgentSetting> = emptyList(),
    val image: Int,
    val highlight: Color = accentLow1
) {
    override fun toString(): String {
        val json = JSONObject().apply {
            put("id", id)
            put("name", name)
            put("description", description)

            // Permissions
            put("requiredPermissions", JSONArray().apply {
                requiredPermissions.forEach { permission ->
                    put(JSONObject().apply {
                        put("name", permission.name)
                        put("runtimePermission", permission.runtimePermission.androidPermission)
                    })
                }
            })

            // Settings
            put("settings", JSONArray().apply {
                settings.forEach { setting ->
                    put(JSONObject().apply {
                        put("name", setting.name)
                        put("description", setting.description)
                        put("inputType", setting.inputType.name)
                        put("defaultValue", setting.defaultValue)
                        put("iconTint", formatColor(setting.iconTint))
                    })
                }
            })

            put("image", resolveResourceName(image))
            put("highlight", formatColor(highlight))
        }
        return json.toString()
    }

    companion object {
        fun fromString(str: String): Agent {
            val json = JSONObject(str)
            val id = json.getString("id")
            val name = json.getString("name")
            val description = json.getString("description")

            val requiredPermissions = json.getJSONArray("requiredPermissions")
                .let { array ->
                    (0 until array.length()).map { i ->
                        val permissionJson = array.getJSONObject(i)
                        val permissionName = permissionJson.getString("name")
                        val runtimePermissionString = permissionJson.getString("runtimePermission")

                        val runtimePermission = AppPermission.values()
                            .find { it.androidPermission == runtimePermissionString }
                            ?: throw IllegalArgumentException("Unknown permission: $runtimePermissionString")

                        PermissionItem(
                            name = permissionName,
                            icon = runtimePermission.displayIcon,
                            runtimePermission = runtimePermission
                        )
                    }.toSet()
                }

            val settings = json.getJSONArray("settings")
                .let { array ->
                    (0 until array.length()).map { i ->
                        val settingJson = array.getJSONObject(i)
                        val settingName = settingJson.getString("name")
                        val settingDescription = settingJson.getString("description")
                        val inputType = InputType.valueOf(settingJson.getString("inputType"))
                        val defaultValue = if (settingJson.isNull("defaultValue")) {
                            null
                        } else {
                            settingJson.get("defaultValue")
                        }
                        val iconTint = parseColor(settingJson.getString("iconTint"))
                        val icon = getSettingIcon(settingName, inputType)

                        AgentSetting(
                            name = settingName,
                            description = settingDescription,
                            inputType = inputType,
                            defaultValue = defaultValue,
                            icon = icon,
                            iconTint = iconTint
                        )
                    }
                }

            val imageName = json.getString("image")
            val imageRes = resolveDrawableResource(imageName)

            val highlightColor = parseColor(json.getString("highlight"))

            return Agent(
                id = id,
                name = name,
                description = description,
                requiredPermissions = requiredPermissions,
                settings = settings,
                image = imageRes,
                highlight = highlightColor
            )
        }
    }
}

data class AgentSetting(
    val name: String,
    val description: String,
    val inputType: InputType,
    val defaultValue: Any? = null,
    val icon: ImageVector,
    val iconTint: Color
) {
    override fun toString(): String {
        return JSONObject().apply {
            put("name", name)
            put("description", description)
            put("inputType", inputType.name)
            put("defaultValue", defaultValue)
            put("iconTint", formatColor(iconTint))
        }.toString()
    }

    companion object {
        fun fromString(str: String): AgentSetting {
            val json = JSONObject(str)
            val name = json.getString("name")
            val description = json.getString("description")
            val inputType = InputType.valueOf(json.getString("inputType"))
            val defaultValue = if (json.isNull("defaultValue")) null else json.get("defaultValue")
            val iconTint = parseColor(json.getString("iconTint"))
            val icon = getSettingIcon(name, inputType)

            return AgentSetting(
                name = name,
                description = description,
                inputType = inputType,
                defaultValue = defaultValue,
                icon = icon,
                iconTint = iconTint
            )
        }
    }
}

data class PermissionItem(
    val name: String,
    val icon: ImageVector,
    val runtimePermission: AppPermission
) {
    override fun toString(): String {
        return JSONObject().apply {
            put("name", name)
            put("runtimePermission", runtimePermission.androidPermission)
        }.toString()
    }

    companion object {
        fun fromString(str: String): PermissionItem {
            val json = JSONObject(str)
            val name = json.getString("name")
            val runtimePermissionString = json.getString("runtimePermission")

            val runtimePermission = AppPermission.values()
                .find { it.androidPermission == runtimePermissionString }
                ?: throw IllegalArgumentException("Unknown permission: $runtimePermissionString")

            return PermissionItem(
                name = name,
                icon = runtimePermission.displayIcon,
                runtimePermission = runtimePermission
            )
        }
    }
}
