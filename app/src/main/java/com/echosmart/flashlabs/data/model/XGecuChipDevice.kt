package com.echosmart.flashlabs.data.model

/**
 * Representación completa de un dispositivo/chip de la base de datos oficial XGecu (infoic.xml).
 * Contiene todos los parámetros físicos y de protocolo para el programador T48.
 */
data class XGecuChipDevice(
    val name: String,
    val manufacturer: String,
    val type: Int,                   // 1:EEPROM, 2:MCU, 3:PLD, 4:SRAM, 6:NAND, 7:eMMC, 8:VGA
    val protocolId: Int,             // Protocol ID enviado en T48_BEGIN_TRANS byte[1]
    val variant: Int,                // Variant ID enviado en byte[2]
    val readBufferSize: Int,         // Tamaño de buffer de lectura
    val writeBufferSize: Int,        // Tamaño de buffer de escritura
    val codeMemorySize: Long,        // Tamaño de memoria Flash/Code en bytes
    val dataMemorySize: Int,         // Tamaño de EEPROM de datos en bytes
    val dataMemory2Size: Int,        // Tamaño de memoria secundaria de datos
    val pageSize: Int,               // Tamaño de página de escritura (Page Size)
    val pagesPerBlock: Int,          // Páginas por bloque
    val chipId: Long,                // Silicon Chip ID / JEDEC ID esperado
    val voltagesRaw: Int,            // Voltajes codificados (VCC / VPP)
    val pulseDelay: Int,             // Temporizado de pulso en microsegundos
    val flagsRaw: Long,              // Banderas de características del chip
    val chipInfo: Int,               // Información del chip y mapa de pines
    val pinMap: Long,                // Mapeo de pines ZIF
    val packageDetails: Long,        // Encapsulado (DIP, SOP, PLCC, TSOP, etc.)
    val configStr: String            // Configuración adicional
) {
    /**
     * Retorna la categoría traducida según el atributo "type" de XGecu.
     */
    fun getCategoryName(): String {
        return when (type) {
            1 -> "EEPROM"
            2 -> "MCU / Microcontroller"
            3 -> "PLD / GAL / CPLD"
            4 -> "SRAM Memory"
            6 -> "NAND Flash"
            7 -> "eMMC Storage"
            8 -> "VGA / HDMI ISP"
            else -> "Memory / IC"
        }
    }

    /**
     * Decodifica el voltaje VCC aproximado a partir del campo voltagesRaw.
     */
    fun getVccVoltage(): Float {
        val vccIndex = voltagesRaw and 0x0F
        return when (vccIndex) {
            0 -> 5.0f
            1 -> 3.3f
            2 -> 2.5f
            3 -> 1.8f
            else -> 3.3f
        }
    }

    /**
     * Decodifica el voltaje VPP aproximado (para EPROM/PIC/AVR).
     */
    fun getVppVoltage(): Float {
        val vppIndex = (voltagesRaw shr 4) and 0x0F
        return when (vppIndex) {
            1 -> 12.0f
            2 -> 12.5f
            3 -> 13.0f
            4 -> 14.5f
            5 -> 15.0f
            6 -> 21.0f
            else -> 0.0f
        }
    }

    override fun toString(): String {
        return "$name [$manufacturer] - ${getCategoryName()}"
    }
}
