package com.example.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun EmergencyDialog(
    preferredName: String,
    emergencyPhone: String,
    caregiverName: String,
    caregiverPhone: String,
    onEmergencyTriggered: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("emergency_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = CareRedLight,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = CareRedAlert,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(44.dp)
                    )
                }

                Text(
                    text = "⚠️ Possible Emergency",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareRedAlert,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$preferredName, please seek immediate professional or emergency medical assistance.",
                    fontSize = 15.sp,
                    color = CareTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Text(
                    text = "The AI does not diagnose and cannot replace an ambulance. An urgent alert will also be dispatched to your family.",
                    fontSize = 12.sp,
                    color = CareTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Call Emergency
                Button(
                    onClick = {
                        onEmergencyTriggered()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareRedAlert),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_call_emergency")
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "CALL EMERGENCY ($emergencyPhone)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Call Caregiver
                OutlinedButton(
                    onClick = {
                        onEmergencyTriggered()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_call_caregiver")
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = CareBluePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "CALL CAREGIVER ($caregiverName)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CareBluePrimary
                    )
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_cancel_emergency")
                ) {
                    Text("CANCEL / FALSE ALARM", color = CareTextSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
