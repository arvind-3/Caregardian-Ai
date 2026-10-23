package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.data.database.AppDatabase
import com.example.data.model.MedicationEvent
import com.example.data.model.MedicationStatus
import java.util.UUID

class MedicationReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "medication_reminders_channel"
        const val CHANNEL_NAME = "Medication Reminders (दवाई स्मरण)"
        const val KEY_MEDICATION_ID = "key_medication_id"
        const val KEY_MEDICATION_NAME = "key_medication_name"
        const val KEY_DOSAGE = "key_dosage"
        const val KEY_SCHEDULED_TIME = "key_scheduled_time"
        const val KEY_PATIENT_NAME = "key_patient_name"
        const val KEY_INSTRUCTIONS = "key_instructions"
        const val KEY_TEMPLATE_STYLE = "key_template_style"

        const val ACTION_MARK_TAKEN = "com.example.ACTION_MARK_TAKEN"
        const val EXTRA_MED_ID = "extra_med_id"
    }

    override suspend fun doWork(): Result {
        val medId = inputData.getString(KEY_MEDICATION_ID) ?: return Result.failure()
        val medName = inputData.getString(KEY_MEDICATION_NAME) ?: "Medication"
        val dosage = inputData.getString(KEY_DOSAGE) ?: "1 dose"
        val scheduledTime = inputData.getString(KEY_SCHEDULED_TIME) ?: "08:00 AM"
        val patientName = inputData.getString(KEY_PATIENT_NAME) ?: "Ravi ji"
        val instructions = inputData.getString(KEY_INSTRUCTIONS) ?: ""
        val styleName = inputData.getString(KEY_TEMPLATE_STYLE) ?: HindiTemplateStyle.HINGLISH_RESPECTFUL.name

        val style = try {
            HindiTemplateStyle.valueOf(styleName)
        } catch (_: Exception) {
            HindiTemplateStyle.HINGLISH_RESPECTFUL
        }

        // Check if database still has active medication
        val db = AppDatabase.getInstance(context)
        val patientDao = db.careGuardianDao()
        val currentPatient = patientDao.getPatient()
        val patientId = currentPatient?.id ?: "patient_1"

        // Generate Hindi / Hinglish message
        val reminder = HindiReminderTemplates.generate(
            patientName = patientName,
            medicationName = medName,
            dosage = dosage,
            scheduledTime = scheduledTime,
            instructions = instructions,
            style = style
        )

        // Ensure Notification Channel
        createNotificationChannel()

        // Content Intent to launch MainActivity
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_MED_ID", medId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            medId.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark Taken
        val takenIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_MARK_TAKEN
            putExtra(EXTRA_MED_ID, medId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val takenPendingIntent = PendingIntent.getActivity(
            context,
            medId.hashCode() + 1000,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(reminder.title)
            .setContentText(reminder.body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${reminder.body}\n\n💡 Swasthya Salaah: Dawai lene ke baad halke gungune paani se gale ko tar karein.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "✓ Le Liya (Mark Taken)",
                takenPendingIntent
            )
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (medId.hashCode() and 0x7FFFFFFF)
        notificationManager.notify(notificationId, notification)

        // Record a scheduled event in DB so UI reflects active reminder
        try {
            val event = MedicationEvent(
                id = UUID.randomUUID().toString(),
                patientId = patientId,
                medicationId = medId,
                medicationName = medName,
                dosage = dosage,
                scheduledTime = scheduledTime,
                responseTimestamp = System.currentTimeMillis(),
                status = MedicationStatus.SCHEDULED,
                source = "WORKMANAGER",
                confidence = 1.0f,
                notes = "Scheduled reminder fired: ${reminder.title}"
            )
            patientDao.insertMedicationEvent(event)
        } catch (_: Exception) {
            // Non-critical if event cannot be logged immediately
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Medication reminders with Hindi & Hinglish voice/text templates for elder care."
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 400, 200, 400)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
