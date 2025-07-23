/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.domain.models

import org.json.JSONObject
import org.json.JSONArray

data class FillerAudio(
    val data: ShortArray,
    var hasPlayed: Boolean = false
) {
    override fun toString(): String {
        val json = JSONObject()
        val dataArray = JSONArray()
        data.forEach { dataArray.put(it) }
        json.put("data", dataArray)
        json.put("hasPlayed", hasPlayed)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): FillerAudio {
            val json = JSONObject(str)
            val dataArray = json.getJSONArray("data")
            val data = ShortArray(dataArray.length()) { i ->
                dataArray.getInt(i).toShort()
            }
            val hasPlayed = json.getBoolean("hasPlayed")
            return FillerAudio(data, hasPlayed)
        }
    }
}
