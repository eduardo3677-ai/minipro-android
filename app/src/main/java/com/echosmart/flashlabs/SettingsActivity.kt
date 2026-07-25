package com.echosmart.flashlabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.echosmart.flashlabs.ui.screens.SettingsScreen
import com.echosmart.flashlabs.ui.theme.FlashLabsTheme
import com.echosmart.flashlabs.ui.viewmodel.SettingsViewModel

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsViewModel = SettingsViewModel()

        setContent {
            val theme by settingsViewModel.theme.collectAsState()
            val language by settingsViewModel.language.collectAsState()
            val autoConnect by settingsViewModel.autoConnect.collectAsState()
            val readIdOnConnect by settingsViewModel.readIdOnConnect.collectAsState()
            val verifyAfterWrite by settingsViewModel.verifyAfterWrite.collectAsState()

            FlashLabsTheme(theme = theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        currentTheme = theme,
                        onThemeSelected = { settingsViewModel.setTheme(it) },
                        currentLanguage = language,
                        onLanguageSelected = { settingsViewModel.setLanguage(it) },
                        autoConnect = autoConnect,
                        onAutoConnectToggled = { settingsViewModel.toggleAutoConnect(it) },
                        readIdOnConnect = readIdOnConnect,
                        onReadIdToggled = { settingsViewModel.toggleReadIdOnConnect(it) },
                        verifyAfterWrite = verifyAfterWrite,
                        onVerifyToggled = { settingsViewModel.toggleVerifyAfterWrite(it) },
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}
