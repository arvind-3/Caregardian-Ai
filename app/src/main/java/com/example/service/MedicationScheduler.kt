package com.example.service

import android.content.Context
import androidx.work.*
import com.example.data.model.Medication
import com.example.data.model.Patient
import java.util.Calendar
import java.util.concurrent.TimeUnit

object MedicationScheduler {

    const val TAG_ALL_REMINDERS = "careguardian_all_med_reminders"

    fun scheduleMedication(
        context: Context,
        medication: Medication,
        patient: Patient?,
        style: HindiTemplateStyle = HindiTemplateStyle.HINGLISH_RESPECTFUL
    ) {
        if (!medication.active) {
            cancel(context, medication.id)
            return
        }

        val delayMs = calculateDelayToScheduledTime(medication.scheduledTime)
        val data = workDataOf(
            MedicationReminderWorker.KEY_MEDICATION_ID to medication.id,
            MedicationReminderWorker.KEY_MEDICATION_NAME to medication.name,
            MedicationReminderWorker.KEY_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_SCHEDULED_TIME to medication.scheduledTime,
            MedicationReminderWorker.KEY_PATIENT_NAME to (patient?.preferredName ?: "Ravi ji"),
            MedicationReminderWorker.KEY_INSTRUCTIONS to medication.instructions,
            MedicationReminderWorker.KEY_TEMPLATE_STYLE to style.name
        )

        val workRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag(TAG_ALL_REMINDERS)
            .addTag("med_${medication.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "med_reminder_${medication.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleAll(
        context: Context,
        medications: List<Medication>,
        patient: Patient?,
        style: HindiTemplateStyle = HindiTemplateStyle.HINGLISH_RESPECTFUL
    ) {
        medications.filter { it.active }.forEach { med ->
            scheduleMedication(context, med, patient, style)
        }
    }

    fun triggerInstantTestReminder(
        context: Context,
        medication: Medication,
        patient: Patient?,
        style: HindiTemplateStyle = HindiTemplateStyle.HINGLISH_RESPECTFUL
    ) {
        val data = workDataOf(
            MedicationReminderWorker.KEY_MEDICATION_ID to medication.id,
            MedicationReminderWorker.KEY_MEDICATION_NAME to medication.name,
            MedicationReminderWorker.KEY_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_SCHEDULED_TIME to medication.scheduledTime,
            MedicationReminderWorker.KEY_PATIENT_NAME to (patient?.preferredName ?: "Ravi ji"),
            MedicationReminderWorker.KEY_INSTRUCTIONS to medication.instructions,
            MedicationReminderWorker.KEY_TEMPLATE_STYLE to style.name
        )

        val immediateRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInputData(data)
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(immediateRequest)
    }

    fun cancel(context: Context, medicationId: String) {
        WorkManager.getInstance(context).cancelUniqueWork("med_reminder_$medicationId")
    }

    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG_ALL_REMINDERS)
    }

    internal fun calculateDelayToScheduledTime(timeStr: String): Long {
        val (hour, minute) = parseHourAndMinute(timeStr)
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the scheduled time for today has already passed, schedule for tomorrow
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)
    }

    private fun parseHourAndMinute(timeStr: String): Pair<Int, Int> {
        val clean = timeStr.trim().uppercase()
        val isPm = clean.endsWith("PM")
        val isAm = clean.endsWith("AM")
        val numericPart = clean.replace("AM", "").replace("PM", "").trim()

        val parts = numericPart.split(":")
        var hour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

        if (isPm && hour < 12) hour += 12
        if (isAm && hour == 12) hour = 0

        return Pair(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
    }
}
