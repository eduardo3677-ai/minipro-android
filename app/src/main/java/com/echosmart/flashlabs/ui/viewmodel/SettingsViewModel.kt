package com.echosmart.flashlabs.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.echosmart.flashlabs.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("flashlabs_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(AppTheme.valueOf(prefs.getString("theme", AppTheme.DARK_OLED.name) ?: AppTheme.DARK_OLED.name))
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString("language", "es") ?: "es")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _autoConnect = MutableStateFlow(prefs.getBoolean("auto_connect", true))
    val autoConnect: StateFlow<Boolean> = _autoConnect.asStateFlow()

    private val _readIdOnConnect = MutableStateFlow(prefs.getBoolean("read_id_on_connect", true))
    val readIdOnConnect: StateFlow<Boolean> = _readIdOnConnect.asStateFlow()

    private val _verifyAfterWrite = MutableStateFlow(prefs.getBoolean("verify_after_write", true))
    val verifyAfterWrite: StateFlow<Boolean> = _verifyAfterWrite.asStateFlow()

    private val _fillByte = MutableStateFlow(prefs.getString("fill_byte", "FF") ?: "FF")
    val fillByte: StateFlow<String> = _fillByte.asStateFlow()

    private val _usbTimeoutMs = MutableStateFlow(prefs.getInt("usb_timeout_ms", 5000))
    val usbTimeoutMs: StateFlow<Int> = _usbTimeoutMs.asStateFlow()

    fun setTheme(newTheme: AppTheme) {
        _theme.value = newTheme
        prefs.edit().putString("theme", newTheme.name).apply()
    }

    fun setLanguage(newLang: String) {
        _language.value = newLang
        prefs.edit().putString("language", newLang).apply()
    }

    fun toggleAutoConnect(value: Boolean) {
        _autoConnect.value = value
        prefs.edit().putBoolean("auto_connect", value).apply()
    }

    fun toggleReadIdOnConnect(value: Boolean) {
        _readIdOnConnect.value = value
        prefs.edit().putBoolean("read_id_on_connect", value).apply()
    }

    fun toggleVerifyAfterWrite(value: Boolean) {
        _verifyAfterWrite.value = value
        prefs.edit().putBoolean("verify_after_write", value).apply()
    }

    fun setFillByte(hexByte: String) {
        _fillByte.value = hexByte
        prefs.edit().putString("fill_byte", hexByte).apply()
    }

    fun setUsbTimeout(timeout: Int) {
        _usbTimeoutMs.value = timeout
        prefs.edit().putInt("usb_timeout_ms", timeout).apply()
    }
}
