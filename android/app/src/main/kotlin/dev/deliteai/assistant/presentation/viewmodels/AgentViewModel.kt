package dev.deliteai.assistant.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.deliteai.assistant.domain.models.Agent
import dev.deliteai.assistant.domain.repositories.CacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray


class AgentViewModel(
    private val application: Application
) : AndroidViewModel(application) {
    private val cacheRepository = CacheRepository(application)
    private var supportedAgents = mutableStateOf<List<Agent>>(listOf())

    init {
        loadSupportedAgents()
    }

    fun getSupportedAgents() = supportedAgents.value

    fun isAgentEnabled(id: String): Boolean {
        return cacheRepository.isAgentEnabled(id)
    }

    fun toggleAgent(id: String): Boolean {
        val currentState = isAgentEnabled(id)
        val newState = !currentState
        cacheRepository.setAgentEnabled(id, newState)
        return newState
    }

    private fun loadSupportedAgents() {
        viewModelScope.launch(Dispatchers.IO) {
            val jsonText = application.assets.open("supported_agents.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(jsonText)

            supportedAgents.value = List(jsonArray.length()) { index ->
                Agent.fromString(jsonArray.getJSONObject(index).toString())
            }
        }
    }

    fun getAgentSettings(agent: Agent): Map<String, Any> {
        val configs = cacheRepository.getAgentConfigs(agent.id)
        return mutableMapOf<String, Any>().apply {
            agent.settings.forEach {
                if (configs.containsKey(it.id) && configs[it.id] != null) {
                    this[it.id] = configs[it.id] as Any
                } else {
                    this[it.id] = it.defaultValue as Any
                }
            }
        }
    }

    fun setAgentSetting(agentId: String, settingId: String, value: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            cacheRepository.saveAgentConfigs(agentId, settingId, value)
        }
    }

}
