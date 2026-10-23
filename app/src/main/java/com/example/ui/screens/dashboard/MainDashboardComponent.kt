package com.example.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.service.HindiTemplateStyle
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class HealthVitalTrend(
    val label: String,
    val currentValue: String,
    val unit: String,
    val baseline: String,
    val statusText: String,
    val isOptimal: Boolean,
    val icon: ImageVector,
    val clinicalNote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardComponent(
    patient: Patient?,
    medications: List<Medication>,
    medicationEvents: List<MedicationEvent>,
    healthCheckIns: List<HealthCheckIn>,
    adherencePercent: Int,
    onMedicationClick: (Medication) -> Unit,
    onToggleMedicationTaken: (Medication, Boolean) -> Unit,
    onTriggerHealthCheck: () -> Unit,
    onTalkClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onViewComplianceLogsClick: () -> Unit,
    onAddMedicationClick: (() -> Unit)? = null,
    medicationLogs: List<MedicationLog> = emptyList(),
    selectedHindiStyle: HindiTemplateStyle = HindiTemplateStyle.HINGLISH_RESPECTFUL,
    onSetHindiStyle: ((HindiTemplateStyle) -> Unit)? = null,
    onTestHindiNotification: ((Medication, HindiTemplateStyle) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val preferredName = patient?.preferredName ?: "Ravi ji"
    val todayDateStr = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    var selectedTrendCategory by remember { mutableStateOf("All") }
    val trendCategories = listOf("All", "Vitals & BP", "Adherence", "Sleep & Mood")

    // Clinical Biomarkers & Bioscience trends
    val vitalTrends = remember {
        listOf(
            HealthVitalTrend(
                label = "Blood Pressure",
                currentValue = "122/78",
                unit = "mmHg",
                baseline = "138/88",
                statusText = "Controlled (Optimal)",
                isOptimal = true,
                icon = Icons.Default.Favorite,
                clinicalNote = "Targeted by Amlodipine 5mg. Vasodilation effective."
            ),
            HealthVitalTrend(
                label = "Fasting Blood Glucose",
                currentValue = "104",
                unit = "mg/dL",
                baseline = "126 mg/dL",
                statusText = "Normal Range (70-110)",
                isOptimal = true,
                icon = Icons.Default.WaterDrop,
                clinicalNote = "Targeted by Metformin 500mg. Glycemic balance stable."
            ),
            HealthVitalTrend(
                label = "Resting Heart Rate",
                currentValue = "72",
                unit = "bpm",
                baseline = "78 bpm",
                statusText = "Normal Sinus (60-100)",
                isOptimal = true,
                icon = Icons.Default.Speed,
                clinicalNote = "Resting rate steady, no tachycardia observed."
            ),
            HealthVitalTrend(
                label = "Blood Oxygen (SpO₂)",
                currentValue = "98",
                unit = "%",
                baseline = "97%",
                statusText = "Optimal Oxygenation",
                isOptimal = true,
                icon = Icons.Default.Air,
                clinicalNote = "Normal respiratory gas exchange."
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CareBackground)
            .padding(horizontal = 16.dp)
            .testTag("main_material3_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Welcome & Patient Health Status Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_header_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Namaste, $preferredName",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareTextPrimary
                            )
                            Text(
                                text = todayDateStr,
                                fontSize = 13.sp,
                                color = CareTextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Surface(
                            color = CareGreenLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CareGreenSuccess)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Status: Stable",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CareGreenSuccess
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Quick Stats strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickMetricBadge(
                            title = "Adherence",
                            value = "$adherencePercent%",
                            subtitle = "Target >90%",
                            color = CareGreenSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            title = "BP Avg",
                            value = "122/78",
                            subtitle = "Controlled",
                            color = CareBluePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            title = "Caregiver",
                            value = "Priya",
                            subtitle = "Connected",
                            color = CarePurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Upcoming Medication Reminders Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Alarm,
                        contentDescription = null,
                        tint = CareBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Upcoming Medication Reminders",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CareTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CareBlueLight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${medications.size} scheduled",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CareBlueDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (onAddMedicationClick != null) {
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = onAddMedicationClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_add_medication_header")
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = "Add New Medication",
                                tint = CareBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Hindi / Hinglish Reminder Style Selector
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("hindi_template_selector_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Translate,
                                contentDescription = null,
                                tint = CareBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Notification Voice & Language Style",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CareTextPrimary
                            )
                        }

                        Surface(
                            color = CareBlueLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = selectedHindiStyle.displayName.split(" ").first(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CareBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HindiTemplateStyle.values().forEach { style ->
                            val isSelected = selectedHindiStyle == style
                            Surface(
                                color = if (isSelected) CareBluePrimary else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetHindiStyle?.invoke(style) }
                                    .testTag("chip_hindi_style_${style.name.lowercase()}")
                            ) {
                                Text(
                                    text = when (style) {
                                        HindiTemplateStyle.HINGLISH_RESPECTFUL -> "Dadi/Dada"
                                        HindiTemplateStyle.PURE_HINDI -> "शुद्ध हिंदी"
                                        HindiTemplateStyle.TIME_OF_DAY -> "Subah/Meal"
                                        HindiTemplateStyle.URGENT_HEALTH -> "Swasthya"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else CareTextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of Medication Reminder Cards
        if (medications.isEmpty()) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No medications scheduled. Tap Add Medication in Caregiver portal.",
                        fontSize = 13.sp,
                        color = CareTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(medications) { med ->
                val matchingEvent = medicationEvents.firstOrNull { it.medicationId == med.id }
                val isTaken = matchingEvent?.status == MedicationStatus.TAKEN
                val isDelayed = matchingEvent?.status == MedicationStatus.DELAYED
                val isMissed = matchingEvent?.status == MedicationStatus.MISSED

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMedicationClick(med) }
                        .testTag("med_reminder_item_${med.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Time Pill
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when {
                                            isTaken -> CareGreenLight
                                            isMissed -> CareRedLight
                                            isDelayed -> CareAmberLight
                                            else -> CareBlueLight
                                        }
                                    )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        when {
                                            isTaken -> Icons.Default.Check
                                            isMissed -> Icons.Default.Cancel
                                            isDelayed -> Icons.Default.Schedule
                                            else -> Icons.Default.Medication
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isTaken -> CareGreenSuccess
                                            isMissed -> CareRedAlert
                                            isDelayed -> CareAmberWarning
                                            else -> CareBluePrimary
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = med.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CareTextPrimary
                                    )

                                    Surface(
                                        color = when {
                                            isTaken -> CareGreenLight
                                            isMissed -> CareRedLight
                                            isDelayed -> CareAmberLight
                                            else -> Color(0xFFEFF6FF)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isTaken -> "Taken ✓"
                                                isMissed -> "Missed"
                                                isDelayed -> "Delayed"
                                                else -> "Due ${med.scheduledTime}"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isTaken -> CareGreenSuccess
                                                isMissed -> CareRedAlert
                                                isDelayed -> CareAmberWarning
                                                else -> CareBluePrimary
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${med.scheduledTime} • ${med.dosage} (${med.frequency})",
                                    fontSize = 13.sp,
                                    color = CareTextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                if (med.instructions.isNotBlank()) {
                                    Text(
                                        text = "Instructions: ${med.instructions}",
                                        fontSize = 12.sp,
                                        color = CareBlueDark,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                        // Divider & Interactive Checkbox / Switch Row
                        Divider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onToggleMedicationTaken(med, !isTaken) }
                            ) {
                                Checkbox(
                                    checked = isTaken,
                                    onCheckedChange = { checked ->
                                        onToggleMedicationTaken(med, checked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CareGreenSuccess,
                                        uncheckedColor = CareTextSecondary,
                                        checkmarkColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("med_checkbox_${med.id}")
                                )
                                Text(
                                    text = if (isTaken) "Marked as Taken" else "Mark as taken",
                                    fontSize = 13.sp,
                                    fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTaken) CareGreenSuccess else CareTextPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onToggleMedicationTaken(med, !isTaken) }
                            ) {
                                Text(
                                    text = if (isTaken) "Taken" else "Pending",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isTaken) CareGreenSuccess else CareTextSecondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Switch(
                                    checked = isTaken,
                                    onCheckedChange = { checked ->
                                        onToggleMedicationTaken(med, checked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = CareGreenSuccess,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFCBD5E1)
                                    ),
                                    thumbContent = {
                                        if (isTaken) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                tint = CareGreenSuccess
                                            )
                                        }
                                    },
                                    modifier = Modifier.testTag("med_switch_${med.id}")
                                )
                            }
                        }

                        // Secondary Action Strip: Test WorkManager Hindi Reminder Notification
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .clickable { onTestHindiNotification?.invoke(med, selectedHindiStyle) }
                                    .testTag("btn_test_notification_${med.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = CareBluePrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "🔔 Test Notification (दवाई अलर्ट)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CareBluePrimary
                                    )
                                }
                            }

                            Text(
                                text = "Channel: High Priority",
                                fontSize = 11.sp,
                                color = CareTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Health & Bioscience Trends Summary Section with Interactive Canvas Visualization
        item {
            HealthTrendsSummaryCard(
                medicationLogs = medicationLogs,
                medicationEvents = medicationEvents,
                overallAdherencePercent = adherencePercent,
                onViewFullHistoryClick = onViewComplianceLogsClick
            )
        }

        // Bioscience Vitals & Biomarkers Cards
        if (selectedTrendCategory == "All" || selectedTrendCategory == "Vitals & BP") {
            items(vitalTrends) { vital ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("vital_card_${vital.label}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CareBlueLight)
                                ) {
                                    Icon(vital.icon, contentDescription = null, tint = CareBluePrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = vital.label,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CareTextPrimary
                                    )
                                    Text(
                                        text = "Baseline: ${vital.baseline}",
                                        fontSize = 11.sp,
                                        color = CareTextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${vital.currentValue} ${vital.unit}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (vital.isOptimal) CareGreenSuccess else CareAmberWarning
                                )
                                Surface(
                                    color = if (vital.isOptimal) CareGreenLight else CareAmberLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = vital.statusText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (vital.isOptimal) CareGreenSuccess else CareAmberWarning,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = CareBluePrimary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = vital.clinicalNote,
                                    fontSize = 12.sp,
                                    color = CareTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sleep & Mood Wellness Trend
        if (selectedTrendCategory == "All" || selectedTrendCategory == "Sleep & Mood") {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Circadian Rhythm & Neurological Wellness",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareTextPrimary
                        )
                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WellnessTrendCard(
                                title = "Sleep Duration",
                                value = "7.2 hrs",
                                state = "Good Quality",
                                color = CarePurple,
                                modifier = Modifier.weight(1f)
                            )
                            WellnessTrendCard(
                                title = "Daily Mood",
                                value = "Positive",
                                state = "Stable",
                                color = CareGreenSuccess,
                                modifier = Modifier.weight(1f)
                            )
                            WellnessTrendCard(
                                title = "Energy Index",
                                value = "Medium",
                                state = "Active",
                                color = CareAmberWarning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Access Banner
        item {
            Surface(
                color = CareBluePrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Need Voice Assistance?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Ask CareGuardian in Hindi or English about medication timing or how you feel.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Button(
                        onClick = onTalkClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("TALK", fontWeight = FontWeight.Bold, color = CareBluePrimary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickMetricBadge(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = CareTextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 10.sp, color = CareTextSecondary)
        }
    }
}

@Composable
private fun WellnessTrendCard(
    title: String,
    value: String,
    state: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = CareTextSecondary)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            Text(state, fontSize = 10.sp, color = CareTextSecondary)
        }
    }
}
