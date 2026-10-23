package com.example.ui.screens.caregiver

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaregiverDashboardScreen(
    patient: Patient?,
    medications: List<Medication>,
    medicationEvents: List<MedicationEvent>,
    notifications: List<CaregiverNotification>,
    healthCheckIns: List<HealthCheckIn>,
    aiMessages: List<AIMessage>,
    adherencePercent: Int,
    unreadAlertsCount: Int,
    onStartAICall: () -> Unit,
    onAddMedicationClick: () -> Unit,
    onGenerateDailyReport: () -> Unit,
    onGenerateWeeklyReport: () -> Unit,
    onMarkNotificationRead: (String) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    onToggleMedicationTaken: (Medication, Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Alerts", "Medications", "Health", "AI Dialogs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareBackground)
            .testTag("caregiver_dashboard_screen")
    ) {
        // Caregiver Header
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CareGuardian Portal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CareBluePrimary
                        )
                        Text(
                            text = "Logged in as ${patient?.caregiverName ?: "Priya Sharma"} (Caregiver)",
                            fontSize = 12.sp,
                            color = CareTextSecondary
                        )
                    }
                    Surface(
                        color = CareGreenLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CareGreenSuccess))
                            Spacer(Modifier.width(6.dp))
                            Text("System Live", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareGreenSuccess)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Stat Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CaregiverStatCard(
                        title = "Adherence",
                        value = "$adherencePercent%",
                        subtitle = "Target: 90%+",
                        color = CareGreenSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    CaregiverStatCard(
                        title = "Pending Alerts",
                        value = "$unreadAlertsCount",
                        subtitle = if (unreadAlertsCount > 0) "Action required" else "All clear",
                        color = if (unreadAlertsCount > 0) CareAmberWarning else CareBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    CaregiverStatCard(
                        title = "Check-ins",
                        value = "${healthCheckIns.size}",
                        subtitle = "Today",
                        color = CarePurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Patient Overview Banner
        Surface(
            color = Color(0xFFF1F5F9),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CareBluePrimary)
                ) {
                    Text(
                        text = patient?.fullName?.take(2)?.uppercase() ?: "RS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${patient?.fullName ?: "Ravi Sharma"} (${patient?.preferredName ?: "Ravi ji"})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CareTextPrimary
                    )
                    Text(
                        text = "Age: ${patient?.age ?: 72} • Conditions: ${patient?.existingConditions ?: "Hypertension"}",
                        fontSize = 12.sp,
                        color = CareTextSecondary
                    )
                }

                // AI Voice Call Action Button
                FilledTonalButton(
                    onClick = onStartAICall,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CareBlueLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_start_ai_call")
                ) {
                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = CareBluePrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("AI CALL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareBluePrimary)
                }
            }
        }

        // Quick Action Row (Add Med, Daily Report, Weekly Report)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onAddMedicationClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("btn_add_medication")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Med", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = onGenerateDailyReport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("btn_daily_report")
            ) {
                Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Daily Report", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = onGenerateWeeklyReport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("btn_weekly_report")
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Weekly Report", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = CareBluePrimary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = if (index == 0 && unreadAlertsCount > 0) "$title ($unreadAlertsCount)" else title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> AlertsTab(
                    notifications = notifications,
                    onMarkRead = onMarkNotificationRead,
                    onMarkAllRead = onMarkAllNotificationsRead
                )
                1 -> MedicationsTab(
                    medications = medications,
                    events = medicationEvents,
                    onToggleTaken = onToggleMedicationTaken
                )
                2 -> HealthTab(checkIns = healthCheckIns)
                3 -> AIDialogsTab(messages = aiMessages)
            }
        }
    }
}

@Composable
private fun CaregiverStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = CareTextSecondary, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 10.sp, color = CareTextSecondary)
        }
    }
}

@Composable
private fun AlertsTab(
    notifications: List<CaregiverNotification>,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit
) {
    if (notifications.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("No alerts at this time. All clear!", color = CareTextSecondary, fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notification Center", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all read", fontSize = 12.sp, color = CareBluePrimary)
                    }
                }
            }
            items(notifications) { notif ->
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (notif.isRead) Color.White else Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (notif.isRead) CareBorder else CareAmberWarning.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val icon = when (notif.type) {
                            AlertType.MEDICATION_MISSED -> Icons.Default.Warning
                            AlertType.NO_RESPONSE -> Icons.Default.Error
                            AlertType.POSSIBLE_EMERGENCY -> Icons.Default.Emergency
                            AlertType.SYMPTOM_REPORTED -> Icons.Default.Healing
                            AlertType.SYSTEM_ALERT -> Icons.Default.CheckCircle
                        }
                        val tint = when (notif.severity) {
                            AlertSeverity.CRITICAL -> CareRedAlert
                            AlertSeverity.URGENT -> CareRedAlert
                            AlertSeverity.WARNING -> CareAmberWarning
                            AlertSeverity.INFO -> CareGreenSuccess
                        }
                        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CareTextPrimary)
                                Text(timeStr, fontSize = 11.sp, color = CareTextSecondary)
                            }
                            Text(notif.message, fontSize = 13.sp, color = CareTextSecondary, modifier = Modifier.padding(top = 3.dp))
                            if (!notif.isRead) {
                                TextButton(
                                    onClick = { onMarkRead(notif.id) },
                                    modifier = Modifier.align(Alignment.End).height(32.dp)
                                ) {
                                    Text("Acknowledge", fontSize = 11.sp, color = CareBluePrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationsTab(
    medications: List<Medication>,
    events: List<MedicationEvent>,
    onToggleTaken: (Medication, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Active Medication Schedule", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(medications) { med ->
            val matchingEvent = events.firstOrNull { it.medicationId == med.id }
            val isTaken = matchingEvent?.status == MedicationStatus.TAKEN
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isTaken) CareGreenSuccess.copy(alpha = 0.5f) else CareBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTaken) CareGreenLight else CareBlueLight)
                        ) {
                            Icon(
                                if (isTaken) Icons.Default.Check else Icons.Default.Medication,
                                contentDescription = null,
                                tint = if (isTaken) CareGreenSuccess else CareBluePrimary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CareTextPrimary)
                            Text("${med.scheduledTime} • ${med.dosage} (${med.frequency})", fontSize = 13.sp, color = CareTextSecondary)
                            if (med.instructions.isNotBlank()) {
                                Text(med.instructions, fontSize = 12.sp, color = CareBlueDark)
                            }
                        }
                        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp)) {
                            Text(med.doctorName.take(12), fontSize = 11.sp, color = CareTextSecondary, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onToggleTaken(med, !isTaken) }
                        ) {
                            Checkbox(
                                checked = isTaken,
                                onCheckedChange = { checked -> onToggleTaken(med, checked) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CareGreenSuccess,
                                    uncheckedColor = CareTextSecondary
                                )
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isTaken) "Marked as Taken ✓" else "Mark as taken",
                                fontSize = 12.sp,
                                fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTaken) CareGreenSuccess else CareTextSecondary
                            )
                        }

                        Switch(
                            checked = isTaken,
                            onCheckedChange = { checked -> onToggleTaken(med, checked) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CareGreenSuccess
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
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Today's Event Logs", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(events) { ev ->
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ev.responseTimestamp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(ev.medicationName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("${ev.scheduledTime} • ${ev.notes}", fontSize = 11.sp, color = CareTextSecondary)
                    }
                    Surface(
                        color = when (ev.status) {
                            MedicationStatus.TAKEN -> CareGreenLight
                            MedicationStatus.MISSED -> CareRedLight
                            MedicationStatus.DELAYED -> CareAmberLight
                            MedicationStatus.NO_RESPONSE -> CareRedLight
                            else -> Color(0xFFF1F5F9)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = ev.status.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (ev.status) {
                                MedicationStatus.TAKEN -> CareGreenSuccess
                                MedicationStatus.MISSED -> CareRedAlert
                                MedicationStatus.DELAYED -> CareAmberWarning
                                MedicationStatus.NO_RESPONSE -> CareRedAlert
                                else -> CareTextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthTab(checkIns: List<HealthCheckIn>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Health & Symptom Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(checkIns) { ci ->
            val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(ci.timestamp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Check-in: $timeStr", fontSize = 12.sp, color = CareTextSecondary)
                        Surface(color = CareGreenLight, shape = RoundedCornerShape(6.dp)) {
                            Text("Mood: ${ci.mood}", fontSize = 11.sp, color = CareGreenSuccess, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Sleep: ${ci.sleep} • Energy: ${ci.energy}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    if (ci.symptoms.isNotBlank() && !ci.symptoms.equals("None", ignoreCase = true)) {
                        Spacer(Modifier.height(4.dp))
                        Surface(color = CareAmberLight, shape = RoundedCornerShape(6.dp)) {
                            Text("Symptom reported: ${ci.symptoms}", fontSize = 12.sp, color = CareAmberWarning, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AIDialogsTab(messages: List<AIMessage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("AI Companion Auditing & Conversation Log", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Consent granted for caregiver auditing. Protected encrypted logs.", fontSize = 12.sp, color = CareTextSecondary)
            Spacer(Modifier.height(8.dp))
        }
        items(messages) { msg ->
            val isUser = msg.sender == "USER"
            Surface(
                color = if (isUser) Color(0xFFF1F5F9) else CareBlueLight.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isUser) "Ravi Sharma (Patient):" else "CareGuardian AI:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) CareTextSecondary else CareBluePrimary
                    )
                    Text(text = msg.text, fontSize = 13.sp, color = CareTextPrimary, modifier = Modifier.padding(top = 2.dp))
                    if (msg.detectedSymptom != null) {
                        Text("Detected: ${msg.detectedSymptom}", fontSize = 11.sp, color = CareAmberWarning, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}
