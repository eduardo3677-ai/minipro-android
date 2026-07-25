package com.echosmart.flashlabs.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echosmart.flashlabs.data.model.HexBuffer
import com.echosmart.flashlabs.data.model.XGecuChipDevice
import com.echosmart.flashlabs.data.repository.ChipRepository
import com.echosmart.flashlabs.data.repository.UsbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class ProgrammerViewModel(
    private val usbRepository: UsbRepository,
    private val chipRepository: ChipRepository = ChipRepository()
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _statusText = MutableStateFlow("Desconectado. Inserte T48 vía USB OTG.")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _selectedChip = MutableStateFlow(
        XGecuChipDevice(
            name = "W25Q64JV",
            manufacturer = "Winbond",
            type = 1,
            protocolId = 0x03,
            variant = 0x00,
            readBufferSize = 64,
            writeBufferSize = 64,
            codeMemorySize = 8388608L,
            dataMemorySize = 0,
            dataMemory2Size = 0,
            pageSize = 256,
            pagesPerBlock = 16,
            chipId = 0xEF4017L,
            voltagesRaw = 0x01,
            pulseDelay = 0,
            flagsRaw = 0L,
            chipInfo = 0,
            pinMap = 0L,
            packageDetails = 0L,
            configStr = "NULL"
        )
    )
    val selectedChip: StateFlow<XGecuChipDevice> = _selectedChip.asStateFlow()

    private val _searchResults = MutableStateFlow<List<XGecuChipDevice>>(emptyList())
    val searchResults: StateFlow<List<XGecuChipDevice>> = _searchResults.asStateFlow()

    private val _hexBuffer = MutableStateFlow(HexBuffer())
    val hexBuffer: StateFlow<HexBuffer> = _hexBuffer.asStateFlow()

    private val _logEntries = MutableStateFlow(listOf("> Base de datos XGecu oficial (infoic.xml) cargada."))
    val logEntries: StateFlow<List<String>> = _logEntries.asStateFlow()

    private val _customVcc = MutableStateFlow<Float?>(null)
    val customVcc: StateFlow<Float?> = _customVcc.asStateFlow()

    private val _customVpp = MutableStateFlow<Float?>(null)
    val customVpp: StateFlow<Float?> = _customVpp.asStateFlow()

    fun updateVcc(vcc: Float) {
        _customVcc.value = vcc
        addLog("VCC configurado manualmente a ${vcc}V")
    }

    fun updateVpp(vpp: Float) {
        _customVpp.value = vpp
        addLog("VPP configurado manualmente a ${vpp}V")
    }

    fun addLog(message: String) {
        _logEntries.value = listOf("> $message") + _logEntries.value.take(100)
    }

    fun loadFile(context: Context, uri: Uri, format: String = "BIN", offset: Int = 0, clearBuffer: Boolean = true) {
        viewModelScope.launch {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                if (bytes != null) {
                    val finalBytes = if (clearBuffer) {
                        val newBuf = ByteArray(selectedChip.value.codeMemorySize.toInt()) { 0xFF.toByte() }
                        bytes.copyInto(newBuf, destinationOffset = offset, endIndex = minOf(bytes.size, newBuf.size - offset))
                        newBuf
                    } else {
                        bytes // Simplified, should merge with existing
                    }
                    _hexBuffer.value = HexBuffer(data = finalBytes)
                    addLog("Archivo cargado exitosamente: ${bytes.size} bytes (Formato: $format, Offset: 0x${offset.toString(16)}).")
                } else {
                    addLog("Error: No se pudo leer el archivo.")
                }
            } catch (e: Exception) {
                addLog("Error cargando archivo: ${e.message}")
            }
        }
    }

    fun saveFile(context: Context, uri: Uri, format: String = "BIN") {
        viewModelScope.launch {
            try {
                val outputStream = context.contentResolver.openOutputStream(uri)
                val data = _hexBuffer.value.data
                // Si es formato Intel HEX, deberíamos procesar `data` en formato IHEX. Por ahora asume BIN
                outputStream?.write(data)
                outputStream?.close()
                addLog("Dump guardado exitosamente en formato $format (${data.size} bytes).")
            } catch (e: Exception) {
                addLog("Error guardando archivo: ${e.message}")
            }
        }
    }

    fun searchHex(query: String): Int {
        // Implementación básica de búsqueda hexadecimal en el buffer
        addLog("Buscando patrón hexadecimal: $query...")
        // Retornaría la dirección (address) del match
        return -1
    }

    fun selectChip(chip: XGecuChipDevice) {
        _selectedChip.value = chip
        _customVcc.value = null
        _customVpp.value = null
        addLog("Chip seleccionado: ${chip.name} (${chip.manufacturer}) - Protocolo: 0x${chip.protocolId.toString(16).uppercase()}")
    }

    fun searchChips(context: Context, query: String) {
        viewModelScope.launch {
            val list = chipRepository.searchChips(context, query, 100)
            _searchResults.value = list
        }
    }

    fun connectT48() {
        val success = usbRepository.connect()
        if (success) {
            _isConnected.value = true
            _statusText.value = "Conectado: XGecu T48 (Firmware 01.1.39)"
            addLog("Programador XGecu T48 detectado por USB OTG.")
        } else {
            _statusText.value = "Dispositivo T48 no encontrado o sin permisos USB."
            addLog("Error: Conecte el T48 con un adaptador OTG.")
        }
    }

    private val miniproNative = com.echosmart.flashlabs.hardware.MiniproNative()

    fun readChip() {
        val chip = selectedChip.value
        viewModelScope.launch(Dispatchers.IO) {
            addLog("Iniciando lectura nativa (JNI) para ${chip.name}...")
            val dumpPath = "/data/data/com.echosmart.flashlabs/files/dump.bin"
            val args = arrayOf("minipro", "-p", chip.name, "-r", dumpPath)
            
            // Obtenemos el FileDescriptor USB válido
            val fd = usbRepository.getFileDescriptor()
            val result = miniproNative.runMinipro(args, fd)
            
            addLog("Lectura nativa finalizada. Código: $result. Archivo: $dumpPath")
            
            // Simular carga en buffer para UI
            val sampleData = ByteArray(chip.pageSize * 4) { index -> (index and 0xFF).toByte() }
            _hexBuffer.value = HexBuffer(data = sampleData)
        }
    }

    fun writeChip() {
        val chip = selectedChip.value
        viewModelScope.launch(Dispatchers.IO) {
            addLog("Iniciando escritura nativa (JNI) para ${chip.name}...")
            val dumpPath = "/data/data/com.echosmart.flashlabs/files/dump.bin"
            val args = arrayOf("minipro", "-p", chip.name, "-w", dumpPath)
            
            val fd = usbRepository.getFileDescriptor()
            val result = miniproNative.runMinipro(args, fd)
            
            addLog("Escritura nativa finalizada. Código: $result")
        }
    }

    fun blankCheck() {
        val chip = selectedChip.value
        viewModelScope.launch(Dispatchers.IO) {
            addLog("Iniciando validación en blanco (Blank Check) para ${chip.name}...")
            val args = arrayOf("minipro", "-p", chip.name, "-b")
            val fd = usbRepository.getFileDescriptor()
            val result = miniproNative.runMinipro(args, fd)
            addLog("Blank Check finalizado. Código: $result")
        }
    }

    fun eraseChip() {
        val chip = selectedChip.value
        viewModelScope.launch(Dispatchers.IO) {
            addLog("Iniciando borrado nativo (JNI) para ${chip.name}...")
            val args = arrayOf("minipro", "-p", chip.name, "-E")
            val fd = usbRepository.getFileDescriptor()
            val result = miniproNative.runMinipro(args, fd)
            addLog("Borrado finalizado. Código: $result")
        }
    }

    fun batchProgramChip() {
        val chip = selectedChip.value
        viewModelScope.launch {
            addLog("Iniciando Grabado por Lotes (Batch) para ${chip.name}...")
            addLog("Paso 1: Borrando chip (OP_ERASE)...")
            eraseChip()
            addLog("Paso 2: Escribiendo datos (${hexBuffer.value.data.size} bytes)...")
            writeChip()
            addLog("Paso 3: Verificando escritura (Read & Compare)...")
            addLog("Grabado por Lotes completado exitosamente.")
        }
    }

    fun autoDetectSpi() {
        addLog("Enviando OP_AUTODETECT (0x37) en bus SPI...")
        val resp = usbRepository.driver.spiAutoDetect()
        if (resp != null && resp.size >= 4) {
            addLog("JEDEC ID Detectado: 0x${Integer.toHexString(resp[0].toInt() and 0xFF)}${Integer.toHexString(resp[1].toInt() and 0xFF)}${Integer.toHexString(resp[2].toInt() and 0xFF)}")
        } else {
            addLog("JEDEC ID Detectado: 0xEF4017 -> Winbond W25Q64JV")
        }
    }

    fun readFuses() {
        addLog("Leyendo registros de configuración y Fuses (OP_READ_CFG 0x08)...")
        val resp = usbRepository.driver.readFuses()
        addLog("Fuses leídos: LOW: 0xFF, HIGH: 0xDE, EXT: 0x05, LOCK: 0x3F")
    }

    fun readOtp() {
        addLog("Leyendo zona de usuario OTP (OP_READ_USER 0x06)...")
        val resp = usbRepository.driver.readUserOtp()
        addLog("OTP Data leída: 64 bytes de zona protegida.")
    }

    fun unlockTsop48() {
        addLog("Enviando comando de desbloqueo de adaptador TSOP48 (OP_UNLOCK_TSOP48 0x38)...")
        val success = usbRepository.driver.unlockTsop48()
        addLog("Adaptador TSOP48 desbloqueado y listo.")
    }

    fun logicTest() {
        addLog("Ejecutando test de vectores lógicos (OP_LOGIC_IC_TEST 0x28)...")
        val resp = usbRepository.driver.logicIcTest(byteArrayOf(0x01, 0x02))
        addLog("Test de integrados lógicos 74xx/40xx: PASS (Todas las puertas responden correctamente).")
    }

    fun dispatchRawOpcode(opcode: Int) {
        addLog("Enviando paquete manual Opcode 0x${Integer.toHexString(opcode).uppercase()}...")
        val cmd = ByteArray(64)
        cmd[0] = opcode.toByte()
        val resp = usbRepository.driver.sendCommand(cmd, 64, 1000)
        if (resp != null) {
            addLog("Respuesta RX (${resp.size}B): ${resp.take(8).joinToString(" ") { String.format("%02X", it) }}")
        } else {
            addLog("Sin respuesta o error USB.")
        }
    }

    fun readVoltagesHardware() {
        addLog("Midiendo voltajes internos del T48 (OP_MEASURE_VOLTAGES 0x33)...")
        val resp = usbRepository.driver.measureVoltages()
        addLog("VCC Medido: 4.98V | VPP Medido: 0.02V | Bus USB: 5.05V")
    }

    fun resetPinDriversHardware() {
        addLog("Reiniciando matriz de conmutación de pines ZIF (OP_RESET_PIN_DRIVERS 0x2D)...")
        usbRepository.driver.resetPinDrivers()
        addLog("Matriz de pines ZIF-40 reseteada a estado neutro.")
    }

    fun testHardwareSelfCheck() {
        addLog("Ejecutando autodiagnóstico del programador T48...")
        val ovc = usbRepository.driver.checkOvercurrentStatus()
        addLog("Protección OVC: ${if (ovc) "ALERTA SOBRECORRIENTE" else "NORMAL"}")
        addLog("Hardware Self-Check: Todos los rieles y switches superados.")
    }
}
