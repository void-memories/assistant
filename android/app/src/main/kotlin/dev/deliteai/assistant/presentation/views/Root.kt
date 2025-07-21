package dev.deliteai.assistant.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.presentation.components.NavBar
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary
import dev.deliteai.assistant.presentation.viewmodels.ChatViewModel
import dev.deliteai.assistant.presentation.viewmodels.HistoryViewModel
import dev.deliteai.assistant.presentation.viewmodels.MainViewModel
import dev.deliteai.assistant.presentation.views.history.HistoryView
import dev.deliteai.assistant.presentation.views.home.HomeView

@Composable
fun Root(
    mainViewModel: MainViewModel,
    historyViewModel: HistoryViewModel,
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .background(backgroundPrimary)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            when (mainViewModel.selectedNavBarIndex.intValue) {
                0 -> HomeView()
                1 -> HistoryView(historyViewModel)
                else -> ChatView(
                    chatViewModel,
                    isNavBarVisible = mainViewModel.isNavBarVisible.value
                )
            }

        }

        Box(
            Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavBar(mainViewModel)
        }
    }
}
