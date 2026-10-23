package com.example.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AIMessage
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCompanionDialog(
    preferredName: String,
    messages: List<AIMessage>,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Talk to CareGuardian",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hindi • Hinglish • English",
                                fontSize = 12.sp,
                                color = CareTextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            containerColor = CareBackground,
            modifier = Modifier
                .fillMaxSize()
                .testTag("voice_companion_dialog")
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Chat history
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubble(msg = msg, preferredName = preferredName)
                    }
                }

                // Quick Prompt Chips
                Text(
                    text = "Quick Speaking Prompts:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CareTextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        modifier = Modifier.weight(1f),
                        onClick = { onSendMessage("Aaj mujhe thoda chakkar aa raha hai") },
                        label = { Text("Chakkar aa raha hai", fontSize = 12.sp) }
                    )
                    SuggestionChip(
                        modifier = Modifier.weight(1f),
                        onClick = { onSendMessage("Ye tablet kab leni hai?") },
                        label = { Text("Tablet timing?", fontSize = 12.sp) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        modifier = Modifier.weight(1f),
                        onClick = { onSendMessage("Headache ke liye kaunsi medicine loon?") },
                        label = { Text("Medicine suggestion?", fontSize = 12.sp) }
                    )
                    SuggestionChip(
                        modifier = Modifier.weight(1f),
                        onClick = { onSendMessage("Beta, mujhe tumse thodi baat karni hai") },
                        label = { Text("Baat karni hai", fontSize = 12.sp) }
                    )
                }

                // Microphone & Input bar
                Surface(
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big Mic Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isListening) CareAmberWarning else CareBluePrimary)
                                .clickable {
                                    isListening = !isListening
                                    if (isListening) {
                                        // Simulate quick voice query
                                        onSendMessage("Beta mujhe tumse thodi baat karni hai.")
                                        isListening = false
                                    }
                                }
                                .testTag("btn_mic_speak")
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = if (isListening) "Listening..." else "Tap Mic to Speak in Hindi / English",
                            fontSize = 13.sp,
                            color = CareTextSecondary,
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        // Text fallback row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                placeholder = { Text("Or type your message here...", fontSize = 14.sp) },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_companion_text"),
                                maxLines = 2
                            )
                            Spacer(Modifier.width(8.dp))
                            FilledIconButton(
                                onClick = {
                                    if (textInput.isNotBlank()) {
                                        onSendMessage(textInput)
                                        textInput = ""
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CareBluePrimary),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: AIMessage, preferredName: String) {
    val isUser = msg.sender == "USER"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isUser) preferredName else "CareGuardian AI",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = CareTextSecondary,
            modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
        )
        Surface(
            color = if (isUser) CareBluePrimary else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = msg.text,
                    color = if (isUser) Color.White else CareTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                if (msg.detectedSymptom != null) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        color = CareAmberLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Observation: ${msg.detectedSymptom} • Caregiver alerted",
                            color = CareAmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
