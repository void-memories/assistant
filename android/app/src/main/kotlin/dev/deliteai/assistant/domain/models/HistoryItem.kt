/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.domain.models

import org.json.JSONObject
import java.util.Date

data class HistoryItem(
    val parentChatId: String,
    val title: String,
    val dateTime: Date
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("parentChatId", parentChatId)
        json.put("title", title)
        json.put("dateTime", dateTime.time)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): HistoryItem {
            val json = JSONObject(str)
            val parentChatId = json.getString("parentChatId")
            val title = json.getString("title")
            val dateTime = Date(json.getLong("dateTime"))
            return HistoryItem(parentChatId, title, dateTime)
        }
    }
}
