/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.deliteai.assistant.domain.models.Agent
import dev.deliteai.assistant.domain.models.AgentSetting
import dev.deliteai.assistant.presentation.ui.theme.NimbleEdgeChatBotTheme
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary
import dev.deliteai.assistant.presentation.viewmodels.AgentViewModel
import dev.deliteai.assistant.presentation.viewmodels.ChatViewModel
import dev.deliteai.assistant.presentation.viewmodels.HistoryViewModel
import dev.deliteai.assistant.presentation.viewmodels.MainViewModel
import dev.deliteai.assistant.presentation.views.RootView
import dev.deliteai.assistant.presentation.views.agent.AgentInfoView
import dev.deliteai.assistant.presentation.views.agent.AgentSettingsView
import dev.deliteai.assistant.utils.AudioPermissionLauncher
import dev.deliteai.assistant.utils.Constants
import dev.deliteai.assistant.utils.GlobalState
import dev.deliteai.assistant.utils.PermissionManager
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private val chatViewModel: ChatViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private val agentViewModel: AgentViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private val perms by lazy {
        PermissionManager(
            context = this,
            caller = this,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Toast Listener
        lifecycleScope.launchWhenStarted {
            mainViewModel.toastMessagesVS.collect { msg ->
                Toast.makeText(this@MainActivity.application, msg, Toast.LENGTH_LONG).show()
            }
        }

        mainViewModel.triggerInAppReview(this)
        GlobalState.perms = perms

        setContent {
            NimbleEdgeChatBotTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = backgroundPrimary,
                        darkIcons = false
                    )
                    systemUiController.setNavigationBarColor(
                        color = backgroundPrimary,
                        darkIcons = false
                    )
                }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundPrimary)
                ) { innerPadding ->
                    Router(
                        modifier = Modifier
                            .background(backgroundPrimary)
                            .padding(innerPadding),
                        historyViewModel = historyViewModel,
                        chatViewModel = chatViewModel,
                        mainViewModel = mainViewModel,
                        agentViewModel = agentViewModel
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.cancelLLMAndClearAudioQueue()
    }
}

@Composable
fun Router(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    chatViewModel: ChatViewModel,
    historyViewModel: HistoryViewModel,
    agentViewModel: AgentViewModel
) {
    AudioPermissionLauncher(mainViewModel)

    LaunchedEffect(Unit) {
//        mainViewModel.initializeApplication()
    }


//    if (mainViewModel.isP0LoadingVS.value && mainViewModel.blockedUsageMessageVS.value == null) {
//        Box(
//            Modifier
//                .background(backgroundPrimary)
//                .fillMaxSize()
//        ) {
//            CircularProgressIndicator(Modifier.align(Alignment.Center), color = accentHigh1)
//        }
//    } else if (mainViewModel.blockedUsageMessageVS.value != null) {
//        NoAccessView(mainViewModel.blockedUsageMessageVS.value!!)
//    } else if (mainViewModel.isFirstBootVS.value) {
//        IntroductionView(modifier = modifier) {
//            mainViewModel.registerUserFirstBoot()
//        }
//    } else if (!mainViewModel.isNimbleNetReadyVS.value) {
//        InitStatusView(mainViewModel.copyStatusVS.value, mainViewModel.copyProgressVS.value)
//    } else {
    val navController = rememberNavController()
    GlobalState.navController = navController

    NavHost(
        navController = navController,
        startDestination = Constants.VIEW.ROOT_VIEW.toString(),
        modifier = modifier
    ) {
        composable(Constants.VIEW.ROOT_VIEW.toString()) {
            RootView(
                mainViewModel = mainViewModel,
                historyViewModel = historyViewModel,
                chatViewModel = chatViewModel,
                agentViewModel = agentViewModel
            )
        }
        composable("${Constants.VIEW.AGENT_INFO_VIEW}/{agentData}") { backStackEntry ->
            val agentDataString = backStackEntry.arguments?.getString("agentData")!!
            val agent = Agent.fromString(agentDataString)
            AgentInfoView(agent = agent, agentViewModel = agentViewModel)
        }
        composable("${Constants.VIEW.AGENT_SETTINGS_VIEW}/{agentData}") { backStackEntry ->
            val agentDataString = backStackEntry.arguments?.getString("agentData")!!
            val agent = Agent.fromString(agentDataString)
            AgentSettingsView(agent = agent, agentViewModel = agentViewModel)
        }
    }
//    }
}
