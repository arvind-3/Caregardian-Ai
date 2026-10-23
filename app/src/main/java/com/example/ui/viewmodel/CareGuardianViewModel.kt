package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.CareGuardianRepository
import com.example.service.AIIntentEngine
import com.example.service.CareGuardianTTS
import com.example.service.HindiReminderTemplates
import com.example.service.HindiTemplateStyle
import com.example.service.MedicationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppMode {
    DASHBOARD,
    PATIENT,
    CAREGIVER,
    DEMO_LAB
}

class CareGuardianViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CareGuardianRepository
    val tts: CareGuardianTTS = CareGuardianTTS(application)

    private val _currentMode = MutableStateFlow(AppMode.DASHBOARD)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    // Dialog & overlay states
    val showReminderDialog = MutableStateFlow(false)
    val activeReminderMedication = MutableStateFlow<Medication?>(null)
    val reminderStatusMessage = MutableStateFlow<String?>(null)

    val showVoiceCompanionDialog = MutableStateFlow(false)
    val showHealthCheckDialog = MutableStateFlow(false)
    val showEmergencyDialog = MutableStateFlow(false)
    val showNightRoutineDialog = MutableStateFlow(false)
    val showSimulatedCallDialog = MutableStateFlow(false)
    val showAddMedicationDialog = MutableStateFlow(false)
    val showReportDetailDialog = MutableStateFlow(false)
    val selectedReport = MutableStateFlow<HealthReport?>(null)

    // Call Simulation state
    val callDurationSeconds = MutableStateFlow(0)
    val callStatus = MutableStateFlow("Dialing...")
    val callTranscript = MutableStateFlow<List<Pair<String, String>>>(emptyList())

    // Demo Sequence Status
    val demoSequenceMessage = MutableStateFlow<String?>(null)
    val isDemoRunning = MutableStateFlow(false)

    val showComplianceHistoryDialog = MutableStateFlow(false)
    val selectedHindiStyle = MutableStateFlow(HindiTemplateStyle.HINGLISH_RESPECTFUL)
    val testNotificationFeedback = MutableStateFlow<String?>(null)

    init {
        val db = AppDatabase.getInstance(application)
        repository = CareGuardianRepository(db.careGuardianDao(), db.medicationLogDao())
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
            // Schedule all active medications via WorkManager
            val p = repository.patient.firstOrNull()
            if (p != null) {
                val currentMeds = repository.getMedications(p.id).firstOrNull() ?: emptyList()
                MedicationScheduler.scheduleAll(application, currentMeds, p, selectedHindiStyle.value)
            }
        }
    }

    val patient: StateFlow<Patient?> = repository.patient
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val medications: StateFlow<List<Medication>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getMedications(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicationEvents: StateFlow<List<MedicationEvent>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getMedicationEvents(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthCheckIns: StateFlow<List<HealthCheckIn>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getHealthCheckIns(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<CaregiverNotification>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getNotifications(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMessages: StateFlow<List<AIMessage>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getAIMessages(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthReports: StateFlow<List<HealthReport>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getHealthReports(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicationLogs: StateFlow<List<MedicationLog>> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getAllMedicationLogs(p.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val complianceStats: StateFlow<com.example.data.dao.ComplianceStats?> = patient
        .flatMapLatest { p ->
            if (p != null) repository.getComplianceStats(p.id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived states
    val nextMedication: StateFlow<Medication?> = medications.map { list ->
        list.firstOrNull { it.active }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adherencePercentage: StateFlow<Int> = medicationEvents.map { events ->
        if (events.isEmpty()) 92 // default baseline
        else {
            val taken = events.count { it.status == MedicationStatus.TAKEN }
            val total = events.size
            if (total > 0) ((taken.toDouble() / total.toDouble()) * 100).toInt() else 92
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 92)

    val unreadAlertsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setMode(mode: AppMode) {
        _currentMode.value = mode
    }

    // --- Medication Actions ---
    fun triggerReminder(med: Medication? = null) {
        val targetMed = med ?: nextMedication.value ?: medications.value.firstOrNull() ?: return
        activeReminderMedication.value = targetMed
        reminderStatusMessage.value = null
        showReminderDialog.value = true

        val preferredName = patient.value?.preferredName ?: "Ravi ji"
        val speakText = "Namaste $preferredName, aapki ${targetMed.name} lene ka time ho gaya hai. Kya aapne dawai le li?"
        tts.speak(speakText)
    }

    fun handleMedicationResponse(status: MedicationStatus, source: String, notes: String) {
        val med = activeReminderMedication.value ?: return
        val p = patient.value ?: return

        viewModelScope.launch {
            repository.recordMedicationStatus(
                eventId = UUID.randomUUID().toString(),
                patientId = p.id,
                medicationId = med.id,
                medicationName = med.name,
                dosage = med.dosage,
                scheduledTime = med.scheduledTime,
                status = status,
                source = source,
                confidence = 0.98f,
                notes = notes
            )
            delay(400)
            showReminderDialog.value = false
        }
    }

    fun toggleMedicationTaken(medication: Medication, isTaken: Boolean) {
        val p = patient.value ?: return
        viewModelScope.launch {
            val newStatus = if (isTaken) MedicationStatus.TAKEN else MedicationStatus.SCHEDULED
            val notes = if (isTaken) "Marked as taken via medication switch" else "Marked as pending/scheduled"
            repository.recordMedicationStatus(
                eventId = UUID.randomUUID().toString(),
                patientId = p.id,
                medicationId = medication.id,
                medicationName = medication.name,
                dosage = medication.dosage,
                scheduledTime = medication.scheduledTime,
                status = newStatus,
                source = "SWITCH",
                confidence = 1.0f,
                notes = notes
            )
            if (isTaken) {
                tts.speak("Shukriya ${p.preferredName}. ${medication.name} taken mark kar di gayi hai.")
            }
        }
    }

    fun markMedicationTakenById(medId: String) {
        val med = medications.value.firstOrNull { it.id == medId } ?: return
        toggleMedicationTaken(med, true)
    }

    fun triggerTestHindiReminder(med: Medication, style: HindiTemplateStyle = selectedHindiStyle.value) {
        val p = patient.value
        val app = getApplication<Application>()
        MedicationScheduler.triggerInstantTestReminder(app, med, p, style)

        val reminderMsg = HindiReminderTemplates.generate(
            patientName = p?.preferredName ?: "Ravi ji",
            medicationName = med.name,
            dosage = med.dosage,
            scheduledTime = med.scheduledTime,
            instructions = med.instructions,
            style = style
        )
        tts.speak(reminderMsg.speakableText)
        testNotificationFeedback.value = "Notification sent: \"${reminderMsg.title}\""
        viewModelScope.launch {
            delay(3500)
            testNotificationFeedback.value = null
        }
    }

    fun processVoiceMedicationInput(transcript: String) {
        val med = activeReminderMedication.value ?: return
        val p = patient.value ?: return
        val preferredName = p.preferredName

        val analysis = AIIntentEngine.analyzeMedicationResponse(transcript, preferredName)
        reminderStatusMessage.value = analysis.explanation
        tts.speak(analysis.explanation)

        if (!analysis.requiresClarification) {
            viewModelScope.launch {
                delay(1500)
                repository.recordMedicationStatus(
                    eventId = UUID.randomUUID().toString(),
                    patientId = p.id,
                    medicationId = med.id,
                    medicationName = med.name,
                    dosage = med.dosage,
                    scheduledTime = med.scheduledTime,
                    status = analysis.status,
                    source = "VOICE",
                    confidence = analysis.confidence,
                    notes = "Voice transcript: \"$transcript\" (Confidence: ${analysis.confidence})"
                )
                showReminderDialog.value = false
            }
        }
    }

    // --- AI Companion Chat ---
    fun sendCompanionMessage(text: String) {
        val p = patient.value ?: return
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        val preferredName = p.preferredName
        val meds = medications.value

        viewModelScope.launch {
            // Save user message
            repository.saveAIMessage(
                AIMessage(
                    conversationId = "conv_active",
                    patientId = p.id,
                    sender = "USER",
                    text = trimmed,
                    isVoice = true
                )
            )

            // AI analysis
            val result = AIIntentEngine.analyzeCompanionMessage(trimmed, preferredName, meds)

            // Emergency trigger if detected
            if (result.isEmergency) {
                showEmergencyDialog.value = true
            }

            // Caregiver notification if symptom or advice sought
            if (result.requiresCaregiverAlert && result.alertMessage != null) {
                repository.sendCaregiverAlert(
                    patientId = p.id,
                    type = if (result.isEmergency) AlertType.POSSIBLE_EMERGENCY else AlertType.SYMPTOM_REPORTED,
                    title = if (result.isEmergency) "🚨 EMERGENCY DETECTED" else "⚠️ Patient Observation",
                    message = result.alertMessage,
                    severity = if (result.isEmergency) AlertSeverity.CRITICAL else AlertSeverity.WARNING
                )
            }

            // Delay for natural conversational pacing
            delay(500)

            // Save assistant message
            repository.saveAIMessage(
                AIMessage(
                    conversationId = "conv_active",
                    patientId = p.id,
                    sender = "ASSISTANT",
                    text = result.spokenResponse,
                    isVoice = true,
                    detectedSymptom = result.detectedSymptom
                )
            )

            // Speak response aloud
            tts.speak(result.spokenResponse)
        }
    }

    // --- Health Check-in ---
    fun submitHealthCheckIn(
        mood: String,
        sleep: String,
        energy: String,
        symptoms: String,
        notes: String
    ) {
        val p = patient.value ?: return
        viewModelScope.launch {
            repository.recordHealthCheckIn(
                HealthCheckIn(
                    patientId = p.id,
                    mood = mood,
                    sleep = sleep,
                    energy = energy,
                    symptoms = symptoms,
                    notes = notes
                )
            )
            val ack = "Dhanyawad ${p.preferredName}. Aapka health check-in update ho gaya hai. Aap aaram kijiye."
            tts.speak(ack)
            showHealthCheckDialog.value = false
        }
    }

    // --- Emergency Workflow ---
    fun triggerEmergency() {
        val p = patient.value ?: return
        viewModelScope.launch {
            repository.sendCaregiverAlert(
                patientId = p.id,
                type = AlertType.POSSIBLE_EMERGENCY,
                title = "🚨 EMERGENCY HELP ACTIVATED",
                message = "${p.fullName} (${p.preferredName}) pressed Emergency Help. Emergency contacts & ambulance notified.",
                severity = AlertSeverity.CRITICAL
            )
            tts.speak("Emergency alert send kar diya gaya hai. Caregiver aur emergency service ko call kiya ja raha hai.")
        }
    }

    // --- 9:00 PM Night Routine ---
    fun triggerNightRoutine() {
        val p = patient.value ?: return
        showNightRoutineDialog.value = true
        val speech = "${p.preferredName}, aaj ki routine complete ho gayi. Ab aap aaram kijiye. Good night ${p.preferredName}. Achhi neend aaye. Kal subah phir milte hain."
        tts.speak(speech)
    }

    // --- Simulated AI Call (Caregiver initiated) ---
    fun startSimulatedAICall() {
        val p = patient.value ?: return
        showSimulatedCallDialog.value = true
        callDurationSeconds.value = 0
        callStatus.value = "Calling ${p.fullName} (${p.phone})..."
        callTranscript.value = emptyList()

        viewModelScope.launch {
            delay(1500)
            callStatus.value = "Connected • Live AI Call"
            val aiGreeting = "Namaste ${p.preferredName}, main CareGuardian AI bol raha hoon. Aap kaisa mehsoos kar rahe hain?"
            callTranscript.value = listOf("CareGuardian AI" to aiGreeting)
            tts.speak(aiGreeting)

            delay(3500)
            val patientReply = "Haan beta, main theek hoon. Thoda sa kamar me dard tha bas."
            callTranscript.value = callTranscript.value + ("Ravi Sharma" to patientReply)

            delay(2500)
            val aiResponse = "Samajh gaya ${p.preferredName}. Maine aapki baat note kar li hai aur Priya ji ko inform kar diya hai. Aap dawai samay par le lijiye."
            callTranscript.value = callTranscript.value + ("CareGuardian AI" to aiResponse)
            tts.speak(aiResponse)

            // Record notification in dashboard
            repository.sendCaregiverAlert(
                patientId = p.id,
                type = AlertType.SYSTEM_ALERT,
                title = "📞 AI Voice Call Completed",
                message = "Simulated AI check-in call with ${p.fullName} completed. Patient reported mild back pain.",
                severity = AlertSeverity.INFO
            )
        }
    }

    fun endSimulatedCall() {
        tts.stop()
        showSimulatedCallDialog.value = false
    }

    // --- Reports ---
    fun generateDailyReport() {
        val p = patient.value ?: return
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            val adherence = adherencePercentage.value
            val report = HealthReport(
                patientId = p.id,
                reportType = "DAILY",
                date = dateStr,
                adherencePercent = adherence,
                takenCount = 2,
                missedCount = 0,
                delayedCount = 1,
                noResponseCount = 0,
                moodSummary = "Good",
                sleepSummary = "Normal (7 hours)",
                energySummary = "Medium",
                reportedSymptoms = "Mild morning dizziness, now stable",
                keyFindings = "Medication adherence is good ($adherence%). Morning blood pressure medication taken on time.",
                caregiverAction = "Verify evening calcium tablet confirmation. Monitor mild dizziness."
            )
            repository.saveHealthReport(report)
            selectedReport.value = report
            showReportDetailDialog.value = true
        }
    }

    fun generateWeeklyReport() {
        val p = patient.value ?: return
        viewModelScope.launch {
            val dateStr = "Week of " + SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            val report = HealthReport(
                patientId = p.id,
                reportType = "WEEKLY",
                date = dateStr,
                adherencePercent = 94,
                takenCount = 19,
                missedCount = 1,
                delayedCount = 1,
                noResponseCount = 0,
                moodSummary = "Stable / Positive trend across 7 days",
                sleepSummary = "Average 6.8 hrs/night, quality improved",
                energySummary = "Medium to High",
                reportedSymptoms = "Occasional joint stiffness, 1 episode of mild dizziness",
                keyFindings = "Weekly compliance target achieved (94%). Patient consistently responds to voice reminders within 2 minutes.",
                caregiverAction = "Review joint stiffness with Dr. Verma at next routine follow-up."
            )
            repository.saveHealthReport(report)
            selectedReport.value = report
            showReportDetailDialog.value = true
        }
    }

    // --- Add Medication ---
    fun addMedication(
        name: String,
        dosage: String,
        frequency: String,
        scheduledTime: String,
        instructions: String,
        doctorName: String,
        notes: String
    ) {
        val p = patient.value ?: return
        viewModelScope.launch {
            val med = Medication(
                patientId = p.id,
                name = name,
                dosage = dosage,
                frequency = frequency,
                scheduledTime = scheduledTime,
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                endDate = "2026-12-31",
                instructions = instructions,
                doctorName = doctorName,
                notes = notes
            )
            repository.addMedication(med)
            MedicationScheduler.scheduleMedication(application, med, p, selectedHindiStyle.value)
            repository.sendCaregiverAlert(
                patientId = p.id,
                type = AlertType.SYSTEM_ALERT,
                title = "💊 New Medication Added",
                message = "Caregiver added $name ($dosage, $scheduledTime) prescribed by $doctorName.",
                severity = AlertSeverity.INFO
            )
            showAddMedicationDialog.value = false
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val p = patient.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(p.id)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            demoSequenceMessage.value = "Demo database reset to initial seeds!"
            delay(2000)
            demoSequenceMessage.value = null
        }
    }

    // --- Guided Demonstration Sequence (Prompt Section 49) ---
    fun runCompleteDemoWorkflow() {
        if (isDemoRunning.value) return
        isDemoRunning.value = true

        viewModelScope.launch {
            // Step 1: Switch to caregiver view to show Ravi Sharma
            _currentMode.value = AppMode.CAREGIVER
            demoSequenceMessage.value = "Step 1: Caregiver Dashboard loaded for Ravi Sharma (Adherence 92%)."
            delay(2500)

            // Step 2: Set demo medication reminder
            demoSequenceMessage.value = "Step 2: Triggering scheduled medication reminder for Medicine A..."
            delay(1500)

            // Step 3: Switch to patient interface
            _currentMode.value = AppMode.PATIENT
            val med = nextMedication.value ?: medications.value.firstOrNull()
            triggerReminder(med)
            demoSequenceMessage.value = "Step 3: Patient interface opened. AI speaking reminder in Hindi/Hinglish."
            delay(4000)

            // Step 4: Patient answers "Haan beta, maine le li"
            demoSequenceMessage.value = "Step 4: Patient answers via voice: \"Haan beta, maine le li.\""
            processVoiceMedicationInput("Haan beta, maine le li")
            delay(3500)

            // Step 5: Switch back to caregiver dashboard showing updated notification
            _currentMode.value = AppMode.CAREGIVER
            demoSequenceMessage.value = "Step 5: Caregiver receives instant update: 'Ravi ji has taken Medicine A'."
            delay(3000)

            // Step 6: Simulate Symptom "Aaj mujhe thoda chakkar aa raha hai"
            _currentMode.value = AppMode.PATIENT
            showVoiceCompanionDialog.value = true
            demoSequenceMessage.value = "Step 6: Patient companion talk: 'Aaj mujhe thoda chakkar aa raha hai.'"
            sendCompanionMessage("Aaj mujhe thoda chakkar aa raha hai")
            delay(4000)

            // Step 7: Caregiver receives alert and generates report
            _currentMode.value = AppMode.CAREGIVER
            demoSequenceMessage.value = "Step 7: Caregiver Alert received for dizziness. Generating Daily Report..."
            generateDailyReport()
            delay(3000)

            // Step 8: Night routine
            _currentMode.value = AppMode.PATIENT
            showVoiceCompanionDialog.value = false
            showReportDetailDialog.value = false
            triggerNightRoutine()
            demoSequenceMessage.value = "Step 8: 9:00 PM Night Routine executed: 'Good night Ravi ji. Kal subah phir milte hain.'"
            delay(4000)

            demoSequenceMessage.value = "✅ Complete Workflow Demonstration Complete! All systems operational."
            isDemoRunning.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.shutdown()
    }
}
