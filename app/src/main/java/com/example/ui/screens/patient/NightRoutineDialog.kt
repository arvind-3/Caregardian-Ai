package com.example.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun NightRoutineDialog(
    preferredName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("night_routine_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = CarePurpleLight,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Bedtime,
                        contentDescription = "Night",
                        tint = CarePurple,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                    )
                }

                Text(
                    text = "🌙 9:00 PM Night Routine",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CarePurple,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "\"$preferredName, aaj ki routine complete ho gayi.\n\nAb aap aaram kijiye.\n\nGood night $preferredName. Achhi neend aaye.\n\nKal subah phir milte hain.\"",
                            fontSize = 16.sp,
                            color = CareTextPrimary,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CarePurple),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_goodnight_done")
                ) {
                    Text("SHUBH RATRI / GOOD NIGHT", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
