package com.example.ui.screens.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DemoSimulationScreen(
    isDemoRunning: Boolean,
    demoMessage: String?,
    onRunCompleteWorkflow: () -> Unit,
    onTriggerReminder: () -> Unit,
    onSimulateTaken: () -> Unit,
    onSimulateMissed: () -> Unit,
    onSimulateDelayed: () -> Unit,
    onSimulateNoResponse: () -> Unit,
    onSimulateSymptom: () -> Unit,
    onSimulateEmergency: () -> Unit,
    onStartAICall: () -> Unit,
    onTriggerNightRoutine: () -> Unit,
    onGenerateDailyReport: () -> Unit,
    onGenerateWeeklyReport: () -> Unit,
    onResetDemoData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("demo_simulation_screen")
    ) {
        // Banner
        Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = CareAmberWarning)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "DEMO & SIMULATION LAB",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = CareAmberWarning
                    )
                }
                Text(
                    text = "Control any state transition, trigger Hindi voice reminders, test escalations, and simulate symptoms without waiting for real schedules.",
                    fontSize = 12.sp,
                    color = Color(0xFF92400E),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Guided Scenario Section (Section 49)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CareBluePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Section 49: Full End-to-End Guided Demo",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Runs the complete patient → AI → medication → caregiver workflow: Reminder -> Hindi Voice Response -> Caregiver Notification -> Symptom alert -> Night routine.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                if (!demoMessage.isNullOrBlank()) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = demoMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CareBlueDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = onRunCompleteWorkflow,
                    enabled = !isDemoRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_run_complete_demo")
                ) {
                    Icon(
                        if (isDemoRunning) Icons.Default.HourglassTop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CareBluePrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isDemoRunning) "DEMO IN PROGRESS..." else "START COMPLETE DEMO SCENARIO",
                        fontWeight = FontWeight.ExtraBold,
                        color = CareBluePrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Individual Simulation Controls",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CareTextPrimary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Grid of Quick Triggers
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoActionItem(
                title = "Trigger Scheduled Medication Reminder",
                subtitle = "Pops up reminder dialog and AI speaks in Hindi/Hinglish",
                icon = Icons.Default.Alarm,
                color = CareBluePrimary,
                tag = "demo_trigger_reminder",
                onClick = onTriggerReminder
            )

            DemoActionItem(
                title = "Simulate Voice 'Haan beta, maine le li'",
                subtitle = "Extracts MEDICATION_TAKEN (confidence 0.96) -> Caregiver notified",
                icon = Icons.Default.CheckCircle,
                color = CareGreenSuccess,
                tag = "demo_simulate_taken",
                onClick = onSimulateTaken
            )

            DemoActionItem(
                title = "Simulate Voice 'Thodi der baad lunga'",
                subtitle = "Extracts MEDICATION_DELAYED -> Sets postponement reminder",
                icon = Icons.Default.Schedule,
                color = CareAmberWarning,
                tag = "demo_simulate_delayed",
                onClick = onSimulateDelayed
            )

            DemoActionItem(
                title = "Simulate Voice 'Nahi abhi nahi li'",
                subtitle = "Extracts MEDICATION_MISSED -> Generates warning alert",
                icon = Icons.Default.Cancel,
                color = CareRedAlert,
                tag = "demo_simulate_missed",
                onClick = onSimulateMissed
            )

            DemoActionItem(
                title = "Simulate No-Response Escalation",
                subtitle = "1st reminder -> 2nd reminder -> NO_RESPONSE Caregiver Alert",
                icon = Icons.Default.NotificationImportant,
                color = CareRedAlert,
                tag = "demo_simulate_no_response",
                onClick = onSimulateNoResponse
            )

            DemoActionItem(
                title = "Simulate Symptom: 'Chakkar aa rahe hain'",
                subtitle = "AI safe logging of dizziness -> Alerts caregiver dashboard",
                icon = Icons.Default.Healing,
                color = CareAmberWarning,
                tag = "demo_simulate_symptom",
                onClick = onSimulateSymptom
            )

            DemoActionItem(
                title = "Simulate Emergency: 'Chest pain / Saans me dikkat'",
                subtitle = "Immediate 108 emergency workflow and red banner trigger",
                icon = Icons.Default.Emergency,
                color = CareRedAlert,
                tag = "demo_simulate_emergency",
                onClick = onSimulateEmergency
            )

            DemoActionItem(
                title = "Simulate AI Voice Call from Caregiver",
                subtitle = "Caregiver initiates call -> simulated dialogue & summary",
                icon = Icons.Default.PhoneInTalk,
                color = CarePurple,
                tag = "demo_simulate_ai_call",
                onClick = onStartAICall
            )

            DemoActionItem(
                title = "Trigger 9:00 PM Night Routine",
                subtitle = "\"Good night Ravi ji. Achhi neend aaye...\"",
                icon = Icons.Default.Bedtime,
                color = Color(0xFF9333EA),
                tag = "demo_trigger_night_routine",
                onClick = onTriggerNightRoutine
            )

            DemoActionItem(
                title = "Generate Daily Health Report",
                subtitle = "Creates adherence summary, wellness breakdown & caregiver actions",
                icon = Icons.Default.Assessment,
                color = CareBluePrimary,
                tag = "demo_generate_daily_report",
                onClick = onGenerateDailyReport
            )

            DemoActionItem(
                title = "Generate Weekly Trend Report",
                subtitle = "Generates 7-day adherence charts and medical review notes",
                icon = Icons.Default.TrendingUp,
                color = CareBluePrimary,
                tag = "demo_generate_weekly_report",
                onClick = onGenerateWeeklyReport
            )

            DemoActionItem(
                title = "Reset Demo Data to Initial Seeds",
                subtitle = "Restores Ravi Sharma, 3 medications, and baseline adherence",
                icon = Icons.Default.Refresh,
                color = Color(0xFF64748B),
                tag = "demo_reset_data",
                onClick = onResetDemoData
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DemoActionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CareBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f))
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CareTextPrimary)
                Text(subtitle, fontSize = 12.sp, color = CareTextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CareTextSecondary)
        }
    }
}
