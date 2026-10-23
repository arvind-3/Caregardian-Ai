package com.example.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Medication
import com.example.data.model.Patient
import com.example.ui.theme.*

@Composable
fun PatientScreen(
    patient: Patient?,
    nextMed: Medication?,
    adherencePercent: Int,
    onTakeMedicineClick: () -> Unit,
    onTalkClick: () -> Unit,
    onHealthCheckClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onNightRoutineClick: () -> Unit
) {
    val preferredName = patient?.preferredName ?: "Ravi ji"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("patient_dashboard_screen")
    ) {
        // Welcoming Elderly Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CareBlueLight)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = CareBluePrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = "Good Morning, $preferredName ❤️",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CareTextPrimary
                )
                Text(
                    text = "CareGuardian is watching over your health",
                    fontSize = 13.sp,
                    color = CareTextSecondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Next Medicine Hero Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CareBluePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_medicine_hero_card")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "NEXT MEDICINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Adherence: $adherencePercent%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = nextMed?.name ?: "Medicine A (Amlodipine)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Scheduled: ${nextMed?.scheduledTime ?: "08:00 AM"} • ${nextMed?.dosage ?: "1 tablet"}",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!nextMed?.instructions.isNullOrBlank()) {
                    Text(
                        text = "Instructions: ${nextMed?.instructions}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Big Take Medicine Action
                Button(
                    onClick = onTakeMedicineClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_take_medicine_hero")
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = CareBluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "💊 TAKE MEDICINE NOW",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CareBluePrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Big Action Grid (Elderly friendly 48dp+ touch targets, clear icons)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 1. Talk to CareGuardian
            ElderlyActionCard(
                title = "🎙 Talk to CareGuardian",
                subtitle = "Speak in Hindi, Hinglish, or English anytime",
                icon = Icons.Default.RecordVoiceOver,
                containerColor = CarePurpleLight,
                iconTint = CarePurple,
                tag = "btn_talk_companion",
                onClick = onTalkClick
            )

            // 2. How am I feeling?
            ElderlyActionCard(
                title = "❤️ How Am I Feeling?",
                subtitle = "Quick check-in for mood, sleep & energy",
                icon = Icons.Default.FavoriteBorder,
                containerColor = CareGreenLight,
                iconTint = CareGreenSuccess,
                tag = "btn_health_checkin",
                onClick = onHealthCheckClick
            )

            // 3. Night Routine
            ElderlyActionCard(
                title = "🌙 9:00 PM Night Routine",
                subtitle = "\"Good night Ravi ji. Ab aap aaram kijiye...\"",
                icon = Icons.Default.Bedtime,
                containerColor = Color(0xFFF3E8FF),
                iconTint = Color(0xFF9333EA),
                tag = "btn_night_routine",
                onClick = onNightRoutineClick
            )

            // 4. Emergency Help
            ElderlyActionCard(
                title = "🚨 Emergency Help",
                subtitle = "Immediate 1-tap call to family & ambulance",
                icon = Icons.Default.PhoneInTalk,
                containerColor = CareRedLight,
                iconTint = CareRedAlert,
                tag = "btn_emergency_help",
                onClick = onEmergencyClick
            )
        }

        Spacer(Modifier.height(20.dp))

        // Adherence and Caregiver reassurance footer
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = CareGreenSuccess,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Connected Caregiver: ${patient?.caregiverName ?: "Priya Sharma"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CareTextPrimary
                    )
                    Text(
                        text = "Real-time updates sent automatically on every medicine & check-in",
                        fontSize = 12.sp,
                        color = CareTextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ElderlyActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    iconTint: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(containerColor)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = CareTextSecondary,
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CareTextSecondary
            )
        }
    }
}
