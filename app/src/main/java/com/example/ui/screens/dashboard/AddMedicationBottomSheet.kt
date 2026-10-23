package com.example.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationBottomSheet(
    onAdd: (name: String, dosage: String, frequency: String, time: String, instructions: String, doctor: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 tablet") }
    var frequency by remember { mutableStateOf("Daily") }
    var scheduledTime by remember { mutableStateOf("08:00 PM") }
    var instructions by remember { mutableStateOf("After food with water") }
    var doctorName by remember { mutableStateOf("Dr. R. K. Verma") }
    var notes by remember { mutableStateOf("General maintenance") }

    var nameError by remember { mutableStateOf(false) }

    val namePresets = listOf("Atorvastatin 10mg", "Metformin 500mg", "Amlodipine 5mg", "Vitamin D3 60k")
    val dosagePresets = listOf("1 tablet", "2 tablets", "1 capsule", "5 ml syrup")
    val timePresets = listOf("08:00 AM", "01:00 PM", "08:00 PM", "09:30 PM")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("add_medication_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CareBlueLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AddBox,
                                contentDescription = null,
                                tint = CareBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Add New Medication",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CareTextPrimary
                        )
                        Text(
                            text = "Set reminders and authorized dosage",
                            fontSize = 12.sp,
                            color = CareTextSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(18.dp))

            // 1. Medication Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Medication Name *") },
                placeholder = { Text("e.g. Atorvastatin 10mg") },
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Please enter a medication name", color = CareRedAlert)
                    }
                },
                leadingIcon = {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = CareBluePrimary)
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sheet_med_name")
            )

            // Quick Name Preset Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                namePresets.take(3).forEach { preset ->
                    SuggestionChip(
                        onClick = {
                            name = preset
                            nameError = false
                        },
                        label = { Text(preset, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 2. Dosage & Reminder Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage *") },
                    leadingIcon = {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = CareGreenSuccess)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_sheet_med_dosage")
                )

                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it },
                    label = { Text("Reminder Time *") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = CareAmberWarning)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_sheet_med_time")
                )
            }

            // Quick Time Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timePresets.forEach { time ->
                    SuggestionChip(
                        onClick = { scheduledTime = time },
                        label = { Text(time, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 3. Frequency & Instructions
            OutlinedTextField(
                value = frequency,
                onValueChange = { frequency = it },
                label = { Text("Frequency (Daily / Twice daily)") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (e.g. After breakfast with warm water)") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = doctorName,
                onValueChange = { doctorName = it },
                label = { Text("Prescribing Doctor") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Clinical Notes / Purpose") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )

            // Submit Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onAdd(name, dosage, frequency, scheduledTime, instructions, doctorName, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CareBluePrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_sheet_save_medication")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SAVE MEDICATION TO SCHEDULE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = CareTextSecondary, fontSize = 14.sp)
            }
        }
    }
}
