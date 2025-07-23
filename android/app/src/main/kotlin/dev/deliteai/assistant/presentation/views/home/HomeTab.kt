/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.assistant.presentation.views.home

import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.presentation.components.Header
import dev.deliteai.assistant.presentation.components.HeroCarousel
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary

@Composable
fun HomeTab() {
    val application = LocalContext.current.applicationContext as Application

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(backgroundPrimary)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                "Hey there,", "Have a pleasant morning \uD83C\uDF1E"
            )

            HeroCarousel(
                listOf(
                    "https://miro.medium.com/v2/resize:fit:1100/format:webp/0*ruv_Cl3m8bvcBjr8",
                    "https://miro.medium" +
                            ".com/v2/resize:fit:720/format:webp/1*bM2PWaD-OpqsjRlirmkMpQ.png",
                    "https://miro.medium.com/v2/resize:fit:720/format:webp/0*dDJRyeKnEsxyzx9f.png"
                )
            )
        }
    }
}
