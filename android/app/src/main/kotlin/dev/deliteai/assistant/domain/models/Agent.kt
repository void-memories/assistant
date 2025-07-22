package dev.deliteai.assistant.domain.models

enum class RuntimePermission(val androidPermission: String) {
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
    val requiredPermissions: Set<RuntimePermission> = emptySet(),
    val settings: List<AgentSetting> = emptyList(),
    val image: Int,
)

data class AgentSetting(
    val name: String,
    val description: String,
    val inputType: InputType,
    val defaultValue: Any? = null,
)
