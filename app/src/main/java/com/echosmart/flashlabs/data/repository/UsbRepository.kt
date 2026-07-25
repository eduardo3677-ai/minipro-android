package com.echosmart.flashlabs.data.repository

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.echosmart.flashlabs.hardware.T48UsbDriver

class UsbRepository(private val usbManager: UsbManager) {

    val driver = T48UsbDriver(usbManager)

    fun findT48Device(): UsbDevice? {
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (driver.isT48Device(device)) {
                return device
            }
        }
        return null
    }

    fun connect(): Boolean {
        val device = findT48Device() ?: return false
        return driver.connect(device)
    }

    fun disconnect() {
        driver.disconnect()
    }

    fun isConnected(): Boolean {
        return driver.isConnected
    }

    fun getFileDescriptor(): Int {
        return driver.fileDescriptor
    }
}
