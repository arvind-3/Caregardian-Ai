package com.example.ui.screens.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.example.ui.theme.*

@Composable
fun HealthCheckInDialog(
    preferredName: String,
    onSubmit: (mood: String, sleep: String, energy: String, symptoms: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMood by remember { mutableStateOf("GOOD") }
    var selectedSleep by remember { mutableStateOf("NORMAL") }
    var selectedEnergy by remember { mutableStateOf("MEDIUM") }
    var selectedSymptoms by remember { mutableStateOf("None") }
    var customNotes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("health_checkin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color(0xFFFCE7F3),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFDB2777), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "DAILY HEALTH CHECK",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDB2777),
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "Namaste $preferredName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareTextPrimary
                )
                Text(
                    text = "Aaj aap kaisa feel kar rahe hain?",
                    fontSize = 16.sp,
                    color = CareTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // 1. Mood
                Text(
                    text = "1. How is your mood? (Aapka mood kaisa hai?)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CareTextPrimary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SelectionPill(label = "😄 Great", isSelected = selectedMood == "GREAT", modifier = Modifier.weight(1f)) {
                        selectedMood = "GREAT"
                    }
                    SelectionPill(label = "😊 Good", isSelected = selectedMood == "GOOD", modifier = Modifier.weight(1f)) {
                        selectedMood = "GOOD"
                    }
                    SelectionPill(label = "😐 Okay", isSelected = selectedMood == "OKAY", modifier = Modifier.weight(1f)) {
                        selectedMood = "OKAY"
                    }
                    SelectionPill(label = "🥱 Tired", isSelected = selectedMood == "TIRED", modifier = Modifier.weight(1f)) {
                        selectedMood = "TIRED"
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 2. Sleep
                Text(
                    text = "2. How was your sleep? (Neend kaisi aayi?)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CareTextPrimary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SelectionPill(label = "😴 Very Good", isSelected = selectedSleep == "VERY_GOOD", modifier = Modifier.weight(1f)) {
                        selectedSleep = "VERY_GOOD"
                    }
                    SelectionPill(label = "🛏️ Normal", isSelected = selectedSleep == "NORMAL", modifier = Modifier.weight(1f)) {
                        selectedSleep = "NORMAL"
                    }
                    SelectionPill(label = "🔄 Disturbed", isSelected = selectedSleep == "DISTURBED", modifier = Modifier.weight(1f)) {
                        selectedSleep = "DISTURBED"
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 3. Energy
                Text(
                    text = "3. How is your energy? (Energy level kaisa hai?)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CareTextPrimary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SelectionPill(label = "⚡ High", isSelected = selectedEnergy == "HIGH", modifier = Modifier.weight(1f)) {
                        selectedEnergy = "HIGH"
                    }
                    SelectionPill(label = "🔋 Medium", isSelected = selectedEnergy == "MEDIUM", modifier = Modifier.weight(1f)) {
                        selectedEnergy = "MEDIUM"
                    }
                    SelectionPill(label = "🪫 Low", isSelected = selectedEnergy == "LOW", modifier = Modifier.weight(1f)) {
                        selectedEnergy = "LOW"
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 4. Any symptoms?
                Text(
                    text = "4. Any unusual symptoms? (Koi takleef?)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CareTextPrimary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SelectionPill(label = "✓ None (Bilkul Theek)", isSelected = selectedSymptoms == "None", modifier = Modifier.weight(1f)) {
                            selectedSymptoms = "None"
                        }
                        SelectionPill(label = "Chakkar (Dizziness)", isSelected = selectedSymptoms == "Mild dizziness", modifier = Modifier.weight(1f)) {
                            selectedSymptoms = "Mild dizziness"
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SelectionPill(label = "Joint Pain / Stiffness", isSelected = selectedSymptoms == "Joint stiffness", modifier = Modifier.weight(1f)) {
                            selectedSymptoms = "Joint stiffness"
                        }
                        SelectionPill(label = "Headache (Sar dard)", isSelected = selectedSymptoms == "Headache", modifier = Modifier.weight(1f)) {
                            selectedSymptoms = "Headache"
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmit(selectedMood, selectedSleep, selectedEnergy, selectedSymptoms, customNotes)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareBluePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_checkin")
                ) {
                    Text("SUBMIT HEALTH CHECK-IN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(6.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = CareTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun SelectionPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) CareBluePrimary else Color(0xFFF1F5F9),
        border = if (isSelected) null else BorderStroke(1.dp, CareBorder),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else CareTextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
