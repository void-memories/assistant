package dev.deliteai.assistant.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.deliteai.assistant.R
import dev.deliteai.assistant.presentation.ui.theme.accentLow1
import org.json.JSONObject
import org.json.JSONArray

enum class AppPermission(val androidPermission: String) {
    POST_NOTIFICATION("android.permission.POST_NOTIFICATIONS"),
    READ_NOTIFICATION("android.permission.READ_NOTIFICATION")
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
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("description", description)
        
        val permissionsArray = JSONArray()
        requiredPermissions.forEach { permission ->
            permissionsArray.put(JSONObject().apply {
                put("name", permission.name)
                put("runtimePermission", permission.runtimePermission.androidPermission)
            })
        }
        json.put("requiredPermissions", permissionsArray)
        
        val settingsArray = JSONArray()
        settings.forEach { setting ->
            settingsArray.put(JSONObject().apply {
                put("name", setting.name)
                put("description", setting.description)
                put("inputType", setting.inputType.name)
                put("defaultValue", setting.defaultValue)
                put("iconTint", setting.iconTint.value.toString())
            })
        }
        json.put("settings", settingsArray)
        json.put("image", image)
        json.put("highlight", highlight.value.toString())
        
        return json.toString()
    }

    companion object {
        fun fromString(str: String): Agent {
            val json = JSONObject(str)
            val id = json.getString("id")
            val name = json.getString("name")
            val description = json.getString("description")
            
            val permissionsArray = json.getJSONArray("requiredPermissions")
            val requiredPermissions = mutableSetOf<PermissionItem>()
            for (i in 0 until permissionsArray.length()) {
                val permissionJson = permissionsArray.getJSONObject(i)
                val permissionName = permissionJson.getString("name")
                val runtimePermission = AppPermission.values().find { 
                    it.androidPermission == permissionJson.getString("runtimePermission") 
                } ?: AppPermission.POST_NOTIFICATION
                
                // Find appropriate icon based on permission type
                val icon = when(runtimePermission) {
                    AppPermission.POST_NOTIFICATION -> Icons.Filled.Notifications
                    AppPermission.READ_NOTIFICATION -> Icons.Filled.RemoveRedEye
                }
                
                requiredPermissions.add(PermissionItem(permissionName, icon, runtimePermission))
            }
            
            val settingsArray = json.getJSONArray("settings")
            val settings = mutableListOf<AgentSetting>()
            for (i in 0 until settingsArray.length()) {
                val settingJson = settingsArray.getJSONObject(i)
                val settingName = settingJson.getString("name")
                val settingDescription = settingJson.getString("description")
                val inputType = InputType.valueOf(settingJson.getString("inputType"))
                val defaultValue = if (settingJson.isNull("defaultValue")) null else settingJson.get("defaultValue")
                val iconTint = Color(settingJson.getString("iconTint").toULong())
                
                // Find appropriate icon based on setting name
                val icon = when {
                    settingName.contains("time", ignoreCase = true) -> Icons.Default.Alarm
                    settingName.contains("play", ignoreCase = true) -> Icons.Default.Speaker
                    else -> Icons.Default.Alarm
                }
                
                settings.add(AgentSetting(settingName, settingDescription, inputType, defaultValue, icon, iconTint))
            }
            
            val image = json.getInt("image")
            val highlight = Color(json.getString("highlight").toULong())
            
            return Agent(id, name, description, requiredPermissions, settings, image, highlight)
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
        val json = JSONObject()
        json.put("name", name)
        json.put("description", description)
        json.put("inputType", inputType.name)
        json.put("defaultValue", defaultValue)
        json.put("iconTint", iconTint.value.toString())
        return json.toString()
    }

    companion object {
        fun fromString(str: String): AgentSetting {
            val json = JSONObject(str)
            val name = json.getString("name")
            val description = json.getString("description")
            val inputType = InputType.valueOf(json.getString("inputType"))
            val defaultValue = if (json.isNull("defaultValue")) null else json.get("defaultValue")
            val iconTint = Color(json.getString("iconTint").toULong())
            
            // Find appropriate icon based on setting name
            val icon = when {
                name.contains("time", ignoreCase = true) -> Icons.Default.Alarm
                name.contains("play", ignoreCase = true) -> Icons.Default.Speaker
                else -> Icons.Default.Alarm
            }
            
            return AgentSetting(name, description, inputType, defaultValue, icon, iconTint)
        }
    }
}

data class PermissionItem(
    val name: String,
    val icon: ImageVector,
    val runtimePermission: AppPermission
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("name", name)
        json.put("runtimePermission", runtimePermission.androidPermission)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): PermissionItem {
            val json = JSONObject(str)
            val name = json.getString("name")
            val runtimePermission = AppPermission.values().find { 
                it.androidPermission == json.getString("runtimePermission") 
            } ?: AppPermission.POST_NOTIFICATION
            
            // Find appropriate icon based on permission type
            val icon = when(runtimePermission) {
                AppPermission.POST_NOTIFICATION -> Icons.Filled.Notifications
                AppPermission.READ_NOTIFICATION -> Icons.Filled.RemoveRedEye
            }
            
            return PermissionItem(name, icon, runtimePermission)
        }
    }
}

val agents = listOf(
    Agent(
        id = "id",
        name = "Notification Summarizer",
        description = "Get summary of your notifications every time you wake up",
        requiredPermissions = setOf(
            PermissionItem(
                name = "Post Notifications",
                icon = Icons.Filled.Notifications,
                runtimePermission = AppPermission.POST_NOTIFICATION
            ),
            PermissionItem(
                name = "Read Notifications",
                icon = Icons.Filled.RemoveRedEye,
                runtimePermission = AppPermission.READ_NOTIFICATION
            )
        ),
        settings = listOf(
            AgentSetting(
                name = "Wake‑up time",
                description = "We’ll keep the summary ready before this time.",
                inputType = InputType.TIME,
                defaultValue = "5:00",
                icon = Icons.Default.Alarm,
                iconTint = Color(0xffC6790D)
            ),
            AgentSetting(
                name = "Autoplay summary",
                description = "We’ll start playing the summary via on‑device TTS at your scheduled wake‑up time.",
                inputType = InputType.BOOL,
                defaultValue = true,
                icon = Icons.Default.Speaker,
                iconTint = Color.Magenta
            ),
        ),
        image = R.drawable.ag_notification_summarizer,
        highlight = Color(0xff5A4900)
    ),
    Agent(
        id = "id2",
        name = "Gmail Agent",
        description = "Get summary of your unread emails",
        requiredPermissions = setOf(
            PermissionItem(
                name = "Read Notifications",
                icon = Icons.Filled.MailOutline,
                runtimePermission = AppPermission.READ_NOTIFICATION
            )
        ),
        settings = listOf(
            AgentSetting(
                name = "Wake‑up time",
                description = "We’ll keep the summary ready before this time.",
                inputType = InputType.TIME,
                defaultValue = "5:00",
                icon = Icons.Default.Alarm,
                iconTint = Color.Magenta
            ),
        ),
        image = R.drawable.ag_gmail_agent,
        highlight = Color(0xff471B1B)
    )
)
