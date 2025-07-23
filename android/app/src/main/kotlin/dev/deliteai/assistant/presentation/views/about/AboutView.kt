package dev.deliteai.assistant.presentation.views.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.deliteai.assistant.R
import dev.deliteai.assistant.presentation.ui.theme.accent
import dev.deliteai.assistant.presentation.ui.theme.accentLow1
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary


// Click handlers defined as private functions
private fun handleDiscord() {
    // TODO: implement Discord action
}

private fun handleGithub() {
    // TODO: implement GitHub action
}

private fun handleGmail() {
    // TODO: implement Gmail action
}

private fun handlePrivacyLink() {
    // TODO: implement Privacy Policy action
}

@Composable
fun AboutView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundPrimary)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp, horizontal = 24.dp)
        ) {
            LogoSection()
            Spacer(modifier = Modifier.height(16.dp))
            TitleSection()
            Spacer(modifier = Modifier.height(4.dp))
            DescriptionSection()
            SocialMediaSection(
                listOf(
                    SocialMediaItem(
                        icon = painterResource(R.drawable.ic_discord),
                        description = "Discord",
                        backgroundColor = Color(0xFF7289DA),
                        onClick = ::handleDiscord
                    ),
                    SocialMediaItem(
                        icon = painterResource(R.drawable.ic_github),
                        description = "GitHub",
                        backgroundColor = Color(0xFF181717),
                        onClick = ::handleGithub
                    ),
                    SocialMediaItem(
                        icon = painterResource(R.drawable.ic_gmail),
                        description = "Gmail",
                        backgroundColor = Color(0xFFD93025),
                        onClick = ::handleGmail
                    )
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            FooterSection(::handlePrivacyLink)
            Spacer(Modifier.height(52.dp))
        }
    }
}

@Composable
private fun LogoSection() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(accentLow1, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_ne_new),
            contentDescription = "App logo",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun TitleSection() {
    Text(
        text = "NimbleEdge AI",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
        color = Color.White
    )
}

@Composable
private fun DescriptionSection() {
    Text(
        text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

private data class SocialMediaItem(
    val icon: Painter,
    val description: String,
    val backgroundColor: Color,
    val onClick: () -> Unit
)

@Composable
private fun SocialMediaSection(items: List<SocialMediaItem>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(item.backgroundColor, RoundedCornerShape(8.dp))
                    .clickable(onClick = item.onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = item.icon,
                    contentDescription = item.description,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FooterSection(onPrivacyClick: () -> Unit) {
    val privacyText = buildAnnotatedString {
        append("Click here to checkout our ")
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = accent)) {
            append("Privacy Policy")
        }
        pop()
    }

    Text(
        text = privacyText,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPrivacyClick)
    )
}
