package dev.deliteai.assistant.presentation.views.agent

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.domain.models.Agent
import dev.deliteai.assistant.domain.models.PermissionItem
import dev.deliteai.assistant.presentation.components.FullButton
import dev.deliteai.assistant.presentation.ui.theme.accent
import dev.deliteai.assistant.presentation.ui.theme.backgroundPrimary
import dev.deliteai.assistant.presentation.ui.theme.backgroundSecondary
import dev.deliteai.assistant.utils.Constants
import dev.deliteai.assistant.utils.GlobalState
import dev.deliteai.assistant.utils.GlobalState.perms
import dev.deliteai.assistant.utils.isPermissionGranted
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun AgentInfoView(agent: Agent) {
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundPrimary)
            .padding(horizontal = 24.dp)
    ) {
        Image(
            painterResource(agent.image),
            null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(agent.name, style = MaterialTheme.typography.titleMedium)
            Icon(
                Icons.Default.Settings,
                null,
                tint = accent,
                modifier = Modifier.clickable {
                    val settingsJson = JSONArray()
                    agent.settings.forEach { setting ->
                        settingsJson.put(JSONObject(setting.toString()))
                    }
                    GlobalState.navController?.navigate("${Constants.VIEW.AGENT_SETTINGS_VIEW}/${settingsJson}")
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            agent.description, style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.6f)
            )
        )

        Spacer(Modifier.height(24.dp))
        Text(
            "Required Permissions", style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight =
                FontWeight.Normal
            )
        )
        Spacer(Modifier.height(16.dp))
        ScrollablePermissionRow(agent.requiredPermissions, agent.highlight)

        Spacer(Modifier.weight(1f))
        FullButton(false) {

        }
    }
}

@Composable
fun ScrollablePermissionRow(permissions: Set<PermissionItem>, highlight: Color) {
    LazyRow(Modifier.fillMaxWidth()) {
        items(permissions.toList()) {
            PermissionTile(it, highlight)
        }
    }
}

@Composable
fun PermissionTile(permission: PermissionItem, highlight: Color) {
    val application = LocalContext.current.applicationContext as Application
    val cs = rememberCoroutineScope()
    val isGranted = remember {
        mutableStateOf(application.isPermissionGranted(permission.runtimePermission))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .padding(
                end = 16.dp
            )
    ) {
        Box(
            Modifier
                .size(64.dp)
                .clickable {
                    if (!isGranted.value) {
                        cs.launch {
                            perms?.grant(permission.runtimePermission)
                            isGranted.value =
                                application.isPermissionGranted(permission.runtimePermission)
                        }
                    } else {
                        Toast
                            .makeText(
                                application,
                                "Permission already granted.",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
                .background(
                    if (!isGranted.value) backgroundSecondary else highlight, shape =
                    RoundedCornerShape(8.dp)
                )
        ) {
            Icon(permission.icon, null, modifier = Modifier.align(Alignment.Center))

        }
        Spacer(Modifier.height(8.dp))
        Text(
            permission.name.replace(" ", "\n"), style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}
