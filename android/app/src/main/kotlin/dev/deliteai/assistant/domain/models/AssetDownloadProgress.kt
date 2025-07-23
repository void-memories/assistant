/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.domain.models

import org.json.JSONObject

data class AssetDownloadProgress(
    val name: String,
    val downloadPercentage: Int
){
    override fun toString(): String {
        val json = JSONObject()
        json.put("name", name)
        json.put("downloadPercentage", downloadPercentage)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): AssetDownloadProgress {
            val json = JSONObject(str)
            val name = json.getString("name")
            val downloadPercentage = json.getInt("downloadPercentage")
            return AssetDownloadProgress(name, downloadPercentage)
        }
    }
}
