package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val preferredName: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val email: String,
    val address: String,
    val preferredLanguage: String = "Hindi/Hinglish",
    val existingConditions: String,
    val allergies: String,
    val caregiverName: String,
    val caregiverPhone: String,
    val caregiverEmail: String,
    val emergencyContact: String,
    val emergencyPhone: String,
    val nightRoutineEnabled: Boolean = true,
    val nightRoutineTime: String = "21:00",
    val voiceConsentGranted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val scheduledTime: String,
    val startDate: String,
    val endDate: String,
    val instructions: String,
    val doctorName: String,
    val notes: String,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MedicationStatus {
    SCHEDULED,
    TAKEN,
    MISSED,
    DELAYED,
    UNCLEAR,
    NO_RESPONSE
}

@Entity(tableName = "medication_events")
data class MedicationEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val medicationId: String,
    val medicationName: String,
    val dosage: String,
    val scheduledTime: String,
    val responseTimestamp: Long = System.currentTimeMillis(),
    val status: MedicationStatus = MedicationStatus.SCHEDULED,
    val source: String = "SYSTEM", // VOICE, BUTTON, SIMULATION, TIMEOUT
    val confidence: Float = 1.0f,
    val notes: String = "",
    val escalationStage: Int = 0 // 0=Initial, 1=Second Reminder, 2=Caregiver Escalated
)

@Entity(tableName = "health_checkins")
data class HealthCheckIn(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String, // GREAT, GOOD, OKAY, TIRED, UNWELL
    val sleep: String, // VERY_GOOD, NORMAL, DISTURBED, POOR
    val energy: String, // HIGH, MEDIUM, LOW
    val symptoms: String = "", // comma-separated or json
    val notes: String = ""
)

enum class AlertType {
    MEDICATION_MISSED,
    NO_RESPONSE,
    SYMPTOM_REPORTED,
    POSSIBLE_EMERGENCY,
    SYSTEM_ALERT
}

enum class AlertSeverity {
    INFO,
    WARNING,
    URGENT,
    CRITICAL
}

@Entity(tableName = "caregiver_notifications")
data class CaregiverNotification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val type: AlertType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val severity: AlertSeverity = AlertSeverity.INFO
)

@Entity(tableName = "ai_messages")
data class AIMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val patientId: String,
    val sender: String, // USER, ASSISTANT
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = true,
    val detectedSymptom: String? = null,
    val detectedIntent: String? = null
)

@Entity(tableName = "health_reports")
data class HealthReport(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val reportType: String, // DAILY, WEEKLY
    val date: String,
    val adherencePercent: Int,
    val takenCount: Int,
    val missedCount: Int,
    val delayedCount: Int,
    val noResponseCount: Int,
    val moodSummary: String,
    val sleepSummary: String,
    val energySummary: String,
    val reportedSymptoms: String,
    val keyFindings: String,
    val caregiverAction: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "medication_logs",
    indices = [
        androidx.room.Index(value = ["patientId"]),
        androidx.room.Index(value = ["medicationId"]),
        androidx.room.Index(value = ["logDate"]),
        androidx.room.Index(value = ["timestamp"])
    ]
)
data class MedicationLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val medicationId: String,
    val medicationName: String,
    val dosage: String,
    val scheduledTime: String,
    val logDate: String, // e.g. "2026-10-22"
    val timestamp: Long = System.currentTimeMillis(),
    val status: MedicationStatus = MedicationStatus.TAKEN,
    val wasTakenOnTime: Boolean = true,
    val delayMinutes: Int = 0,
    val responseSource: String = "SWITCH", // VOICE, SWITCH, BUTTON, NOTIFICATION, TIMEOUT
    val notes: String = "",
    val dayOfWeek: String = "Thursday"
)
