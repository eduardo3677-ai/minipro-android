package com.echosmart.flashlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echosmart.flashlabs.data.model.HexBuffer

@Composable
fun HexEditorScreen(
    buffer: HexBuffer,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF070707),
        shape = MaterialTheme.shapes.small
    ) {
        val rows = buffer.data.toList().chunked(16)
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            itemsIndexed(rows) { index, rowBytes ->
                val offsetStr = String.format("%08X", index * 16)
                val hexStr = rowBytes.joinToString(" ") { String.format("%02X", it) }
                val asciiStr = rowBytes.map { b ->
                    val c = b.toInt() and 0xFF
                    if (c in 32..126) c.toChar() else '.'
                }.joinToString("")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                ) {
                    Text(
                        text = offsetStr,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF777777),
                        fontSize = 11.sp,
                        modifier = Modifier.width(75.dp)
                    )
                    Text(
                        text = hexStr.padEnd(48, ' '),
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = asciiStr,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        }
    }
}
