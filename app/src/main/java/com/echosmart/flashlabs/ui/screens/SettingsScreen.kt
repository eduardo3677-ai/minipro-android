package com.echosmart.flashlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.echosmart.flashlabs.R
import com.echosmart.flashlabs.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    autoConnect: Boolean,
    onAutoConnectToggled: (Boolean) -> Unit,
    readIdOnConnect: Boolean,
    onReadIdToggled: (Boolean) -> Unit,
    verifyAfterWrite: Boolean,
    onVerifyToggled: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Theme Selector
            Text(stringResource(R.string.setting_theme), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = currentTheme == AppTheme.DARK_OLED,
                    onClick = { onThemeSelected(AppTheme.DARK_OLED) },
                    label = { Text(stringResource(R.string.theme_dark_oled)) }
                )
                FilterChip(
                    selected = currentTheme == AppTheme.LIGHT_PRO,
                    onClick = { onThemeSelected(AppTheme.LIGHT_PRO) },
                    label = { Text("Light Pro") }
                )
                FilterChip(
                    selected = currentTheme == AppTheme.CYBERPUNK,
                    onClick = { onThemeSelected(AppTheme.CYBERPUNK) },
                    label = { Text(stringResource(R.string.theme_cyber)) }
                )
                FilterChip(
                    selected = currentTheme == AppTheme.RETRO_AMBER,
                    onClick = { onThemeSelected(AppTheme.RETRO_AMBER) },
                    label = { Text("Retro CRT") }
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Language Selector
            Text(stringResource(R.string.setting_language), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row {
                FilterChip(
                    selected = currentLanguage == "es",
                    onClick = { onLanguageSelected("es") },
                    label = { Text(stringResource(R.string.lang_es)) }
                )
                Spacer(Modifier.width(12.dp))
                FilterChip(
                    selected = currentLanguage == "en",
                    onClick = { onLanguageSelected("en") },
                    label = { Text(stringResource(R.string.lang_en)) }
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.setting_auto_connect))
                Switch(checked = autoConnect, onCheckedChange = onAutoConnectToggled)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.setting_read_id))
                Switch(checked = readIdOnConnect, onCheckedChange = onReadIdToggled)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.setting_verify_write))
                Switch(checked = verifyAfterWrite, onCheckedChange = onVerifyToggled)
            }
        }
    }
}
