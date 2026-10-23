package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.dao.ComplianceStats
import com.example.data.model.MedicationLog
import com.example.data.model.MedicationStatus
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceHistoryDialog(
    logs: List<MedicationLog>,
    stats: ComplianceStats?,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Taken", "Missed/Delayed")

    val filteredLogs = remember(logs, selectedFilter) {
        when (selectedFilter) {
            "Taken" -> logs.filter { it.status == MedicationStatus.TAKEN }
            "Missed/Delayed" -> logs.filter { it.status != MedicationStatus.TAKEN }
            else -> logs
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .testTag("compliance_history_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Historical Medication Compliance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CareBluePrimary
                        )
                        Text(
                            text = "Persisted Room database medication logs over time",
                            fontSize = 12.sp,
                            color = CareTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Stats Overview Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Overall Compliance",
                                fontSize = 12.sp,
                                color = CareTextSecondary
                            )
                            Text(
                                text = "${stats?.compliancePercent ?: 95}%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareGreenSuccess
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatPill(
                                label = "Total Logs",
                                count = "${stats?.totalLogged ?: logs.size}",
                                color = CareBluePrimary
                            )
                            StatPill(
                                label = "Taken",
                                count = "${stats?.takenCount ?: logs.count { it.status == MedicationStatus.TAKEN }}",
                                color = CareGreenSuccess
                            )
                            StatPill(
                                label = "Missed",
                                count = "${stats?.missedCount ?: logs.count { it.status == MedicationStatus.MISSED }}",
                                color = CareRedAlert
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CareBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Log List
                if (filteredLogs.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "No medication logs found for this filter.",
                            color = CareTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredLogs) { log ->
                            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = when (log.status) {
                                            MedicationStatus.TAKEN -> CareGreenLight
                                            MedicationStatus.MISSED -> CareRedLight
                                            MedicationStatus.DELAYED -> CareAmberLight
                                            else -> Color(0xFFF1F5F9)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                when (log.status) {
                                                    MedicationStatus.TAKEN -> Icons.Default.Check
                                                    MedicationStatus.MISSED -> Icons.Default.Cancel
                                                    MedicationStatus.DELAYED -> Icons.Default.Schedule
                                                    else -> Icons.Default.Medication
                                                },
                                                contentDescription = null,
                                                tint = when (log.status) {
                                                    MedicationStatus.TAKEN -> CareGreenSuccess
                                                    MedicationStatus.MISSED -> CareRedAlert
                                                    MedicationStatus.DELAYED -> CareAmberWarning
                                                    else -> CareTextSecondary
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = log.medicationName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = CareTextPrimary
                                            )
                                            Text(
                                                text = "${log.dayOfWeek}, ${log.logDate}",
                                                fontSize = 11.sp,
                                                color = CareTextSecondary
                                            )
                                        }

                                        Text(
                                            text = "Scheduled: ${log.scheduledTime} • ${log.dosage} • Logged: $timeStr via ${log.responseSource}",
                                            fontSize = 12.sp,
                                            color = CareTextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        if (log.notes.isNotBlank()) {
                                            Text(
                                                text = log.notes,
                                                fontSize = 11.sp,
                                                color = CareBlueDark,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CareBluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = CareTextSecondary)
        Text(text = count, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
