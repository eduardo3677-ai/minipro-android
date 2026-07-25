package com.echosmart.flashlabs.hardware

import android.system.Os
import java.io.File

class MiniproNative {
    init {
        System.loadLibrary("minipro")
    }

    fun setupEnvironment(filesDir: String) {
        Os.setenv("MINIPRO_HOME", filesDir, true)
    }

    external fun runMinipro(args: Array<String>, usbFd: Int): Int
}
