package com.echosmart.flashlabs.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echosmart.flashlabs.R
import com.echosmart.flashlabs.data.model.HexBuffer
import com.echosmart.flashlabs.data.model.XGecuChipDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isConnected: Boolean,
    statusText: String,
    selectedChip: XGecuChipDevice,
    hexBuffer: HexBuffer,
    logEntries: List<String>,
    onSelectChipClick: () -> Unit,
    onConnectClick: () -> Unit,
    onReadClick: () -> Unit,
    onWriteClick: () -> Unit,
    onBatchProgramClick: () -> Unit,
    onBlankCheckClick: () -> Unit,
    onEraseClick: () -> Unit,
    onAutoDetectClick: () -> Unit,
    onReadFusesClick: () -> Unit,
    onReadOtpClick: () -> Unit,
    onUnlockTsop48Click: () -> Unit,
    onLogicTestClick: () -> Unit,
    onLoadFileClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    onOpenDebugClick: () -> Unit,
    customVcc: Float?,
    customVpp: Float?,
    onUpdateVcc: (Float) -> Unit,
    onUpdateVpp: (Float) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenDebugClick) {
                        Icon(Icons.Default.BugReport, contentDescription = "Debug", tint = Color(0xFFFF5252))
                    }
                    IconButton(onClick = onOpenSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
            // Status Card
            AnimatedContent(targetState = isConnected, label = "ConnectionStatus") { connected ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (connected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(if (connected) Icons.Default.Usb else Icons.Default.UsbOff, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (connected) stringResource(R.string.status_connected) else stringResource(R.string.status_disconnected),
                                fontWeight = FontWeight.Bold
                            )
                            Text(statusText, style = MaterialTheme.typography.bodySmall)
                        }
                        if (!connected) {
                            Button(onClick = onConnectClick) {
                                Text(stringResource(R.string.btn_connect))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Active Chip Card
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_active_chip), style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Simularemos cargar la imagen T48SOP44.jpg desde assets/img/ basándonos en el tipo de paquete
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Chip Image",
                                modifier = Modifier.size(32.dp).padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text("${selectedChip.name} [${selectedChip.manufacturer}]", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                val displayVcc = customVcc ?: selectedChip.getVccVoltage()
                                val displayVpp = customVpp ?: selectedChip.getVppVoltage()
                                Text(
                                    "${selectedChip.getCategoryName()} | VCC: ${displayVcc}V | VPP: ${displayVpp}V | Protocol: 0x${selectedChip.protocolId.toString(16).uppercase()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    IconButton(onClick = onAutoDetectClick) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "SPI Auto-Detect")
                    }
                    Button(onClick = onSelectChipClick) {
                        Text(stringResource(R.string.btn_select_chip))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Main Actions Toolbar (Row 1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onReadClick, enabled = isConnected) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_read))
                }
                Button(onClick = onWriteClick, enabled = isConnected) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_write))
                }
                Button(onClick = onBatchProgramClick, enabled = isConnected, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                    Icon(Icons.Default.AutoMode, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Auto")
                }
                OutlinedButton(onClick = onBlankCheckClick, enabled = isConnected) {
                    Text(stringResource(R.string.btn_blank_check))
                }
                OutlinedButton(onClick = onEraseClick, enabled = isConnected) {
                    Text(stringResource(R.string.btn_erase))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Hardware Sub-Operations (Row 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = false,
                    onClick = onReadFusesClick,
                    enabled = isConnected,
                    label = { Text("Fuses / Config") },
                    leadingIcon = { Icon(Icons.Default.SettingsSuggest, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = false,
                    onClick = onReadOtpClick,
                    enabled = isConnected,
                    label = { Text("OTP User") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = false,
                    onClick = onUnlockTsop48Click,
                    enabled = isConnected,
                    label = { Text("TSOP48 Unlock") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = false,
                    onClick = onLogicTestClick,
                    enabled = isConnected,
                    label = { Text("Logic Test") },
                    leadingIcon = { Icon(Icons.Default.Rule, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.header_hex_viewer), fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = { /* TODO: Mostrar Dialog Go To Address */ }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Go to Address", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { /* TODO: Mostrar Dialog Búsqueda Hex/ASCII */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar Texto/Hex", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { /* TODO: Mostrar Dialog Opciones Guardar Dump (BIN/HEX) */ }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar Dump", tint = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = onLoadFileClick) {
                        Icon(Icons.Default.FileOpen, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cargar Dump")
                    }
                }
            }
            HexEditorScreen(buffer = hexBuffer, modifier = Modifier.height(160.dp))

            Spacer(Modifier.height(12.dp))

            Text(stringResource(R.string.header_logs), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(logEntries) { entry ->
                        Text(
                            text = entry,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
