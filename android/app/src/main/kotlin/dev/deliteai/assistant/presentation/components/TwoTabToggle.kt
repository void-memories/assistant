package dev.deliteai.assistant.presentation.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.deliteai.assistant.presentation.ui.theme.accentLow1
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary

@Composable
fun TwoTabToggle(
    firstTabText: String,
    secondTabText: String,
    onChange: (Int) -> Unit,
) {
    //TODO: move to agents view model
    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = listOf(firstTabText, secondTabText)

    val unselectedText = Color.White.copy(alpha = 0.7f)
    val selectedText   = Color.White

    BoxWithConstraints(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundSecondary)
    ) {
        val fullWidth = maxWidth
        val tabWidth  = fullWidth / tabs.size

        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(accentLow1)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clickable {
                            selectedIndex = index
                            onChange(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (index == selectedIndex) selectedText else unselectedText
                    )
                }
            }
        }
    }
}
