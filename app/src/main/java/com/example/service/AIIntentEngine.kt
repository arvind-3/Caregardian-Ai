package com.example.service

import com.example.data.model.Medication
import com.example.data.model.MedicationStatus

data class MedicationIntentResult(
    val intent: String, // MEDICATION_TAKEN, MEDICATION_MISSED, MEDICATION_DELAYED, UNCLEAR, REQUEST_HELP
    val status: MedicationStatus,
    val confidence: Float,
    val requiresClarification: Boolean,
    val explanation: String
)

data class SymptomDetectionResult(
    val detected: Boolean,
    val symptomName: String,
    val severity: String,
    val requiresAttention: Boolean,
    val isEmergency: Boolean
)

data class CompanionResponseResult(
    val spokenResponse: String,
    val detectedSymptom: String? = null,
    val isEmergency: Boolean = false,
    val requiresCaregiverAlert: Boolean = false,
    val alertMessage: String? = null
)

object AIIntentEngine {

    fun analyzeMedicationResponse(utterance: String, preferredName: String): MedicationIntentResult {
        val lower = utterance.trim().lowercase()

        // 1. Taken signals
        val takenKeywords = listOf(
            "haan beta", "maine le li", "le li", "kha li", "kha liya", "yes", "taken",
            "ha", "haan", "ho gayi", "already taken", "done", "pi li", "swallowed"
        )
        if (takenKeywords.any { lower.contains(it) } && !lower.contains("nahi") && !lower.contains("baad")) {
            return MedicationIntentResult(
                intent = "MEDICATION_TAKEN",
                status = MedicationStatus.TAKEN,
                confidence = 0.96f,
                requiresClarification = false,
                explanation = "Aapne confirm kiya ki aapne dawai le li hai. Very good, $preferredName."
            )
        }

        // 2. Delayed signals
        val delayedKeywords = listOf(
            "thodi der baad", "baad mein", "baad me", "later", "after some time",
            "khana khane ke baad", "ruk jao", "thodi der", "in 10 minutes", "15 minute"
        )
        if (delayedKeywords.any { lower.contains(it) }) {
            return MedicationIntentResult(
                intent = "MEDICATION_DELAYED",
                status = MedicationStatus.DELAYED,
                confidence = 0.92f,
                requiresClarification = false,
                explanation = "Theek hai $preferredName, main thodi der baad aapko phir se remind karunga."
            )
        }

        // 3. Missed / No signals
        val missedKeywords = listOf(
            "nahi li", "nahi li hai", "nahi lunga", "no", "nahi", "forget", "missed",
            "skip", "nahi khana", "mann nahi hai"
        )
        if (missedKeywords.any { lower.contains(it) }) {
            return MedicationIntentResult(
                intent = "MEDICATION_MISSED",
                status = MedicationStatus.MISSED,
                confidence = 0.94f,
                requiresClarification = false,
                explanation = "$preferredName, maine note kar liya hai ki aapne dawai nahi li. Main aapke caregiver ko inform kar deta hoon."
            )
        }

        // 4. Request help
        if (lower.contains("help") || lower.contains("madad") || lower.contains("doctor") || lower.contains("dikkat")) {
            return MedicationIntentResult(
                intent = "REQUEST_HELP",
                status = MedicationStatus.UNCLEAR,
                confidence = 0.85f,
                requiresClarification = true,
                explanation = "$preferredName, kya aapko koi pareshani ya takleef ho rahi hai? Main caregiver ko call kar sakta hoon."
            )
        }

        // 5. Unclear
        return MedicationIntentResult(
            intent = "UNCLEAR",
            status = MedicationStatus.UNCLEAR,
            confidence = 0.45f,
            requiresClarification = true,
            explanation = "$preferredName, mujhe thoda clearly nahi samajh aaya. Kya aapne dawai le li hai?"
        )
    }

    fun analyzeCompanionMessage(
        message: String,
        preferredName: String,
        medications: List<Medication>
    ): CompanionResponseResult {
        val lower = message.trim().lowercase()

        // 1. Emergency Detection
        val emergencyKeywords = listOf(
            "saans lene mein dikkat", "saans nahi aa rahi", "tez chest pain",
            "chhati me dard", "chest pain", "heart pain", "chhati mein dard",
            "heart attack", "unconscious", "gir gaya", "behosh", "severe pain"
        )
        if (emergencyKeywords.any { lower.contains(it) }) {
            return CompanionResponseResult(
                spokenResponse = "$preferredName, kripya aaram se baithiye. Main emergency service aur aapke caregiver ko connect kar raha hoon. Help is on the way.",
                detectedSymptom = "Severe Emergency / Chest pain / Breathing difficulty",
                isEmergency = true,
                requiresCaregiverAlert = true,
                alertMessage = "EMERGENCY: $preferredName reported severe distress: '$message'"
            )
        }

        // 2. Prescription Explanation (Authorized info only)
        if (lower.contains("kab leni") || lower.contains("kab khana") || lower.contains("timing") || lower.contains("schedule")) {
            val matchedMed = medications.firstOrNull { med ->
                lower.contains(med.name.lowercase().split(" ")[0]) ||
                lower.contains("medicine") || lower.contains("dawai") || lower.contains("tablet")
            } ?: medications.firstOrNull()

            if (matchedMed != null) {
                return CompanionResponseResult(
                    spokenResponse = "$preferredName, aapke saved medication schedule ke according ${matchedMed.name} subah ${matchedMed.scheduledTime} par ${matchedMed.instructions} leni hai.",
                    detectedSymptom = null
                )
            } else {
                return CompanionResponseResult(
                    spokenResponse = "Mujhe is medicine ki timing confirm nahi hai. Please apne caregiver ya doctor se confirm kijiye.",
                    detectedSymptom = null
                )
            }
        }

        // 3. Safety Check: autonomous prescribing refusal
        val prescribingKeywords = listOf(
            "kaunsi medicine loon", "kaun si dawai", "medicine suggest",
            "kya khau", "headache ke liye", "pain killer", "prescribe", "suggest medicine"
        )
        if (prescribingKeywords.any { lower.contains(it) }) {
            return CompanionResponseResult(
                spokenResponse = "$preferredName, main aapke liye medicine prescribe nahi kar sakta. Main aapki problem record karke caregiver ya doctor ko inform kar sakta hoon.",
                detectedSymptom = "Medication advice sought",
                requiresCaregiverAlert = true,
                alertMessage = "$preferredName asked for medicine suggestion: '$message'. Please consult the doctor."
            )
        }

        // 4. Symptom Reporting (Dizziness, fever, weakness, nausea)
        val symptomsMap = mapOf(
            "chakkar" to "dizziness",
            "dizziness" to "dizziness",
            "sir dard" to "headache",
            "headache" to "headache",
            "kamzori" to "weakness",
            "weakness" to "weakness",
            "bukhar" to "fever",
            "fever" to "fever",
            "vomit" to "nausea",
            "pet dard" to "stomach ache",
            "stiffness" to "joint stiffness"
        )

        for ((key, symptom) in symptomsMap) {
            if (lower.contains(key)) {
                return CompanionResponseResult(
                    spokenResponse = "Thank you $preferredName. Maine aapki baat record kar li hai. Main aapke caregiver ko inform karunga taaki woh aapka dhyan rakh sakein.",
                    detectedSymptom = symptom,
                    requiresCaregiverAlert = true,
                    alertMessage = "$preferredName reported symptom: '$symptom' ($message). Please check on the patient."
                )
            }
        }

        // 5. Friendly conversation / Greetings / Routine
        if (lower.contains("baat karni hai") || lower.contains("kaise ho") || lower.contains("namaste") || lower.contains("hello") || lower.contains("kya haal")) {
            return CompanionResponseResult(
                spokenResponse = "Ji $preferredName, bilkul! Main yahin hoon. Aap bataiye, aaj aap kaisa mehsoos kar rahe hain? Sab theek hai na?",
                detectedSymptom = null
            )
        }

        if (lower.contains("night") || lower.contains("so raha") || lower.contains("aaram") || lower.contains("neend")) {
            return CompanionResponseResult(
                spokenResponse = "Good night $preferredName! Aaj ki routine complete ho gayi. Ab aap aaram kijiye. Achhi neend aaye. Kal subah phir milte hain.",
                detectedSymptom = null
            )
        }

        // Generic comforting companion response
        return CompanionResponseResult(
            spokenResponse = "Samajh gaya $preferredName. Maine aapki baat sun li hai aur note kar li hai. Main hamesha aapke sath hoon.",
            detectedSymptom = null
        )
    }
}
