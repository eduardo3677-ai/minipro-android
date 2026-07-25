package com.echosmart.flashlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.echosmart.flashlabs.R
import com.echosmart.flashlabs.ui.viewmodel.ProgrammerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugPanelScreen(
    viewModel: ProgrammerViewModel,
    onBackClick: () -> Unit
) {
    var opcodeInput by remember { mutableStateOf("05") }
    val isConnected by viewModel.isConnected.collectAsState()
    val logEntries by viewModel.logEntries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFFF5252))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.debug_title))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E2C)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.debug_console_title), fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = opcodeInput,
                            onValueChange = { opcodeInput = it },
                            label = { Text(stringResource(R.string.debug_opcode_label)) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val opcode = opcodeInput.toIntOrNull(16) ?: 5
                                viewModel.dispatchRawOpcode(opcode)
                            },
                            enabled = isConnected
                        ) {
                            Text(stringResource(R.string.debug_btn_send))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Quick Debug Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { viewModel.readVoltagesHardware() }, enabled = isConnected) {
                    Icon(Icons.Default.ElectricMeter, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.debug_btn_voltages))
                }
                OutlinedButton(onClick = { viewModel.resetPinDriversHardware() }, enabled = isConnected) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.debug_btn_reset_pins))
                }
                OutlinedButton(onClick = { viewModel.testHardwareSelfCheck() }, enabled = isConnected) {
                    Text(stringResource(R.string.debug_btn_self_check))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.debug_log_title), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Black,
                shape = MaterialTheme.shapes.small
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(logEntries) { log ->
                        Text(
                            text = log,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00FF66),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
