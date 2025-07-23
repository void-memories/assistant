/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.domain.models

import org.json.JSONObject
import org.json.JSONArray

data class TierConfig(
    val minMultiCoreScore: Int,
    val minRam: Int,
    val minNumCores: Int
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("minMultiCoreScore", minMultiCoreScore)
        json.put("minRam", minRam)
        json.put("minNumCores", minNumCores)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): TierConfig {
            val json = JSONObject(str)
            return TierConfig(
                minMultiCoreScore = json.getInt("minMultiCoreScore"),
                minRam = json.getInt("minRam"),
                minNumCores = json.getInt("minNumCores")
            )
        }
    }
}

data class BenchmarkEntry(
    val device: String,
    val chipset: String,
    val multiCoreScore: Int
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("device", device)
        json.put("chipset", chipset)
        json.put("multiCoreScore", multiCoreScore)
        return json.toString()
    }

    companion object {
        fun fromString(str: String): BenchmarkEntry {
            val json = JSONObject(str)
            return BenchmarkEntry(
                device = json.getString("device"),
                chipset = json.getString("chipset"),
                multiCoreScore = json.getInt("multiCoreScore")
            )
        }
    }
}

data class DeviceTierConfig(
    val tier1: TierConfig,
    val tier2: TierConfig,
    val historicalBenchmarks: List<BenchmarkEntry>
) {
    override fun toString(): String {
        val json = JSONObject()
        val tierConfig = JSONObject()
        tierConfig.put("tier_1", JSONObject().apply {
            put("min_multi_core_score", tier1.minMultiCoreScore)
            put("min_ram", tier1.minRam)
            put("min_num_cores", tier1.minNumCores)
        })
        tierConfig.put("tier_2", JSONObject().apply {
            put("min_multi_core_score", tier2.minMultiCoreScore)
            put("min_ram", tier2.minRam)
            put("min_num_cores", tier2.minNumCores)
        })
        json.put("tier_config", tierConfig)

        val benchmarksArray = JSONArray()
        historicalBenchmarks.forEach { benchmark ->
            benchmarksArray.put(JSONObject().apply {
                put("device", benchmark.device)
                put("chipset", benchmark.chipset)
                put("multi_core_score", benchmark.multiCoreScore)
            })
        }
        json.put("historical_benchmarks", benchmarksArray)

        return json.toString()
    }

    companion object {
        fun fromRawJson(jsonString: String): DeviceTierConfig {
            val json = JSONObject(jsonString)
            val tierConfig = json.getJSONObject("tier_config")
            val tier1Config = tierConfig.getJSONObject("tier_1")
            val tier2Config = tierConfig.getJSONObject("tier_2")

            val benchmarks = json.getJSONArray("historical_benchmarks")
            val benchmarksList = mutableListOf<BenchmarkEntry>()
            
            for (i in 0 until benchmarks.length()) {
                val benchmark = benchmarks.getJSONObject(i)
                benchmarksList.add(
                    BenchmarkEntry(
                        device = benchmark.getString("device"),
                        chipset = benchmark.getString("chipset"),
                        multiCoreScore = benchmark.getInt("multi_core_score")
                    )
                )
            }

            return DeviceTierConfig(
                tier1 = TierConfig(
                    minMultiCoreScore = tier1Config.getInt("min_multi_core_score"),
                    minRam = tier1Config.getInt("min_ram"),
                    minNumCores = tier1Config.getInt("min_num_cores")
                ),
                tier2 = TierConfig(
                    minMultiCoreScore = tier2Config.getInt("min_multi_core_score"), 
                    minRam = tier2Config.getInt("min_ram"),
                    minNumCores = tier2Config.getInt("min_num_cores")
                ),
                historicalBenchmarks = benchmarksList
            )
        }

        fun fromString(str: String): DeviceTierConfig {
            return fromRawJson(str)
        }
    }
}
