package dev.deliteai.assistant.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.domain.models.NavItem
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary
import dev.deliteai.assistant.presentation.viewmodels.MainViewModel
import dev.deliteai.assistant.utils.Constants

@Composable
fun NavBar(mainViewModel: MainViewModel) {
    val isExpanded by mainViewModel.isNavBarVisible
    val selectedIndex by mainViewModel.selectedNavBarIndex

    val containerHeight by animateDpAsState(
        targetValue = if (isExpanded) 52.dp else 24.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded) backgroundSecondary else Color.Transparent,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = if (isExpanded) 24.dp else 0.dp)
            .fillMaxWidth()
            .height(containerHeight)
            .background(
                backgroundColor,
                shape = if (isExpanded)
                    RoundedCornerShape(8.dp)
                else
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
    ) {
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    NavItem(Icons.Default.Home, Constants.VIEWS.HOME_VIEW),
                    NavItem(Icons.Default.History, Constants.VIEWS.HISTORY_VIEW),
                    NavItem(Icons.AutoMirrored.Filled.Message, Constants.VIEWS.CHAT_VIEW),
                    NavItem(Icons.Default.Assistant, Constants.VIEWS.HOME_VIEW),
                    NavItem(Icons.Default.AccountCircle, Constants.VIEWS.HOME_VIEW)
                )
                navItems.forEachIndexed { index, item ->
                    NavBarItem(
                        icon = item.icon,
                        isSelected = selectedIndex == index
                    ) {
                        mainViewModel.switchTab(item.view)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mainViewModel.isNavBarVisible.value = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowUp,
                    contentDescription = "Show Nav Bar",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(arrowRotation),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )
    Box(
        modifier = Modifier.run {
            size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxHeight()
                .size(20.dp),
            tint = Color.White.copy(alpha = alpha)
        )
    }
}
