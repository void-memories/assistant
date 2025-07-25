/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.domain.repositories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dev.deliteai.assistant.domain.models.Chat
import org.json.JSONObject

//TODO:(naman) break this in smaller chunks
class CacheRepository(application: Application) {
    private val MAX_HISTORY_SIZE = 25
    private val PREF_NAME = "chat_prefs"
    private val CHAT_IDS_KEY = "chat_ids"
    private val FIRST_BOOT_KEY = "first_boot"
    private val FIRST_CHAT_KEY = "first_chat"
    private val INVITE_REGISTRATION_KEY = "invite_registered"
    private val APP_START_COUNT_KEY = "app_start_count"
    private val AGENT_CONFIG_PREFIX = "agent_config_"
    private var sharedPreferences: SharedPreferences =
        application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun cacheChat(chat: Chat) {
        val chatIds = getChatIds()
        if (!chatIds.contains(chat.id)) {
            if (chatIds.size == MAX_HISTORY_SIZE) {
                chatIds.removeAt(0)
            }

            chatIds.add(chat.id)
            saveChatIds(chatIds)
        }

        sharedPreferences.edit().putString(chat.id, chat.toString()).apply()
    }

    fun retrieveChat(id: String): Chat {
        val chatString = sharedPreferences.getString(id, "") ?: ""
        return Chat.fromString(chatString)
    }

    fun getChatIds(): MutableList<String> {
        val chatIdsString = sharedPreferences.getString(CHAT_IDS_KEY, "") ?: ""
        return if (chatIdsString.isNotEmpty()) chatIdsString.split(",")
            .toMutableList() else mutableListOf()
    }

    fun registerUserFirstBoot() {
        sharedPreferences.edit().putBoolean(FIRST_BOOT_KEY, false).apply()
    }

    fun isFirstBoot(): Boolean {
        return sharedPreferences.getBoolean(FIRST_BOOT_KEY, true)
    }

    fun hasUserEverClickedOnChat(): Boolean {
        return sharedPreferences.getBoolean(FIRST_CHAT_KEY, false)
    }

    fun registerUserTapToChat() {
        sharedPreferences.edit().putBoolean(FIRST_CHAT_KEY, true).apply()
    }

    fun deleteChat(id: String) {
        deleteChatId(id)
        deleteChatHistory(id)
    }

    fun isUserInvited(): Boolean {
        return sharedPreferences.getBoolean(INVITE_REGISTRATION_KEY, false)
    }

    fun registerUserInvitation() {
        sharedPreferences.edit().putBoolean(INVITE_REGISTRATION_KEY, true).apply()
    }

    fun getAppStartCount(): Int {
        return sharedPreferences.getInt(APP_START_COUNT_KEY, 0)
    }

    fun incrementAppStartCount() {
        sharedPreferences.edit().putInt(APP_START_COUNT_KEY, getAppStartCount() + 1).apply()
    }

    private fun deleteChatId(id: String) {
        val chatIds = getChatIds()
        chatIds.remove(id)
        saveChatIds(chatIds)
    }

    private fun deleteChatHistory(id: String) {
        sharedPreferences.edit().remove(id).apply()
    }

    private fun saveChatIds(chatIds: List<String>) {
        sharedPreferences.edit().putString(CHAT_IDS_KEY, chatIds.joinToString(",")).apply()
    }

    //agent persistance
    fun saveAgentConfigs(agentId: String, settingId: String, value: Any) {
        val existingConfigString =
            sharedPreferences.getString(AGENT_CONFIG_PREFIX + agentId, "{}") ?: "{}"
        val settingsJson = JSONObject(existingConfigString)
        settingsJson.put(settingId, value)
        sharedPreferences.edit()
            .putString(AGENT_CONFIG_PREFIX + agentId, settingsJson.toString())
            .apply()
    }

    fun getAgentConfigs(id: String): Map<String, Any?> {
        val configString = sharedPreferences.getString(AGENT_CONFIG_PREFIX + id, "{}") ?: "{}"
        val configJson = JSONObject(configString)
        val configMap = mutableMapOf<String, Any>()

        configJson.keys().forEach { key ->
            val value = configJson.get(key)
            if (value != JSONObject.NULL) {
                configMap[key] = value
            }
        }

        return configMap
    }
}
