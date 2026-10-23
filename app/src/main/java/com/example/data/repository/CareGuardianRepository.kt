package com.example.data.repository

import com.example.data.dao.CareGuardianDao
import com.example.data.dao.ComplianceStats
import com.example.data.dao.MedicationLogDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*

class CareGuardianRepository(
    private val dao: CareGuardianDao,
    private val logDao: MedicationLogDao
) {

    val patient: Flow<Patient?> = dao.getPatient()

    fun getMedications(patientId: String): Flow<List<Medication>> =
        dao.getMedications(patientId)

    fun getMedicationEvents(patientId: String): Flow<List<MedicationEvent>> =
        dao.getMedicationEvents(patientId)

    fun getHealthCheckIns(patientId: String): Flow<List<HealthCheckIn>> =
        dao.getHealthCheckIns(patientId)

    fun getNotifications(patientId: String): Flow<List<CaregiverNotification>> =
        dao.getNotifications(patientId)

    fun getAIMessages(patientId: String): Flow<List<AIMessage>> =
        dao.getAIMessages(patientId)

    fun getHealthReports(patientId: String): Flow<List<HealthReport>> =
        dao.getHealthReports(patientId)

    // Historical Medication Compliance Logs
    fun getAllMedicationLogs(patientId: String): Flow<List<MedicationLog>> =
        logDao.getAllLogsForPatient(patientId)

    fun getLogsByDate(patientId: String, date: String): Flow<List<MedicationLog>> =
        logDao.getLogsByDate(patientId, date)

    fun getLogsBetween(patientId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<MedicationLog>> =
        logDao.getLogsBetween(patientId, startTimestamp, endTimestamp)

    fun getLogsForMedication(medicationId: String): Flow<List<MedicationLog>> =
        logDao.getLogsForMedication(medicationId)

    fun getComplianceStats(patientId: String): Flow<ComplianceStats> =
        combine(
            logDao.getTotalLogsCount(patientId),
            logDao.getTakenLogsCount(patientId),
            logDao.getMissedLogsCount(patientId),
            logDao.getDelayedLogsCount(patientId)
        ) { total, taken, missed, delayed ->
            val percent = if (total > 0) ((taken.toDouble() / total.toDouble()) * 100).toInt() else 95
            ComplianceStats(
                totalLogged = total,
                takenCount = taken,
                missedCount = missed,
                delayedCount = delayed,
                compliancePercent = percent
            )
        }

    suspend fun insertMedicationLog(log: MedicationLog) {
        logDao.insertLog(log)
    }

    suspend fun getPatientDirect(patientId: String): Patient? =
        dao.getPatientById(patientId)

    suspend fun initializeSeedDataIfEmpty() {
        // Check if patient exists
        val existingPatient = dao.getPatientById("patient_ravi_sharma")
        if (existingPatient == null) {
            val patient = Patient(
                id = "patient_ravi_sharma",
                fullName = "Ravi Sharma",
                preferredName = "Ravi ji",
                age = 72,
                gender = "Male",
                phone = "+91 98765 43210",
                email = "patient@careguardian.demo",
                address = "Flat 402, Shanti Kunj, New Delhi",
                preferredLanguage = "Hindi/Hinglish",
                existingConditions = "Hypertension, Mild Arthritis",
                allergies = "Penicillin",
                caregiverName = "Priya Sharma",
                caregiverPhone = "+91 98123 45678",
                caregiverEmail = "caregiver@careguardian.demo",
                emergencyContact = "Dr. Verma & Priya Sharma",
                emergencyPhone = "108 / +91 98123 45678",
                nightRoutineEnabled = true,
                nightRoutineTime = "21:00",
                voiceConsentGranted = true
            )
            dao.insertPatient(patient)

            // Seed Medications
            val medA = Medication(
                id = "med_a",
                patientId = patient.id,
                name = "Medicine A (Amlodipine)",
                dosage = "1 tablet",
                frequency = "Daily",
                scheduledTime = "08:00 AM",
                startDate = "2026-01-01",
                endDate = "2026-12-31",
                instructions = "After breakfast with warm water",
                doctorName = "Dr. R. K. Verma",
                notes = "Blood pressure control"
            )
            val medB = Medication(
                id = "med_b",
                patientId = patient.id,
                name = "Medicine B (Metformin)",
                dosage = "1 tablet",
                frequency = "Daily",
                scheduledTime = "01:00 PM",
                startDate = "2026-01-01",
                endDate = "2026-12-31",
                instructions = "Take after lunch",
                doctorName = "Dr. R. K. Verma",
                notes = "Sugar control"
            )
            val medC = Medication(
                id = "med_c",
                patientId = patient.id,
                name = "Medicine C (Calcium & D3)",
                dosage = "1 tablet",
                frequency = "Daily",
                scheduledTime = "08:00 PM",
                startDate = "2026-01-01",
                endDate = "2026-12-31",
                instructions = "Take after dinner",
                doctorName = "Dr. R. K. Verma",
                notes = "Bone health"
            )
            dao.insertMedication(medA)
            dao.insertMedication(medB)
            dao.insertMedication(medC)

            // Seed initial events for today
            val now = System.currentTimeMillis()
            dao.insertMedicationEvent(
                MedicationEvent(
                    id = "event_today_morning",
                    patientId = patient.id,
                    medicationId = medA.id,
                    medicationName = medA.name,
                    dosage = medA.dosage,
                    scheduledTime = medA.scheduledTime,
                    responseTimestamp = now - 14400000L, // 4 hours ago
                    status = MedicationStatus.TAKEN,
                    source = "VOICE",
                    confidence = 0.96f,
                    notes = "Confirmed via voice: 'Haan beta maine le li'"
                )
            )

            // Seed initial health check-in
            dao.insertHealthCheckIn(
                HealthCheckIn(
                    id = "checkin_initial",
                    patientId = patient.id,
                    timestamp = now - 7200000L, // 2 hours ago
                    mood = "GOOD",
                    sleep = "NORMAL",
                    energy = "MEDIUM",
                    symptoms = "Mild stiffness in morning",
                    notes = "Routine morning check-in completed"
                )
            )

            // Seed initial notification
            dao.insertNotification(
                CaregiverNotification(
                    id = "notif_morning",
                    patientId = patient.id,
                    type = AlertType.SYSTEM_ALERT,
                    title = "✅ Medication Update",
                    message = "Ravi ji has taken Medicine A (Amlodipine) at 08:04 AM.",
                    timestamp = now - 14200000L,
                    isRead = true,
                    severity = AlertSeverity.INFO
                )
            )

            // Initial AI Welcome Message
            dao.insertAIMessage(
                AIMessage(
                    conversationId = "conv_default",
                    patientId = patient.id,
                    sender = "ASSISTANT",
                    text = "Namaste Ravi ji! Main CareGuardian hoon. Main aapki dawai aur health ka dhyan rakhne ke liye yahan hoon. Aap mujhse kabhi bhi Hindi ya English mein baat kar sakte hain.",
                    timestamp = now - 10000L,
                    isVoice = true
                )
            )

            // Seed historical 7-day medication compliance logs
            val days = listOf("Thursday", "Wednesday", "Tuesday", "Monday", "Sunday", "Saturday", "Friday")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for (i in 0..6) {
                val dayTimestamp = now - (i * 86400000L)
                val dateStr = dateFormat.format(Date(dayTimestamp))
                val dayName = days[i % days.size]

                // Morning med A
                logDao.insertLog(
                    MedicationLog(
                        id = "log_med_a_day_$i",
                        patientId = patient.id,
                        medicationId = medA.id,
                        medicationName = medA.name,
                        dosage = medA.dosage,
                        scheduledTime = medA.scheduledTime,
                        logDate = dateStr,
                        timestamp = dayTimestamp - 14400000L,
                        status = MedicationStatus.TAKEN,
                        wasTakenOnTime = true,
                        delayMinutes = 0,
                        responseSource = if (i == 0) "VOICE" else "SWITCH",
                        notes = "Taken on schedule with water",
                        dayOfWeek = dayName
                    )
                )

                // Afternoon med B
                val statusB = if (i == 4) MedicationStatus.MISSED else MedicationStatus.TAKEN
                logDao.insertLog(
                    MedicationLog(
                        id = "log_med_b_day_$i",
                        patientId = patient.id,
                        medicationId = medB.id,
                        medicationName = medB.name,
                        dosage = medB.dosage,
                        scheduledTime = medB.scheduledTime,
                        logDate = dateStr,
                        timestamp = dayTimestamp - 7200000L,
                        status = statusB,
                        wasTakenOnTime = statusB == MedicationStatus.TAKEN,
                        delayMinutes = if (statusB == MedicationStatus.MISSED) 0 else 5,
                        responseSource = "SWITCH",
                        notes = if (statusB == MedicationStatus.MISSED) "Missed during clinic travel" else "Taken after lunch",
                        dayOfWeek = dayName
                    )
                )

                // Evening med C
                val statusC = if (i == 3) MedicationStatus.DELAYED else MedicationStatus.TAKEN
                logDao.insertLog(
                    MedicationLog(
                        id = "log_med_c_day_$i",
                        patientId = patient.id,
                        medicationId = medC.id,
                        medicationName = medC.name,
                        dosage = medC.dosage,
                        scheduledTime = medC.scheduledTime,
                        logDate = dateStr,
                        timestamp = dayTimestamp - 3600000L,
                        status = statusC,
                        wasTakenOnTime = statusC == MedicationStatus.TAKEN,
                        delayMinutes = if (statusC == MedicationStatus.DELAYED) 30 else 0,
                        responseSource = "SWITCH",
                        notes = if (statusC == MedicationStatus.DELAYED) "Delayed 30 mins after dinner" else "Taken with milk",
                        dayOfWeek = dayName
                    )
                )
            }
        }
    }

    suspend fun addMedication(medication: Medication) {
        dao.insertMedication(medication)
    }

    suspend fun updateMedication(medication: Medication) {
        dao.updateMedication(medication)
    }

    suspend fun deleteMedication(medication: Medication) {
        dao.deleteMedication(medication)
    }

    suspend fun recordMedicationStatus(
        eventId: String,
        patientId: String,
        medicationId: String,
        medicationName: String,
        dosage: String,
        scheduledTime: String,
        status: MedicationStatus,
        source: String,
        confidence: Float,
        notes: String
    ) {
        val event = MedicationEvent(
            id = eventId,
            patientId = patientId,
            medicationId = medicationId,
            medicationName = medicationName,
            dosage = dosage,
            scheduledTime = scheduledTime,
            responseTimestamp = System.currentTimeMillis(),
            status = status,
            source = source,
            confidence = confidence,
            notes = notes
        )
        dao.insertMedicationEvent(event)

        // Also persist as historical medication log
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
        logDao.insertLog(
            MedicationLog(
                id = UUID.randomUUID().toString(),
                patientId = patientId,
                medicationId = medicationId,
                medicationName = medicationName,
                dosage = dosage,
                scheduledTime = scheduledTime,
                logDate = todayDate,
                timestamp = System.currentTimeMillis(),
                status = status,
                wasTakenOnTime = status == MedicationStatus.TAKEN,
                delayMinutes = if (status == MedicationStatus.DELAYED) 30 else 0,
                responseSource = source,
                notes = notes,
                dayOfWeek = dayOfWeek
            )
        )

        // Generate Caregiver Notification based on status
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        when (status) {
            MedicationStatus.TAKEN -> {
                dao.insertNotification(
                    CaregiverNotification(
                        patientId = patientId,
                        type = AlertType.SYSTEM_ALERT,
                        title = "✅ Medication Update",
                        message = "Ravi ji has taken $medicationName at $timeStr.",
                        severity = AlertSeverity.INFO
                    )
                )
            }
            MedicationStatus.MISSED -> {
                dao.insertNotification(
                    CaregiverNotification(
                        patientId = patientId,
                        type = AlertType.MEDICATION_MISSED,
                        title = "⚠️ Medication Alert",
                        message = "Ravi ji has marked $medicationName as MISSED or declined to take it. Please check on the patient.",
                        severity = AlertSeverity.WARNING
                    )
                )
            }
            MedicationStatus.DELAYED -> {
                dao.insertNotification(
                    CaregiverNotification(
                        patientId = patientId,
                        type = AlertType.MEDICATION_MISSED,
                        title = "⏳ Medication Delayed",
                        message = "Ravi ji requested to take $medicationName later ($timeStr).",
                        severity = AlertSeverity.INFO
                    )
                )
            }
            MedicationStatus.NO_RESPONSE -> {
                dao.insertNotification(
                    CaregiverNotification(
                        patientId = patientId,
                        type = AlertType.NO_RESPONSE,
                        title = "🚨 Patient Check Required",
                        message = "Ravi ji did not respond to the medication reminder after multiple alerts. Please contact the patient immediately.",
                        severity = AlertSeverity.CRITICAL
                    )
                )
            }
            else -> {}
        }
    }

    suspend fun recordHealthCheckIn(checkIn: HealthCheckIn) {
        dao.insertHealthCheckIn(checkIn)
        if (checkIn.symptoms.isNotBlank() && !checkIn.symptoms.equals("None", ignoreCase = true)) {
            dao.insertNotification(
                CaregiverNotification(
                    patientId = checkIn.patientId,
                    type = AlertType.SYMPTOM_REPORTED,
                    title = "❤️ Health Check-in Alert",
                    message = "Ravi ji reported symptom: '${checkIn.symptoms}'. Mood: ${checkIn.mood}, Energy: ${checkIn.energy}.",
                    severity = AlertSeverity.WARNING
                )
            )
        }
    }

    suspend fun sendCaregiverAlert(
        patientId: String,
        type: AlertType,
        title: String,
        message: String,
        severity: AlertSeverity
    ) {
        dao.insertNotification(
            CaregiverNotification(
                patientId = patientId,
                type = type,
                title = title,
                message = message,
                severity = severity
            )
        )
    }

    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead(patientId: String) {
        dao.markAllNotificationsAsRead(patientId)
    }

    suspend fun saveAIMessage(message: AIMessage) {
        dao.insertAIMessage(message)
    }

    suspend fun clearConversation(patientId: String) {
        dao.clearConversation(patientId)
    }

    suspend fun saveHealthReport(report: HealthReport) {
        dao.insertHealthReport(report)
    }

    suspend fun resetDemoData() {
        dao.clearMedicationEvents()
        dao.clearHealthCheckIns()
        dao.clearNotifications()
        dao.clearHealthReports()
        logDao.clearLogsForPatient("patient_ravi_sharma")
        dao.clearConversation("patient_ravi_sharma")
        // Re-initialize
        initializeSeedDataIfEmpty()
    }
}
