package dev.deliteai.assistant.presentation.views.agent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.deliteai.assistant.domain.models.AgentSetting
import dev.deliteai.assistant.domain.models.InputType
import dev.deliteai.assistant.presentation.components.Header
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AgentSettingsView(settings: List<AgentSetting>) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Header("Settings", "Tweak the settings to suit your needs")
        LazyColumn {
            items(settings) { setting ->
                SettingRow(setting)
            }
        }
    }
}

@Composable
private fun SettingRow(setting: AgentSetting) {
    var showDialog by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(setting.defaultValue) }
    val ctx = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(setting)
        Spacer(Modifier.width(8.dp))
        SettingTexts(setting)
        Spacer(Modifier.width(8.dp))
        SettingValue(currentValue.toString())
    }

    if (showDialog) {
        when (setting.inputType) {
            InputType.TEXT, InputType.NUMBER ->
                TextNumberDialog(
                    title = setting.name,
                    initial = currentValue.toString(),
                    isNumber = setting.inputType == InputType.NUMBER,
                    onConfirm = {
                        currentValue = it
                        Log.d("Settings", "${setting.name} = $it")
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )

            InputType.BOOL ->
                BoolDialog(
                    title = setting.name,
                    initial = currentValue == "true",
                    onConfirm = {
                        currentValue = it.toString()
                        Log.d("Settings", "${setting.name} = $it")
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )

            InputType.TIME ->
                showTimePickerDialog(ctx,
                    onResult = {
                        currentValue = it
                        Log.d("Settings", "${setting.name} = $it")
                    },
                    onDismiss = { showDialog = false }
                )

            InputType.DATE ->
                showDatePickerDialog(ctx,
                    onResult = {
                        currentValue = it
                        Log.d("Settings", "${setting.name} = $it")
                    },
                    onDismiss = { showDialog = false }
                )

            InputType.DATE_TIME ->
                showDateTimePickerDialog(ctx,
                    onResult = {
                        currentValue = it
                        Log.d("Settings", "${setting.name} = $it")
                    },
                    onDismiss = { showDialog = false }
                )
        }
    }
}

@Composable
private fun RowScope.SettingIcon(setting: AgentSetting) {
    Icon(
        imageVector = setting.icon,
        contentDescription = null,
        tint = setting.iconTint,
        modifier = Modifier.align(Alignment.Top)
    )
}

@Composable
private fun RowScope.SettingTexts(setting: AgentSetting) {
    Column(
        Modifier.weight(1f),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            setting.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            setting.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
private fun SettingValue(value: String) {
    Text(
        "$value >",
        modifier = Modifier.wrapContentWidth(),
        maxLines = 1
    )
}

@Composable
private fun TextNumberDialog(
    title: String,
    initial: String,
    isNumber: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text(title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                keyboardOptions = KeyboardOptions(keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text),
                singleLine = true
            )
        }
    )
}

@Composable
private fun BoolDialog(
    title: String,
    initial: Boolean,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(initial.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selected,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("true", "false").forEach { value ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                selected = value
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected.toBoolean()) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


private fun showTimePickerDialog(
    context: Context,
    onResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val now = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, h, m -> onResult(String.format("%02d:%02d", h, m)) },
        now.get(Calendar.HOUR_OF_DAY),
        now.get(Calendar.MINUTE),
        true
    ).apply { setOnDismissListener { onDismiss() } }.show()
}

private fun showDatePickerDialog(
    context: Context,
    onResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val cal = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, y, mo, d ->
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Calendar.getInstance().apply { set(y, mo, d) }
            onResult(fmt.format(date.time))
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).apply { setOnDismissListener { onDismiss() } }.show()
}

private fun showDateTimePickerDialog(
    context: Context,
    onResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val cal = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, y, mo, d ->
            cal.set(y, mo, d)
            TimePickerDialog(
                context,
                { _, h, m ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, m)
                    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    onResult(fmt.format(cal.time))
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).apply { setOnDismissListener { onDismiss() } }.show()
}
