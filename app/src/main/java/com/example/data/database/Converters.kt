package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.AlertSeverity
import com.example.data.model.AlertType
import com.example.data.model.MedicationStatus

class Converters {
    @TypeConverter
    fun fromMedicationStatus(status: MedicationStatus): String = status.name

    @TypeConverter
    fun toMedicationStatus(value: String): MedicationStatus = runCatching {
        MedicationStatus.valueOf(value)
    }.getOrDefault(MedicationStatus.SCHEDULED)

    @TypeConverter
    fun fromAlertType(type: AlertType): String = type.name

    @TypeConverter
    fun toAlertType(value: String): AlertType = runCatching {
        AlertType.valueOf(value)
    }.getOrDefault(AlertType.SYSTEM_ALERT)

    @TypeConverter
    fun fromAlertSeverity(severity: AlertSeverity): String = severity.name

    @TypeConverter
    fun toAlertSeverity(value: String): AlertSeverity = runCatching {
        AlertSeverity.valueOf(value)
    }.getOrDefault(AlertSeverity.INFO)
}
