package dev.deliteai.assistant.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.deliteai.assistant.domain.models.Agent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray


class AgentViewModel(private val application: Application) : AndroidViewModel(application) {
    private var supportedAgents = mutableStateOf<List<Agent>>(listOf())

    init {
        loadSupportedAgents()
    }

    fun getSupportedAgents() = supportedAgents.value

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

}
