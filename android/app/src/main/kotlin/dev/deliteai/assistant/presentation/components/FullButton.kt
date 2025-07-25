package dev.deliteai.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.presentation.ui.theme.accent
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary

@Composable
fun FullButton(isEnabled: Boolean, onClick: (isEnabled: Boolean) -> Unit) {
    val bg = if (isEnabled) Color.Red.copy(alpha = 0.3f) else backgroundSecondary
    val icon = if (!isEnabled) Icons.AutoMirrored.Filled.ArrowForwardIos else Icons.Default.Close
    val iconTine = if (!isEnabled) accent else Color.White
    val text = (if (!isEnabled) "Enable" else "Disable") + " Agent"

    Box(
        Modifier
            .height(52.dp)
            .fillMaxWidth()
            .background(bg, shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Icon(
                icon, null, tint = iconTine, modifier =
                Modifier.size(16.dp)
            )
        }
    }
}
