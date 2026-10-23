# 🧓 CareGuardian AI

### AI-Powered Elderly Care, Medication Monitoring & Voice Companion

> **Because caring should never depend on being physically present.**

CareGuardian AI is a full-stack AI-powered elderly-care platform designed to help families remotely monitor elderly members, track medication adherence, conduct daily health check-ins, and provide a natural voice-based AI companion.

The system combines **AI, voice interaction, medication scheduling, health monitoring, notifications, caregiver dashboards, and automated escalation workflows** into one platform.

---

## 🚀 Project Overview

Many elderly people need to take multiple medicines at specific times every day. Family members may not always be available to remind them or check whether they actually took their medication.

CareGuardian AI addresses this problem by acting as a **voice-first digital care companion**.

At the scheduled medication time, the system can remind the elderly person:

> **"Namaste Ravi ji, aapki dawai lene ka time ho gaya hai. Kya aapne dawai le li?"**

The patient can respond naturally:

> **"Haan beta, maine le li."**

The AI processes the response and records the medication status.

The caregiver can then see the update on their dashboard.

---

# 🎯 Key Features

## 👴 1. Elderly-Friendly Patient App

The patient application provides a simple interface with large buttons and minimal navigation.

Patients can:

- Create an account
- Manage their profile
- Add caregiver information
- View medication schedules
- Receive medication reminders
- Respond using voice or text
- Talk with the AI assistant
- Complete daily health check-ins
- View medication history
- View health history
- Request caregiver assistance
- Access emergency assistance

---

# 💊 2. Smart Medication Monitoring

Authorized caregivers can configure medication schedules.

Each medication can contain:

```text
Medicine Name
Dosage
Frequency
Time
Start Date
End Date
Instructions
Doctor Name
Notes
```

Example:

```text
Medicine: Medicine A
Dosage: 1 tablet
Frequency: Daily
Time: 08:00 AM
```

The system automatically monitors scheduled medication events.

---

# ⏰ 3. Automatic Medication Reminder

When a medication becomes due, the patient receives a reminder.

Example:

```text
💊 Medicine Time

Namaste Ravi ji.

Aapki Medicine A lene ka time ho gaya hai.

Kya aapne dawai le li?

[ YES ]
[ NO ]
[ LATER ]
[ TALK TO AI ]
```

Where supported by the mobile platform, the application uses notifications to bring the patient's attention to the reminder.

The application does **not bypass operating-system restrictions to forcibly open an app**.

---

# 🎙️ 4. AI Voice Assistant

The main interaction method is voice.

The patient can speak naturally in:

- Hindi
- Hinglish
- English

Example:

```text
Patient:
"Haan beta, maine tablet le li."

AI:
"Bahut achha Ravi ji. Maine record kar liya hai."
```

Voice pipeline:

```text
Patient Voice
      ↓
Speech-to-Text
      ↓
AI / LLM
      ↓
Intent Detection
      ↓
Backend Validation
      ↓
Database
      ↓
Text-to-Speech
      ↓
AI Voice Response
```

A text-based fallback is available if voice services are unavailable.

---

# 🤖 5. Natural AI Companion

CareGuardian AI is not designed to be only a medication reminder.

The patient can start a conversation whenever they want.

Example:

```text
Patient:
"Beta, mujhe tumse thodi baat karni hai."

CareGuardian:
"Ji Ravi ji, bilkul.
Aap bataiye, aaj kya baat karni hai?"
```

The assistant can help with:

- General conversations
- Medication reminders
- Daily check-ins
- Health-related information already stored in the system
- Recording concerns
- Caregiver communication
- General assistance

The assistant should clearly identify itself as an AI and should not impersonate a human.

---

# 🩺 6. Daily Health Check

The AI can conduct a daily wellness check.

Example questions:

```text
Aaj aap kaise feel kar rahe hain?

Aapki neend kaisi rahi?

Energy level kaisa hai?

Kya koi unusual symptom hai?

Kya aapne apni medicines li hain?

Kya aapko kisi help ki zarurat hai?
```

The responses are stored as structured health data.

Example:

```json
{
  "mood": "GOOD",
  "sleep": "NORMAL",
  "energy": "MEDIUM",
  "symptoms": [],
  "notes": ""
}
```

---

# 🧠 7. AI Intent Detection

The AI converts natural language into structured information.

Example:

```text
"Haan beta maine le li."

        ↓

MEDICATION_TAKEN
```

Possible intents:

```text
MEDICATION_TAKEN
MEDICATION_MISSED
MEDICATION_DELAYED
UNCLEAR
REQUEST_HELP
SYMPTOM_REPORTED
HEALTH_CHECK_RESPONSE
EMERGENCY_CONCERN
```

Example AI output:

```json
{
  "intent": "MEDICATION_TAKEN",
  "status": "TAKEN",
  "confidence": 0.96,
  "requires_clarification": false
}
```

The backend validates AI output before modifying important records.

---

# 📊 8. Medication Adherence Tracking

Medication events are tracked using:

```text
SCHEDULED
TAKEN
MISSED
DELAYED
UNCLEAR
NO_RESPONSE
```

The system calculates:

- Daily adherence
- Weekly adherence
- Monthly adherence
- Missed medications
- Delayed medications
- No-response events

---

# 👨‍👩‍👦 9. Caregiver Dashboard

Caregivers have a dedicated web dashboard.

The dashboard provides:

### Patient Overview

```text
Patient Name
Age
Current Status
Last Check-in
Medication Adherence
```

### Medication Monitoring

```text
Today's adherence
Weekly adherence
Missed medicines
Delayed medicines
No-response events
```

### Health Monitoring

```text
Mood
Sleep
Energy
Symptoms
Check-in frequency
```

### Alerts

```text
Medication Missed
No Response
Reported Symptoms
Possible Emergency
```

---

# 🔔 10. Caregiver Notifications

When important events occur, caregivers receive notifications.

### Medication Taken

```text
✅ Medication Update

Ravi ji has taken Medicine A.

08:04 AM
```

### Medication Missed

```text
⚠️ Medication Alert

Ravi ji has not confirmed Medicine A.

Please check on the patient.
```

### No Response

```text
🚨 Patient Check Required

Ravi ji did not respond to the medication reminder.

Please contact the patient.
```

### Health Alert

```text
⚠️ Health Check Alert

Ravi ji reported dizziness.

Please check on the patient and
contact a healthcare professional
if appropriate.
```

---

# 📞 11. AI Voice Calling

A future/production version can support automated AI phone calls.

Example:

```text
Medication Reminder
       ↓
No Response
       ↓
Escalation Rule
       ↓
AI Voice Call
       ↓
Patient Answers
       ↓
Speech-to-Text
       ↓
AI
       ↓
Text-to-Speech
       ↓
Conversation
       ↓
Result Stored
       ↓
Caregiver Notification
```

The current prototype provides a **Simulated AI Call** mode.

It is clearly marked:

```text
SIMULATED CALL — DEMO MODE
```

A real telephony provider can later be connected through the `VoiceCallService` abstraction.

---

# 🎧 12. Voice Recording

With appropriate user consent, conversations can be recorded.

The system can store:

```text
Audio
Transcript
Conversation Summary
Duration
Timestamp
Consent Status
```

Example workflow:

```text
Start Conversation
       ↓
User Consent
       ↓
Record Audio
       ↓
Speech-to-Text
       ↓
AI Analysis
       ↓
Save Transcript
       ↓
Save Audio
       ↓
Generate Summary
```

Audio files are protected from unauthorized access.

---

# 🌙 13. Night Routine

CareGuardian AI supports a configurable night routine.

Default time:

**9:00 PM**

Example:

> **"Ravi ji, aaj ki routine complete ho gayi. Ab aap aaram kijiye. Good night Ravi ji. Achhi neend aaye. Kal subah phir milte hain."**

The caregiver can enable or disable this feature.

---

# 🚨 14. Emergency Detection

The system can identify statements indicating a possible emergency.

Example:

```text
"Mujhe saans lene mein bahut dikkat ho rahi hai."

"Mujhe bahut tez chest pain hai."
```

The system should not diagnose the patient.

Instead:

```text
Possible Emergency
       ↓
Emergency Guidance
       ↓
Configured Caregiver Workflow
       ↓
Professional/Emergency Assistance
```

The application only reports an emergency call as completed if an actual call was successfully initiated.

---

# 💡 15. Medication Information Assistant

The AI can explain information already present in the patient's authorized medication schedule.

Example:

```text
Patient:
"Medicine A kab leni hai?"

AI:
"Ravi ji, aapke saved medication schedule ke according
Medicine A subah 8 baje leni hai."
```

If information is unavailable:

```text
"Mujhe is medicine ki timing confirm nahi hai.
Please apne caregiver ya doctor se confirm kijiye."
```

---

# ⚠️ 16. Medical Safety

CareGuardian AI is an **assistive monitoring and communication system**, not a doctor.

The AI must never:

- Diagnose diseases
- Independently prescribe medicines
- Change medication dosage
- Change prescriptions
- Tell a patient to double a missed dose
- Tell a patient to stop prescribed medication
- Pretend to be a doctor
- Replace professional medical advice

Medication information must come from an authorized caregiver, clinician, or prescription.

If concerning symptoms are reported, the system should escalate the information to the appropriate caregiver or professional.

---

# 🏗️ System Architecture

```text
                 👴 PATIENT
                     │
              React Native App
                     │
          ┌──────────┴──────────┐
          │                     │
       Voice                 Text
          │                     │
          ▼                     │
   Speech-to-Text               │
          │                     │
          └──────────┬──────────┘
                     ▼
                  AI / LLM
                     │
              Intent Detection
                     │
                     ▼
              FastAPI Backend
                     │
       ┌─────────────┼─────────────┐
       │             │             │
       ▼             ▼             ▼
 PostgreSQL      Scheduler     Notifications
       │                           │
       │                           ▼
       │                    👨‍👩‍👧 CAREGIVER
       │                    React Dashboard
       │
       ▼
 Reports / Analytics
```

---

# ☁️ Future AWS Architecture

The system is designed to be AWS-ready.

```text
React / React Native
        │
        ▼
AWS Hosting
        │
        ▼
FastAPI Backend
        │
   ┌────┼───────────┐
   ▼    ▼           ▼
 RDS   S3        Lambda
   │    │           │
   │    │      Scheduled Jobs
   │    │
   ▼    ▼
PostgreSQL       Reports/Audio

        │
        ▼
CloudWatch
Monitoring & Logs
```

Potential AWS services:

- Amazon RDS
- Amazon S3
- AWS Lambda
- Amazon CloudWatch
- Amazon ECS/EC2
- Amazon Cognito
- Amazon SNS

AWS is **not required for local development**.

---

# 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Patient App | React Native + Expo |
| Caregiver Dashboard | React + Vite |
| Backend | Python + FastAPI |
| Database | PostgreSQL |
| ORM | SQLAlchemy |
| Validation | Pydantic |
| Authentication | JWT |
| AI | LLM API |
| Speech | Speech-to-Text |
| Voice | Text-to-Speech |
| Charts | Recharts |
| Scheduler | APScheduler |
| Storage | Local / AWS S3 |
| Cloud | AWS |
| Version Control | Git + GitHub |

---

# 📁 Project Structure

```text
careguardian-ai/
│
├── backend/
│   ├── app/
│   │   ├── main.py
│   │   ├── config.py
│   │   ├── database.py
│   │   │
│   │   ├── models/
│   │   ├── schemas/
│   │   ├── routers/
│   │   ├── services/
│   │   ├── ai/
│   │   ├── scheduler/
│   │   ├── notifications/
│   │   └── voice/
│   │
│   ├── tests/
│   ├── requirements.txt
│   └── .env.example
│
├── patient-app/
│   └── src/
│
├── caregiver-web/
│   └── src/
│
├── storage/
│   ├── audio/
│   └── reports/
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

# 🔄 Core Workflow

```text
Patient Registration
        ↓
Patient Profile
        ↓
Medication Added
        ↓
Medication Schedule
        ↓
Scheduled Time
        ↓
AI Reminder
        ↓
Patient Responds
        ↓
AI Understands Response
        ↓
Backend Validation
        ↓
Medication Event
        ↓
Caregiver Notification
        ↓
Dashboard Updated
        ↓
Daily Report
```

---

# 🧪 Demo Workflow

The project includes a demo environment for presentation.

### Step 1

Login as caregiver.

### Step 2

View:

```text
Ravi Sharma
Age: 72
```

### Step 3

Set a medication reminder for a short demo interval.

### Step 4

Switch to patient application.

### Step 5

Reminder appears.

### Step 6

AI asks:

> **"Namaste Ravi ji, aapki dawai lene ka time ho gaya hai. Kya aapne dawai le li?"**

### Step 7

Respond:

> **"Haan beta, maine le li."**

### Step 8

System records:

```text
TAKEN
```

### Step 9

Caregiver dashboard updates.

### Step 10

Simulate:

```text
MISSED
NO_RESPONSE
```

and demonstrate caregiver alerts.

### Step 11

Start AI health conversation.

### Step 12

Report a symptom.

### Step 13

Show caregiver alert.

### Step 14

Generate daily report.

### Step 15

Demonstrate simulated AI voice call.

### Step 16

Demonstrate 9 PM good-night routine.

---

# 🧑‍💻 Local Development

## Requirements

Install:

```text
Node.js
Python 3.11+
PostgreSQL
Git
Docker Desktop
Expo CLI
```

---

## Clone Repository

```bash
git clone <your-repository-url>
cd careguardian-ai
```

---

# 🐘 Start PostgreSQL

Using Docker:

```bash
docker-compose up -d
```

Check containers:

```bash
docker ps
```

---

# ⚙️ Backend Setup

```bash
cd backend
python -m venv venv
```

Windows:

```bash
venv\Scripts\activate
```

Install dependencies:

```bash
pip install -r requirements.txt
```

Create environment file:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Start FastAPI:

```bash
uvicorn app.main:app --reload
```

API:

```text
http://localhost:8000
```

Swagger documentation:

```text
http://localhost:8000/docs
```

---

# 🌐 Caregiver Dashboard

```bash
cd caregiver-web
npm install
npm run dev
```

Open the development URL shown by Vite.

---

# 📱 Patient Application

```bash
cd patient-app
npm install
npx expo start
```

Run using:

```text
Android Emulator
iOS Simulator
Expo Go
```

depending on the development environment.

---

# 🔐 Environment Variables

Create `.env` from `.env.example`.

Example:

```env
DATABASE_URL=postgresql://postgres:password@localhost:5432/careguardian

JWT_SECRET=change_this_secret

AI_API_KEY=

STT_API_KEY=

TTS_API_KEY=

STORAGE_MODE=local

DEMO_MODE=true
```

Never commit `.env` to GitHub.

Add:

```text
.env
```

to `.gitignore`.

---

# 👤 Demo Accounts

### Patient

```text
Email:
patient@careguardian.demo

Password:
Demo@123
```

### Caregiver

```text
Email:
caregiver@careguardian.demo

Password:
Demo@123
```

### Admin

```text
Email:
admin@careguardian.demo

Password:
Demo@123
```

These accounts are intended only for development/demo purposes.

---

# 🧪 Testing

Backend tests:

```bash
cd backend
pytest
```

Test important workflows:

```text
Authentication
Authorization
Medication creation
Medication scheduling
Medication events
Medication status
No-response workflow
Caregiver notifications
Health check-ins
AI response validation
Report generation
```

---

# 📈 Future Scope

CareGuardian AI can eventually be expanded with:

### Wearable Integration

Connect:

- Smart watches
- Heart-rate sensors
- Sleep tracking
- Activity
