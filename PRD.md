
# 📄 **Product Requirements Document (PRD)**

## 🛡️ Product Name: **Alertify – Smart Emergency Response System**

---

# 1. 📌 Overview

**Alertify** is a mobile safety application that enables users to **trigger emergency alerts using multiple hidden and smart activation methods**, ensuring **instant help even in critical situations where manual interaction is not possible**.

The system combines **hardware triggers + real-time communication + proximity-based alerts** to maximize safety.

---

# 2. 🎯 Objectives

* Enable **instant SOS activation without unlocking phone**
* Provide **multi-layer emergency communication**
* Ensure **real-time location tracking**
* Create a **community-based nearby alert system**

---

# 3. 👥 Target Users

* Women (primary focus)
* College students
* Night commuters
* Elderly users
* Children

---

# 4. ⚙️ Core Functional Modules

---

## 🔴 4.1 Trigger Engine (Multi-Trigger Detection System)

### 📳 1. Shake Detection

* Detect rapid shake using accelerometer
* Configurable sensitivity
* Threshold-based activation

---

### 🔘 2. Volume Button Trigger

* Detect multiple presses of volume down button

**Condition:**

* 3–5 presses within 2 seconds

---

### 🎙️ 3. Voice Trigger

* Detect phrase: **“Help me!”**
* Background listening (optimized mode)

---

### 📞 4. Secret Dial Code

* User enters secret code in dial pad (e.g., `*123#`)
* Triggers alert silently

---

# 5. 🚨 Emergency Response System

When triggered, the system executes **parallel emergency actions**:

---

## 📞 5.1 Automated Calling

* Call primary contact
* Retry if not answered
* Optional multi-contact calling sequence

---

## 📍 5.2 Live Location Sharing

* Fetch GPS location
* Generate Google Maps link
* Update location every 5–10 seconds

---

## 📩 5.3 Automated SMS Alert

**Message:**

```
I am in danger. Please help!
Live Location: [link]
Time: [timestamp]
```

---

## 📡 5.4 Nearby Alert System (USP 🔥)

* Notify nearby users with Alertify installed
* Technologies:

  * Internet-based (Firebase)
  * Optional BLE (advanced)

**Output:**

* Nearby users receive alert notification + location

---

# 6. 🧑‍🤝‍🧑 Emergency Contacts Module

* Add/edit/delete contacts
* Priority-based system:

  * Primary (call + SMS)
  * Secondary (SMS only)

---

# 7. 🔒 Stealth & Safety

* Background service always active
* No visible UI when triggered
* Fake screen (calculator mode)
* Silent activation

---

# 8. 🛠️ Technical Stack

### 📱 Frontend

* Android (java + XML)

### ⚙️ Backend

* Firebase:

  * Realtime Database
  * Cloud Messaging (FCM)

### 🔌 APIs & Services

* Google Maps API
* Fused Location Provider
* SMS Manager
* Speech Recognition API

---

# 9. 📊 System Flow

```
Trigger Detected (Shake / Button / Voice / Code)
            ↓
Validation Engine
            ↓
Fetch Location
            ↓
Parallel Execution:
   → Call Contact
   → Send SMS
   → Share Live Location
   → Notify Nearby Users
```

---

# 10. 🔐 Permissions Required

* Location
* SMS
* Call
* Microphone
* Background activity
* Internet
* Bluetooth (optional)

---

# 11. ⚠️ Challenges

* False triggers
* Battery optimization
* Background restrictions (Android)
* Privacy concerns

---

# 12. 🚀 Future Scope

* AI-based danger detection
* Smartwatch integration
* Police API integration
* Auto video recording

---

# 13. 🧠 USP (Important for Judges)

> “Alertify enables emergency activation through multiple hidden triggers and extends help beyond contacts using a nearby user alert system.”

---

# 👨‍💻 **Team Work Division (4 Members)**

Now this is VERY IMPORTANT for execution 👇

---

## 👤 **Member 1: Android Core Developer (Triggers + Sensors)**

### Responsibilities:

* Shake detection (accelerometer)
* Volume button detection
* Background service implementation
* Secret dial code detection

### Skills Needed:

* Android lifecycle
* Sensors API
* Broadcast receivers

---

## 👤 **Member 2: Communication & Emergency Module**

### Responsibilities:

* SMS sending system
* Auto calling feature
* Contact management system
* Permissions handling

### Skills Needed:

* Telephony APIs
* SMS Manager
* Runtime permissions

---

## 👤 **Member 3: Location & Backend Developer**

### Responsibilities:

* GPS location tracking
* Google Maps integration
* Firebase backend setup
* Nearby alert system (core logic)

### Skills Needed:

* Firebase
* APIs
* Networking

---

## 👤 **Member 4: UI/UX + Voice System Developer**

### Responsibilities:

* App UI design (modern + clean)
* Voice recognition system
* Fake calculator screen (stealth UI)
* User onboarding flow

### Skills Needed:

* XML/UI design
* Speech recognition
* UX thinking

---

# 🗂️ Suggested Folder Structure

```
Alertify/
│
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   │
│   ├── java/com/alertify/
│   │
│   │   ├── ui/
│   │   │   ├── activities/
│   │   │   ├── fragments/
│   │   │   ├── adapters/
│   │   │   └── viewmodels/
│   │   │
│   │   ├── triggers/
│   │   │   ├── ShakeDetector.kt
│   │   │   ├── VolumeButtonReceiver.kt
│   │   │   ├── VoiceTriggerService.kt
│   │   │   └── DialCodeReceiver.kt
│   │   │
│   │   ├── emergency/
│   │   │   ├── EmergencyManager.kt
│   │   │   ├── SmsSender.kt
│   │   │   ├── CallHandler.kt
│   │   │   └── AlertDispatcher.kt
│   │   │
│   │   ├── location/
│   │   │   ├── LocationService.kt
│   │   │   ├── LocationHelper.kt
│   │   │   └── MapsUtils.kt
│   │   │
│   │   ├── contacts/
│   │   │   ├── ContactModel.kt
│   │   │   ├── ContactRepository.kt
│   │   │   └── ContactManager.kt
│   │   │
│   │   ├── nearby/
│   │   │   ├── NearbyAlertService.kt
│   │   │   ├── FirebaseHelper.kt
│   │   │   └── AlertListener.kt
│   │   │
│   │   ├── services/
│   │   │   ├── BackgroundService.kt
│   │   │   └── ForegroundService.kt
│   │   │
│   │   ├── stealth/
│   │   │   ├── FakeCalculatorActivity.kt
│   │   │   └── StealthManager.kt
│   │   │
│   │   ├── utils/
│   │   │   ├── Constants.kt
│   │   │   ├── PermissionUtils.kt
│   │   │   └── Logger.kt
│   │   │
│   │   └── MainActivity.kt
│   │
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│   │   └── mipmap/
│   │
│   └── Gradle Scripts
```

---
