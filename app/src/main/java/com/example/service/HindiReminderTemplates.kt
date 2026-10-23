package com.example.service

enum class HindiTemplateStyle(val displayName: String, val description: String) {
    HINGLISH_RESPECTFUL(
        displayName = "Aadarniya Hinglish (Dadi/Dada ji)",
        description = "Polite, affectionate Hinglish for elderly parents/grandparents"
    ),
    PURE_HINDI(
        displayName = "Shuddh Hindi (दवाई स्मरण)",
        description = "Devanagari script with warm, traditional respect"
    ),
    TIME_OF_DAY(
        displayName = "Subah/Shaam Meal-based",
        description = "Contextual to meals (nashta, dopahar ka khana, raat ka khana)"
    ),
    URGENT_HEALTH(
        displayName = "Swasthya Raksha (Health Priority)",
        description = "Focuses on vital stability, BP, and sugar control"
    )
}

data class ReminderMessage(
    val title: String,
    val body: String,
    val speakableText: String
)

object HindiReminderTemplates {

    fun generate(
        patientName: String,
        medicationName: String,
        dosage: String,
        scheduledTime: String,
        instructions: String = "",
        style: HindiTemplateStyle = HindiTemplateStyle.HINGLISH_RESPECTFUL
    ): ReminderMessage {
        val honorific = if (patientName.contains("ji", ignoreCase = true)) patientName else "$patientName ji"
        val instructionSnippet = if (instructions.isNotBlank()) " ($instructions)" else ""

        return when (style) {
            HindiTemplateStyle.HINGLISH_RESPECTFUL -> {
                val title = "Dawai ka Samay! 💊 | $honorific"
                val body = "Namaste $honorific, aapki $medicationName ($dosage) lene ka waqt ($scheduledTime) ho gaya hai. Kripya samay par lein aur swasth rahein$instructionSnippet."
                val speakable = "Namaste $honorific, aapki $medicationName $dosage lene ka samay ho gaya hai. Kripya taaza paani ke saath dawai le lijiye."
                ReminderMessage(title, body, speakable)
            }

            HindiTemplateStyle.PURE_HINDI -> {
                val title = "दवाई का समय हो गया 💊"
                val body = "नमस्ते $honorific, आपकी $medicationName ($dosage) लेने का समय ($scheduledTime) हो गया है। कृपया समय पर दवाई लें।"
                val speakable = "नमस्ते $honorific, आपकी $medicationName लेने का समय हो गया है। कृपया समय पर दवाई ले लीजिए।"
                ReminderMessage(title, body, speakable)
            }

            HindiTemplateStyle.TIME_OF_DAY -> {
                val mealContext = when {
                    scheduledTime.startsWith("06") || scheduledTime.startsWith("07") ||
                    scheduledTime.startsWith("08") || scheduledTime.startsWith("09") ||
                    scheduledTime.contains("AM", ignoreCase = true) && !scheduledTime.startsWith("11") ->
                        "Subah ke nashte ke baad"

                    scheduledTime.startsWith("12") || scheduledTime.startsWith("13") ||
                    scheduledTime.startsWith("14") || scheduledTime.startsWith("01:") ||
                    scheduledTime.startsWith("02:") ->
                        "Dopahar ke bhojan ke baad"

                    scheduledTime.startsWith("17") || scheduledTime.startsWith("18") ||
                    scheduledTime.startsWith("19") || scheduledTime.startsWith("05:") ||
                    scheduledTime.startsWith("06:") || scheduledTime.startsWith("07:") ->
                        "Shaam ki chai ke baad"

                    else -> "Raat ko sone se pehle"
                }
                val title = "$mealContext: $medicationName"
                val body = "$honorific, $mealContext aapko $medicationName ($dosage) leni hai. Samay: $scheduledTime$instructionSnippet."
                val speakable = "$honorific, $mealContext aapki $medicationName lene ka waqt ho gaya hai."
                ReminderMessage(title, body, speakable)
            }

            HindiTemplateStyle.URGENT_HEALTH -> {
                val title = "Swasthya Alert: $medicationName 🩺"
                val body = "Dhyan dein $honorific: Regular BP & Sugar control ke liye $medicationName ($dosage) miss mat kijiye. Samay: $scheduledTime."
                val speakable = "Zaroori suchna $honorific, BP aur sugar ko niyantran mein rakhne ke liye apni $medicationName dawai zaroor lein."
                ReminderMessage(title, body, speakable)
            }
        }
    }
}
