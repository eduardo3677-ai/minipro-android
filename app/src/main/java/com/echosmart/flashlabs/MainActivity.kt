package com.echosmart.flashlabs

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echosmart.flashlabs.data.model.XGecuChipDevice
import com.echosmart.flashlabs.data.repository.UsbRepository
import com.echosmart.flashlabs.ui.screens.DebugPanelScreen
import com.echosmart.flashlabs.ui.screens.MainScreen
import com.echosmart.flashlabs.ui.screens.OnboardingScreen
import com.echosmart.flashlabs.ui.screens.SettingsScreen
import com.echosmart.flashlabs.ui.theme.FlashLabsTheme
import com.echosmart.flashlabs.ui.viewmodel.ProgrammerViewModel
import com.echosmart.flashlabs.ui.viewmodel.SettingsViewModel

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            FlashLabsApp()
        }
    }
}

@Composable
fun FlashLabsApp() {
    val context = LocalContext.current
    val usbManager = remember { context.getSystemService(Context.USB_SERVICE) as UsbManager }
    val usbRepository = remember { UsbRepository(usbManager) }
    val viewModel = remember { ProgrammerViewModel(usbRepository) }
    val settingsViewModel = remember { SettingsViewModel() }

    val isConnected by viewModel.isConnected.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val selectedChip by viewModel.selectedChip.collectAsState()
    val hexBuffer by viewModel.hexBuffer.collectAsState()
    val logEntries by viewModel.logEntries.collectAsState()

    val theme by settingsViewModel.theme.collectAsState()
    val language by settingsViewModel.language.collectAsState()
    val autoConnect by settingsViewModel.autoConnect.collectAsState()
    val readIdOnConnect by settingsViewModel.readIdOnConnect.collectAsState()
    val verifyAfterWrite by settingsViewModel.verifyAfterWrite.collectAsState()

    var isOnboardingCompleted by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf("main") } // "main", "settings", "debug"
    var showChipDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.loadFile(context, it) }
    }

    FlashLabsTheme(theme = theme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (!isOnboardingCompleted) {
                OnboardingScreen(onComplete = { isOnboardingCompleted = true })
            } else {
                AnimatedContent(
                    targetState = currentScreen,
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        "settings" -> {
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
                                onBackClick = { currentScreen = "main" }
                            )
                        }
                        "debug" -> {
                            DebugPanelScreen(
                                viewModel = viewModel,
                                onBackClick = { currentScreen = "main" }
                            )
                        }
                        else -> {
                            MainScreen(
                                isConnected = isConnected,
                                statusText = statusText,
                                selectedChip = selectedChip,
                                hexBuffer = hexBuffer,
                                logEntries = logEntries,
                                onSelectChipClick = { showChipDialog = true },
                                onConnectClick = { viewModel.connectT48() },
                                onReadClick = { viewModel.readChip() },
                                onWriteClick = { viewModel.writeChip() },
                                onBatchProgramClick = { viewModel.batchProgramChip() },
                                onBlankCheckClick = { viewModel.blankCheck() },
                                onEraseClick = { viewModel.eraseChip() },
                                onAutoDetectClick = { viewModel.autoDetectSpi() },
                                onReadFusesClick = { viewModel.readFuses() },
                                onReadOtpClick = { viewModel.readOtp() },
                                onUnlockTsop48Click = { viewModel.unlockTsop48() },
                                onLogicTestClick = { viewModel.logicTest() },
                                onLoadFileClick = { filePickerLauncher.launch("*/*") },
                                onOpenSettingsClick = { currentScreen = "settings" },
                                onOpenDebugClick = { currentScreen = "debug" },
                                customVcc = viewModel.customVcc.collectAsState().value,
                                customVpp = viewModel.customVpp.collectAsState().value,
                                onUpdateVcc = { viewModel.updateVcc(it) },
                                onUpdateVpp = { viewModel.updateVpp(it) }
                            )
                        }
                    }
                }

                if (showChipDialog) {
                    ChipSelectorXmlDialog(
                        viewModel = viewModel,
                        onDismiss = { showChipDialog = false },
                        onChipSelected = { chip ->
                            viewModel.selectChip(chip)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipSelectorXmlDialog(
    viewModel: ProgrammerViewModel,
    onDismiss: () -> Unit,
    onChipSelected: (XGecuChipDevice) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.searchChips(context, searchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Base de Datos Oficial XGecu (~30,000 Chips)") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar chip (ej. W25Q64, 24C02, GAL16V8, PIC16)...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(searchResults) { chip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onChipSelected(chip)
                                    onDismiss()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(chip.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        chip.getCategoryName(),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    "Fabricante: ${chip.manufacturer} | Protocol ID: 0x${chip.protocolId.toString(16).uppercase()}",
                                    fontSize = 12.sp
                                )
                                Text(
                                    "VCC: ${chip.getVccVoltage()}V | VPP: ${chip.getVppVoltage()}V | Tam: ${chip.codeMemorySize} B",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
