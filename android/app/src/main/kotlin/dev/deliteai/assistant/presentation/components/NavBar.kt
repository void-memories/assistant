package dev.deliteai.assistant.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.deliteai.assistant.domain.models.NavItem
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary
import dev.deliteai.assistant.presentation.viewmodels.MainViewModel
import dev.deliteai.assistant.utils.Constants

@Composable
fun NavBar(navController: NavController, mainViewModel: MainViewModel) {
    val selectedIndex by mainViewModel.selectedNavBarIndex

    val navItems = listOf(
        NavItem(Icons.Default.Home, Constants.VIEWS.HOME_VIEW),
        NavItem(Icons.Default.History, Constants.VIEWS.HOME_VIEW),
        NavItem(Icons.AutoMirrored.Filled.Message, Constants.VIEWS.HOME_VIEW),
        NavItem(Icons.Default.Assistant, Constants.VIEWS.HOME_VIEW),
        NavItem(Icons.Default.AccountCircle, Constants.VIEWS.HOME_VIEW),
    )

    fun navigateTo(index: Int, view: Constants.VIEWS) {
        mainViewModel.selectedNavBarIndex.intValue = index
        navController.navigate(view.str)
    }

    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(backgroundSecondary, shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                NavBarItem(
                    icon = item.icon,
                    isSelected = selectedIndex == index
                ) { navigateTo(index, item.view) }
            }
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxHeight()
            .size(20.dp),
        tint = Color.White.copy(alpha = if (isSelected) 1f else 0.5f)
    )
}
