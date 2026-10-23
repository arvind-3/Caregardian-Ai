package com.example.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import com.example.ui.theme.*

@Composable
fun MedicationReminderDialog(
    medication: Medication?,
    preferredName: String,
    statusMessage: String?,
    onAction: (MedicationStatus, String, String) -> Unit,
    onVoiceInput: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (medication == null) return
    var textInput by remember { mutableStateOf("") }
    var isMicActive by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("medication_reminder_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Surface(
                    color = CareAmberLight,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Alarm,
                            contentDescription = "Alarm",
                            tint = CareAmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "MEDICINE TIME",
                            fontWeight = FontWeight.Bold,
                            color = CareAmberWarning,
                            fontSize = 13.sp
                        )
                    }
                }

                Text(
                    text = "Namaste $preferredName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Aapki ${medication.name} lene ka time ho gaya hai.",
                    fontSize = 17.sp,
                    color = CareTextSecondary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Kya aapne dawai le li?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CareBluePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Medication Details Pill
                Surface(
                    color = CareBlueLight.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = CareBluePrimary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = medication.name,
                                fontWeight = FontWeight.Bold,
                                color = CareTextPrimary,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Dosage: ${medication.dosage} • ${medication.scheduledTime}",
                            fontSize = 14.sp,
                            color = CareTextSecondary,
                            modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                        )
                        if (medication.instructions.isNotBlank()) {
                            Text(
                                text = "Instructions: ${medication.instructions}",
                                fontSize = 13.sp,
                                color = CareTextSecondary,
                                modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // AI Status feedback if voice processed
                if (!statusMessage.isNullOrBlank()) {
                    Surface(
                        color = CareGreenLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = CareGreenSuccess)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = statusMessage,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF064E3B)
                            )
                        }
                    }
                }

                // Voice Response Section
                Text(
                    text = "🎙 Speak or Tap Your Answer",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Voice Prompt Suggestion Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    VoiceChip(
                        text = "\"Haan beta, maine le li.\"",
                        icon = Icons.Default.CheckCircle,
                        tint = CareGreenSuccess
                    ) {
                        onVoiceInput("Haan beta, maine le li")
                    }
                    VoiceChip(
                        text = "\"Thodi der baad lunga.\"",
                        icon = Icons.Default.Schedule,
                        tint = CareAmberWarning
                    ) {
                        onVoiceInput("Thodi der baad lunga")
                    }
                    VoiceChip(
                        text = "\"Nahi abhi nahi li.\"",
                        icon = Icons.Default.Cancel,
                        tint = CareRedAlert
                    ) {
                        onVoiceInput("Nahi abhi nahi li")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Primary Large Action Buttons
                Button(
                    onClick = {
                        onAction(
                            MedicationStatus.TAKEN,
                            "BUTTON",
                            "Confirmed taken via primary button"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareGreenSuccess),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_yes_took_it")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "YES, I TOOK IT",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onAction(
                                MedicationStatus.DELAYED,
                                "BUTTON",
                                "Postponed reminder via Later button"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_later")
                    ) {
                        Text("LATER", fontWeight = FontWeight.SemiBold, color = CareAmberWarning)
                    }

                    OutlinedButton(
                        onClick = {
                            onAction(
                                MedicationStatus.MISSED,
                                "BUTTON",
                                "Marked missed via No button"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_no")
                    ) {
                        Text("NO", fontWeight = FontWeight.SemiBold, color = CareRedAlert)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = CareTextPrimary
            )
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Default.Mic,
                contentDescription = "Speak",
                tint = CareBluePrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
