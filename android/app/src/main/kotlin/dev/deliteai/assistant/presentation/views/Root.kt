package dev.deliteai.assistant.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.deliteai.assistant.presentation.components.NavBar
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary
import dev.deliteai.assistant.presentation.viewmodels.MainViewModel

@Composable
fun Root(navController: NavController, mainViewModel: MainViewModel) {
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


        }

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 24.dp, horizontal = 24.dp)) {
            NavBar(navController, mainViewModel)
        }
    }
}
