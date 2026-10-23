package com.example.data.dao

import androidx.room.*
import com.example.data.model.MedicationLog
import kotlinx.coroutines.flow.Flow

data class ComplianceStats(
    val totalLogged: Int,
    val takenCount: Int,
    val missedCount: Int,
    val delayedCount: Int,
    val compliancePercent: Int
)

@Dao
interface MedicationLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<MedicationLog>)

    @Update
    suspend fun updateLog(log: MedicationLog)

    @Delete
    suspend fun deleteLog(log: MedicationLog)

    @Query("SELECT * FROM medication_logs WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getAllLogsForPatient(patientId: String): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE patientId = :patientId AND logDate = :date ORDER BY timestamp DESC")
    fun getLogsByDate(patientId: String, date: String): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE patientId = :patientId AND timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getLogsBetween(patientId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    fun getLogsForMedication(medicationId: String): Flow<List<MedicationLog>>

    @Query("SELECT COUNT(*) FROM medication_logs WHERE patientId = :patientId")
    fun getTotalLogsCount(patientId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM medication_logs WHERE patientId = :patientId AND status = 'TAKEN'")
    fun getTakenLogsCount(patientId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM medication_logs WHERE patientId = :patientId AND status = 'MISSED'")
    fun getMissedLogsCount(patientId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM medication_logs WHERE patientId = :patientId AND status = 'DELAYED'")
    fun getDelayedLogsCount(patientId: String): Flow<Int>

    @Query("DELETE FROM medication_logs WHERE patientId = :patientId")
    suspend fun clearLogsForPatient(patientId: String)
}
