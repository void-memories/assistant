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
)

data class AgentSetting(
    val name: String,
    val description: String,
    val inputType: InputType,
    val defaultValue: Any? = null,
    val icon: ImageVector,
    val iconTint: Color
)

data class PermissionItem(
    val name: String,
    val icon: ImageVector,
    val runtimePermission: AppPermission
)

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
