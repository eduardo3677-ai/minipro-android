package com.echosmart.flashlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.echosmart.flashlabs.R

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Usb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.onboarding_req_1))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.onboarding_req_2))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SdCard, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.onboarding_req_3))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.btn_get_started), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
