package dev.deliteai.assistant.presentation.views.agent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.domain.models.Agent
import dev.deliteai.assistant.domain.models.agents
import dev.deliteai.assistant.presentation.components.Header
import dev.deliteai.assistant.presentation.components.HeroCarousel
import dev.deliteai.assistant.presentation.components.TwoTabToggle
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary
import dev.deliteai.assistant.utils.Constants
import dev.deliteai.assistant.utils.GlobalState

@Composable
fun AgentsTab() {
    val selectedTabIndex = remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundPrimary)
            .padding(horizontal = 24.dp)
    ) {
        Header("Agents Marketplace", "The agent processes notifications in the something random.")
        HeroCarousel(
            listOf(
                "https://miro.medium" +
                        ".com/v2/resize:fit:2912/format:webp/1*LxIyh8pAhZqXl3ADn_pz3A.jpeg",
                "https://s.yimg.com/ny/api/res/1.2/V7ZAoNMq5U2hSprMyECalQ--/YXBwaWQ9aGlnaGxhbmRlcjt3PTEyNDI7aD04Nzc-/https://media.zenfs.com/en/the_conversation_us_articles_815/3b7a2e30b30f79fb863a20391dbbb689"
            )
        )

        Spacer(Modifier.height(16.dp))

        TwoTabToggle("Explore", "Installed") {
            selectedTabIndex.value = it
        }
        Spacer(Modifier.height(12.dp))
        AgentGrid(agents)
    }
}

@Composable
fun ColumnScope.AgentGrid(agents: List<Agent>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(agents) {
            AgentCard(it)
        }
    }
}

@Composable
fun AgentCard(agent: Agent) {
    Column(
        Modifier
            .height(220.dp)
            .background(backgroundSecondary, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable {
                GlobalState.navController?.navigate("${Constants.VIEW.AGENT_INFO_VIEW}/${agent.toString()}")
            }
    ) {
        Box(
            Modifier
                .height(80.dp)
                .fillMaxWidth().clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painterResource(agent.image),
                null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            agent.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            agent.description, style = MaterialTheme.typography.bodySmall.copy(
                color = Color
                    .White.copy(alpha = 0.6f)
            )
        )
    }
}
