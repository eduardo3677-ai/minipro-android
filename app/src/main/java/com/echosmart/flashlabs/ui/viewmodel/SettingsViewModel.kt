package com.echosmart.flashlabs.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.echosmart.flashlabs.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _theme = MutableStateFlow(AppTheme.DARK_OLED)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _language = MutableStateFlow("es") // "es" or "en"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _autoConnect = MutableStateFlow(true)
    val autoConnect: StateFlow<Boolean> = _autoConnect.asStateFlow()

    private val _readIdOnConnect = MutableStateFlow(true)
    val readIdOnConnect: StateFlow<Boolean> = _readIdOnConnect.asStateFlow()

    private val _verifyAfterWrite = MutableStateFlow(true)
    val verifyAfterWrite: StateFlow<Boolean> = _verifyAfterWrite.asStateFlow()

    private val _fillByte = MutableStateFlow("FF")
    val fillByte: StateFlow<String> = _fillByte.asStateFlow()

    private val _usbTimeoutMs = MutableStateFlow(5000)
    val usbTimeoutMs: StateFlow<Int> = _usbTimeoutMs.asStateFlow()

    fun setTheme(newTheme: AppTheme) {
        _theme.value = newTheme
    }

    fun setLanguage(newLang: String) {
        _language.value = newLang
    }

    fun toggleAutoConnect(value: Boolean) {
        _autoConnect.value = value
    }

    fun toggleReadIdOnConnect(value: Boolean) {
        _readIdOnConnect.value = value
    }

    fun toggleVerifyAfterWrite(value: Boolean) {
        _verifyAfterWrite.value = value
    }

    fun setFillByte(hexByte: String) {
        _fillByte.value = hexByte
    }

    fun setUsbTimeout(timeout: Int) {
        _usbTimeoutMs.value = timeout
    }
}
