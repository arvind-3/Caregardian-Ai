package com.example.ui.screens.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun SimulatedAICallDialog(
    patientName: String,
    patientPhone: String,
    callStatus: String,
    transcript: List<Pair<String, String>>,
    onEndCall: () -> Unit
) {
    Dialog(onDismissRequest = onEndCall) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("simulated_ai_call_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Demo Mode Disclaimer
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "SIMULATED CALL — DEMO MODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Call Avatar & Pulse
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(CareBlueLight)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Calling",
                        tint = CareBluePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = patientName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareTextPrimary
                )

                Text(
                    text = patientPhone,
                    fontSize = 13.sp,
                    color = CareTextSecondary
                )

                Text(
                    text = callStatus,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (callStatus.contains("Connected")) CareGreenSuccess else CareAmberWarning,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                // Live Transcript Box
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (transcript.isEmpty()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Dialing patient... voice synthesis ready",
                                fontSize = 13.sp,
                                color = CareTextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(transcript) { (speaker, text) ->
                                Column {
                                    Text(
                                        text = speaker,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (speaker.contains("AI")) CareBluePrimary else CareGreenSuccess
                                    )
                                    Text(
                                        text = text,
                                        fontSize = 13.sp,
                                        color = CareTextPrimary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onEndCall,
                    colors = ButtonDefaults.buttonColors(containerColor = CareRedAlert),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_end_simulated_call")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("END SIMULATED CALL", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
