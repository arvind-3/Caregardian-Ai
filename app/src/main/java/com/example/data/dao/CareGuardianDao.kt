package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CareGuardianDao {

    // Patient
    @Query("SELECT * FROM patients LIMIT 1")
    fun getPatient(): Flow<Patient?>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientById(id: String): Patient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    // Medications
    @Query("SELECT * FROM medications WHERE patientId = :patientId ORDER BY scheduledTime ASC")
    fun getMedications(patientId: String): Flow<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    // Medication Events
    @Query("SELECT * FROM medication_events WHERE patientId = :patientId ORDER BY responseTimestamp DESC")
    fun getMedicationEvents(patientId: String): Flow<List<MedicationEvent>>

    @Query("SELECT * FROM medication_events WHERE id = :id LIMIT 1")
    suspend fun getMedicationEventById(id: String): MedicationEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationEvent(event: MedicationEvent)

    @Update
    suspend fun updateMedicationEvent(event: MedicationEvent)

    // Health Check-ins
    @Query("SELECT * FROM health_checkins WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getHealthCheckIns(patientId: String): Flow<List<HealthCheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthCheckIn(checkIn: HealthCheckIn)

    // Caregiver Notifications
    @Query("SELECT * FROM caregiver_notifications WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getNotifications(patientId: String): Flow<List<CaregiverNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: CaregiverNotification)

    @Query("UPDATE caregiver_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE caregiver_notifications SET isRead = 1 WHERE patientId = :patientId")
    suspend fun markAllNotificationsAsRead(patientId: String)

    // AI Messages
    @Query("SELECT * FROM ai_messages WHERE patientId = :patientId ORDER BY timestamp ASC")
    fun getAIMessages(patientId: String): Flow<List<AIMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIMessage(message: AIMessage)

    @Query("DELETE FROM ai_messages WHERE patientId = :patientId")
    suspend fun clearConversation(patientId: String)

    // Health Reports
    @Query("SELECT * FROM health_reports WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getHealthReports(patientId: String): Flow<List<HealthReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthReport(report: HealthReport)

    // Purge/Reset for Demo
    @Query("DELETE FROM medication_events")
    suspend fun clearMedicationEvents()

    @Query("DELETE FROM health_checkins")
    suspend fun clearHealthCheckIns()

    @Query("DELETE FROM caregiver_notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM health_reports")
    suspend fun clearHealthReports()
}
