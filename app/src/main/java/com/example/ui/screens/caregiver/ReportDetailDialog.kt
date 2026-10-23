package com.example.ui.screens.caregiver

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HealthReport
import com.example.ui.theme.*

@Composable
fun ReportDetailDialog(
    report: HealthReport?,
    patientName: String,
    onDismiss: () -> Unit
) {
    if (report == null) return
    var downloadStatus by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .testTag("report_detail_dialog")
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
                            text = "CAREGUARDIAN ${report.reportType} REPORT",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CareBluePrimary
                        )
                        Text(
                            text = "Patient: $patientName • ${report.date}",
                            fontSize = 13.sp,
                            color = CareTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Adherence Stat & Mini Chart
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Medication Adherence",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CareTextPrimary
                                )
                                Text(
                                    text = "${report.adherencePercent}%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = CareGreenSuccess
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // Custom Compose Canvas Chart for Medication Adherence
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                val totalWidth = size.width
                                val barHeight = size.height
                                val takenWidth = totalWidth * (report.adherencePercent / 100f)

                                // Background track
                                drawRoundRect(
                                    color = Color(0xFFE2E8F0),
                                    topLeft = Offset(0f, 0f),
                                    size = Size(totalWidth, barHeight),
                                    cornerRadius = CornerRadius(18f, 18f)
                                )
                                // Active progress
                                drawRoundRect(
                                    color = CareGreenSuccess,
                                    topLeft = Offset(0f, 0f),
                                    size = Size(takenWidth, barHeight),
                                    cornerRadius = CornerRadius(18f, 18f)
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taken: ${report.takenCount}", fontSize = 12.sp, color = CareGreenSuccess, fontWeight = FontWeight.SemiBold)
                                Text("Delayed: ${report.delayedCount}", fontSize = 12.sp, color = CareAmberWarning, fontWeight = FontWeight.SemiBold)
                                Text("Missed: ${report.missedCount}", fontSize = 12.sp, color = CareRedAlert, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Vitals & Wellness
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Daily Wellness & Check-ins",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CareTextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                WellnessPill(title = "Mood", value = report.moodSummary)
                                WellnessPill(title = "Sleep", value = report.sleepSummary)
                                WellnessPill(title = "Energy", value = report.energySummary)
                            }
                        }
                    }

                    // Symptoms & Observations
                    Surface(
                        color = if (report.reportedSymptoms.isNotBlank() && !report.reportedSymptoms.contains("None", ignoreCase = true)) CareAmberLight else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Reported Symptoms / Notes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CareTextPrimary
                            )
                            Text(
                                text = report.reportedSymptoms.ifBlank { "No adverse symptoms reported." },
                                fontSize = 13.sp,
                                color = CareTextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Key Findings & Actions
                    Surface(
                        color = CareBlueLight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Key Findings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CareBlueDark
                            )
                            Text(
                                text = report.keyFindings,
                                fontSize = 13.sp,
                                color = CareTextPrimary,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )
                            Text(
                                text = "Caregiver Action Required:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CareBlueDark
                            )
                            Text(
                                text = report.caregiverAction,
                                fontSize = 13.sp,
                                color = CareTextPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    if (downloadStatus != null) {
                        Text(
                            text = downloadStatus ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareGreenSuccess
                        )
                    }
                }

                // Action Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            downloadStatus = "Report exported to storage/reports/report_${System.currentTimeMillis()}.pdf (Demo Mode)"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CareBluePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_download_report_pdf")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("DOWNLOAD PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            downloadStatus = "Report summary copied & sent to caregiver email (Demo Mode)"
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_share_report")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("SHARE REPORT", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun WellnessPill(title: String, value: String) {
    Column {
        Text(text = title, fontSize = 11.sp, color = CareTextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareTextPrimary)
    }
}
