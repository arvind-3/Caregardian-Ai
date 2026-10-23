package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicationStatus
import com.example.ui.screens.caregiver.AddMedicationDialog
import com.example.ui.screens.caregiver.CaregiverDashboardScreen
import com.example.ui.screens.caregiver.ReportDetailDialog
import com.example.ui.screens.caregiver.SimulatedAICallDialog
import com.example.ui.screens.dashboard.AddMedicationBottomSheet
import com.example.ui.screens.dashboard.ComplianceHistoryDialog
import com.example.ui.screens.dashboard.MainDashboardComponent
import com.example.ui.screens.demo.DemoSimulationScreen
import com.example.ui.screens.patient.*
import android.content.Intent
import com.example.service.HindiTemplateStyle
import com.example.service.MedicationReminderWorker
import com.example.ui.theme.CareBluePrimary
import com.example.ui.theme.CareGuardianTheme
import com.example.ui.viewmodel.AppMode
import com.example.ui.viewmodel.CareGuardianViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CareGuardianViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            CareGuardianTheme {
                CareGuardianApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == MedicationReminderWorker.ACTION_MARK_TAKEN) {
            val medId = intent.getStringExtra(MedicationReminderWorker.EXTRA_MED_ID)
            if (medId != null) {
                viewModel.markMedicationTakenById(medId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareGuardianApp(viewModel: CareGuardianViewModel) {
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val patient by viewModel.patient.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val nextMed by viewModel.nextMedication.collectAsStateWithLifecycle()
    val medicationEvents by viewModel.medicationEvents.collectAsStateWithLifecycle()
    val healthCheckIns by viewModel.healthCheckIns.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val adherencePercent by viewModel.adherencePercentage.collectAsStateWithLifecycle()
    val unreadAlertsCount by viewModel.unreadAlertsCount.collectAsStateWithLifecycle()
    val medicationLogs by viewModel.medicationLogs.collectAsStateWithLifecycle()
    val complianceStats by viewModel.complianceStats.collectAsStateWithLifecycle()

    // Dialog state collectors
    val showComplianceHistory by viewModel.showComplianceHistoryDialog.collectAsStateWithLifecycle()
    val showReminderDialog by viewModel.showReminderDialog.collectAsStateWithLifecycle()
    val activeReminderMed by viewModel.activeReminderMedication.collectAsStateWithLifecycle()
    val reminderStatusMsg by viewModel.reminderStatusMessage.collectAsStateWithLifecycle()

    val showVoiceCompanion by viewModel.showVoiceCompanionDialog.collectAsStateWithLifecycle()
    val showHealthCheck by viewModel.showHealthCheckDialog.collectAsStateWithLifecycle()
    val showEmergency by viewModel.showEmergencyDialog.collectAsStateWithLifecycle()
    val showNightRoutine by viewModel.showNightRoutineDialog.collectAsStateWithLifecycle()
    val showSimulatedCall by viewModel.showSimulatedCallDialog.collectAsStateWithLifecycle()
    val showAddMedication by viewModel.showAddMedicationDialog.collectAsStateWithLifecycle()
    val showReportDetail by viewModel.showReportDetailDialog.collectAsStateWithLifecycle()
    val selectedReport by viewModel.selectedReport.collectAsStateWithLifecycle()

    val callStatus by viewModel.callStatus.collectAsStateWithLifecycle()
    val callTranscript by viewModel.callTranscript.collectAsStateWithLifecycle()

    val isDemoRunning by viewModel.isDemoRunning.collectAsStateWithLifecycle()
    val demoMessage by viewModel.demoSequenceMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = CareBluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CareGuardian AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareBluePrimary
                            )
                            Text(
                                text = "Elderly Care & Voice Companion",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    // Quick Demo Trigger
                    IconButton(
                        onClick = { viewModel.triggerReminder(nextMed) },
                        modifier = Modifier.testTag("action_quick_reminder")
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Trigger Reminder",
                            tint = CareBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentMode == AppMode.DASHBOARD,
                    onClick = { viewModel.setMode(AppMode.DASHBOARD) },
                    icon = { Icon(Icons.Default.SpaceDashboard, contentDescription = "Main Dashboard") },
                    label = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_main_dashboard")
                )
                NavigationBarItem(
                    selected = currentMode == AppMode.PATIENT,
                    onClick = { viewModel.setMode(AppMode.PATIENT) },
                    icon = { Icon(Icons.Default.Elderly, contentDescription = "Patient Mode") },
                    label = { Text("Patient", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_patient_mode")
                )
                NavigationBarItem(
                    selected = currentMode == AppMode.CAREGIVER,
                    onClick = { viewModel.setMode(AppMode.CAREGIVER) },
                    icon = {
                        BadgedBox(badge = {
                            if (unreadAlertsCount > 0) {
                                Badge { Text("$unreadAlertsCount") }
                            }
                        }) {
                            Icon(Icons.Default.Dashboard, contentDescription = "Caregiver Portal")
                        }
                    },
                    label = { Text("Caregiver", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_caregiver_mode")
                )
                NavigationBarItem(
                    selected = currentMode == AppMode.DEMO_LAB,
                    onClick = { viewModel.setMode(AppMode.DEMO_LAB) },
                    icon = { Icon(Icons.Default.Science, contentDescription = "Demo Lab") },
                    label = { Text("Demo Lab", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_demo_lab")
                )
            }
        },
        floatingActionButton = {
            if (currentMode == AppMode.DASHBOARD || currentMode == AppMode.CAREGIVER) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.showAddMedicationDialog.value = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Medication") },
                    text = { Text("Add Medication", fontWeight = FontWeight.Bold) },
                    containerColor = CareBluePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_medication")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentMode) {
                AppMode.DASHBOARD -> {
                    MainDashboardComponent(
                        patient = patient,
                        medications = medications,
                        medicationEvents = medicationEvents,
                        healthCheckIns = healthCheckIns,
                        adherencePercent = adherencePercent,
                        onMedicationClick = { med -> viewModel.triggerReminder(med) },
                        onToggleMedicationTaken = { med, isTaken -> viewModel.toggleMedicationTaken(med, isTaken) },
                        onTriggerHealthCheck = { viewModel.showHealthCheckDialog.value = true },
                        onTalkClick = { viewModel.showVoiceCompanionDialog.value = true },
                        onEmergencyClick = { viewModel.showEmergencyDialog.value = true },
                        onViewComplianceLogsClick = { viewModel.showComplianceHistoryDialog.value = true },
                        onAddMedicationClick = { viewModel.showAddMedicationDialog.value = true }
                    )
                }

                AppMode.PATIENT -> {
                    PatientScreen(
                        patient = patient,
                        nextMed = nextMed,
                        adherencePercent = adherencePercent,
                        onTakeMedicineClick = { viewModel.triggerReminder(nextMed) },
                        onTalkClick = { viewModel.showVoiceCompanionDialog.value = true },
                        onHealthCheckClick = { viewModel.showHealthCheckDialog.value = true },
                        onEmergencyClick = { viewModel.showEmergencyDialog.value = true },
                        onNightRoutineClick = { viewModel.triggerNightRoutine() }
                    )
                }

                AppMode.CAREGIVER -> {
                    CaregiverDashboardScreen(
                        patient = patient,
                        medications = medications,
                        medicationEvents = medicationEvents,
                        notifications = notifications,
                        healthCheckIns = healthCheckIns,
                        aiMessages = aiMessages,
                        adherencePercent = adherencePercent,
                        unreadAlertsCount = unreadAlertsCount,
                        onStartAICall = { viewModel.startSimulatedAICall() },
                        onAddMedicationClick = { viewModel.showAddMedicationDialog.value = true },
                        onGenerateDailyReport = { viewModel.generateDailyReport() },
                        onGenerateWeeklyReport = { viewModel.generateWeeklyReport() },
                        onMarkNotificationRead = { id -> viewModel.markNotificationRead(id) },
                        onMarkAllNotificationsRead = { viewModel.markAllNotificationsRead() },
                        onToggleMedicationTaken = { med, isTaken -> viewModel.toggleMedicationTaken(med, isTaken) }
                    )
                }

                AppMode.DEMO_LAB -> {
                    DemoSimulationScreen(
                        isDemoRunning = isDemoRunning,
                        demoMessage = demoMessage,
                        onRunCompleteWorkflow = { viewModel.runCompleteDemoWorkflow() },
                        onTriggerReminder = { viewModel.triggerReminder(nextMed) },
                        onSimulateTaken = {
                            viewModel.triggerReminder(nextMed)
                            viewModel.processVoiceMedicationInput("Haan beta, maine le li")
                        },
                        onSimulateMissed = {
                            viewModel.triggerReminder(nextMed)
                            viewModel.processVoiceMedicationInput("Nahi abhi nahi li")
                        },
                        onSimulateDelayed = {
                            viewModel.triggerReminder(nextMed)
                            viewModel.processVoiceMedicationInput("Thodi der baad lunga")
                        },
                        onSimulateNoResponse = {
                            viewModel.handleMedicationResponse(
                                MedicationStatus.NO_RESPONSE,
                                "TIMEOUT",
                                "Simulated multiple timeouts (Section 16 escalation)"
                            )
                        },
                        onSimulateSymptom = {
                            viewModel.showVoiceCompanionDialog.value = true
                            viewModel.sendCompanionMessage("Aaj mujhe thoda chakkar aa raha hai")
                        },
                        onSimulateEmergency = {
                            viewModel.showEmergencyDialog.value = true
                            viewModel.triggerEmergency()
                        },
                        onStartAICall = { viewModel.startSimulatedAICall() },
                        onTriggerNightRoutine = { viewModel.triggerNightRoutine() },
                        onGenerateDailyReport = { viewModel.generateDailyReport() },
                        onGenerateWeeklyReport = { viewModel.generateWeeklyReport() },
                        onResetDemoData = { viewModel.resetDemoData() }
                    )
                }
            }
        }
    }

    // --- Overlays and Modals ---

    if (showReminderDialog) {
        MedicationReminderDialog(
            medication = activeReminderMed,
            preferredName = patient?.preferredName ?: "Ravi ji",
            statusMessage = reminderStatusMsg,
            onAction = { status, source, notes ->
                viewModel.handleMedicationResponse(status, source, notes)
            },
            onVoiceInput = { transcript ->
                viewModel.processVoiceMedicationInput(transcript)
            },
            onDismiss = { viewModel.showReminderDialog.value = false }
        )
    }

    if (showVoiceCompanion) {
        VoiceCompanionDialog(
            preferredName = patient?.preferredName ?: "Ravi ji",
            messages = aiMessages,
            onSendMessage = { text -> viewModel.sendCompanionMessage(text) },
            onDismiss = { viewModel.showVoiceCompanionDialog.value = false }
        )
    }

    if (showHealthCheck) {
        HealthCheckInDialog(
            preferredName = patient?.preferredName ?: "Ravi ji",
            onSubmit = { mood, sleep, energy, symptoms, notes ->
                viewModel.submitHealthCheckIn(mood, sleep, energy, symptoms, notes)
            },
            onDismiss = { viewModel.showHealthCheckDialog.value = false }
        )
    }

    if (showEmergency) {
        EmergencyDialog(
            preferredName = patient?.preferredName ?: "Ravi ji",
            emergencyPhone = patient?.emergencyPhone ?: "108",
            caregiverName = patient?.caregiverName ?: "Priya Sharma",
            caregiverPhone = patient?.caregiverPhone ?: "+91 98123 45678",
            onEmergencyTriggered = {
                viewModel.triggerEmergency()
                viewModel.showEmergencyDialog.value = false
            },
            onDismiss = { viewModel.showEmergencyDialog.value = false }
        )
    }

    if (showNightRoutine) {
        NightRoutineDialog(
            preferredName = patient?.preferredName ?: "Ravi ji",
            onDismiss = { viewModel.showNightRoutineDialog.value = false }
        )
    }

    if (showSimulatedCall) {
        SimulatedAICallDialog(
            patientName = patient?.fullName ?: "Ravi Sharma",
            patientPhone = patient?.phone ?: "+91 98765 43210",
            callStatus = callStatus,
            transcript = callTranscript,
            onEndCall = { viewModel.endSimulatedCall() }
        )
    }

    if (showAddMedication) {
        AddMedicationBottomSheet(
            onAdd = { name, dosage, frequency, time, instructions, doctor, notes ->
                viewModel.addMedication(name, dosage, frequency, time, instructions, doctor, notes)
            },
            onDismiss = { viewModel.showAddMedicationDialog.value = false }
        )
    }

    if (showReportDetail) {
        ReportDetailDialog(
            report = selectedReport,
            patientName = patient?.fullName ?: "Ravi Sharma",
            onDismiss = { viewModel.showReportDetailDialog.value = false }
        )
    }

    if (showComplianceHistory) {
        ComplianceHistoryDialog(
            logs = medicationLogs,
            stats = complianceStats,
            onDismiss = { viewModel.showComplianceHistoryDialog.value = false }
        )
    }
}
