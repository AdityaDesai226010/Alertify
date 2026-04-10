
# 📄 **Product Requirements Document (PRD)**

## 🛡️ Product Name: **Alertify – Smart Emergency Response System**

---

# 1. 📌 Overview

**Alertify** is a mobile safety application designed to provide **instant emergency assistance through multiple hidden trigger mechanisms**, ensuring users can send alerts even when they are unable to manually operate their phone.

The system integrates **sensor-based triggers, voice recognition, and communication modules** to automatically notify emergency contacts and nearby users with real-time location data.

---

# 2. 🎯 Objectives

* Enable **quick SOS activation without unlocking the phone**
* Provide **multiple hidden trigger options**
* Ensure **real-time location tracking**
* Enable **automatic emergency communication**
* Build a **community-based nearby alert system**

---

# 3. 👥 Target Users

* Women (Primary focus)
* College students
* Night commuters
* Elderly individuals
* Children

---

# 4. ⚙️ Core Functional Modules

---

## 🔴 4.1 Trigger Engine (Multi-Trigger Detection System)

The Trigger Engine continuously runs in the background and detects emergency activation signals.

---

### 📳 1. Shake Detection

* Uses **accelerometer sensor**
* Detects rapid shaking motion
* Configurable sensitivity level
* Threshold-based activation logic

---

### 🔘 2. Volume Button Trigger

* Detects multiple presses of volume down button

**Condition:**

* 3–5 presses within 2 seconds triggers emergency

---

### 🎙️ 3. Voice Trigger

* Detects keyword: **“Help me!”**
* Uses Android Speech Recognition API
* Runs in optimized background mode

---

### 📞 4. Secret Dial Code

* User enters predefined code (e.g., `*123#`)
* Triggers emergency silently

---

# 5. 🚨 Emergency Response System

Once any trigger is activated, the system executes multiple emergency actions **in parallel**.

---

## 📞 5.1 Automated Calling

* Calls primary emergency contact
* Retries if call is not answered
* Option to call multiple contacts sequentially

---

## 📍 5.2 Live Location Sharing

* Fetches GPS location using **Fused Location Provider**
* Generates Google Maps link
* Updates location every 5–10 seconds

---

## 📩 5.3 Automated SMS Alert

**Message Format:**

```
I am in danger. Please help!
Live Location: [Google Maps link]
Time: [timestamp]
```

* Sent to all emergency contacts

---

## 📡 5.4 Nearby Alert System (USP 🔥)

* Sends alerts to nearby Alertify users

### Technologies Used:

* Firebase Realtime Database
* Firebase Cloud Messaging (FCM)
* Optional Bluetooth Low Energy (BLE)

### Output:

* Nearby users receive notification with:

  * User’s location
  * Alert message

---

# 6. 🧑‍🤝‍🧑 Emergency Contacts Module

* Add / edit / delete contacts
* Priority-based contact system:

  * **Primary Contacts:** Call + SMS
  * **Secondary Contacts:** SMS only

---

# 7. 🔒 Stealth & Safety Features

* Background service always active
* No visible UI during emergency trigger
* Fake calculator screen for disguise
* Silent activation mode

---

# 8. 🛠️ Technical Stack

---

## 📱 Frontend

* Android Development using:

  * **Java**
  * XML for UI design

---

## ⚙️ Backend

* Firebase Services:

  * Realtime Database
  * Firebase Cloud Messaging (FCM)

---

## 🔌 APIs & Services

* Google Maps API
* Fused Location Provider API
* SMS Manager
* Telephony API (calling)
* Speech Recognition API
* Sensors API (accelerometer)

---

# 9. 📊 System Flow

```
Trigger Detected (Shake / Button / Voice / Dial Code)
                ↓
        Validation Engine
                ↓
        Fetch User Location
                ↓
    Parallel Execution Begins:
        → Make Call
        → Send SMS
        → Share Live Location
        → Notify Nearby Users
```

---

# 10. 🔐 Permissions Required

* Location (Fine & Coarse)
* SMS
* Call
* Microphone
* Internet
* Foreground Service
* Background Activity
* Bluetooth (optional)

---

# 11. ⚠️ Challenges

* False trigger detection
* Battery consumption (background services)
* Android background execution limits
* Privacy and data security
* Continuous voice listening optimization

---

# 12. 🚀 Future Scope

* AI-based danger detection (behavior analysis)
* Smartwatch integration
* Police/emergency services API integration
* Automatic video/audio recording during emergencies
* Cloud-based alert history

---

# 13. 🧠 Unique Selling Proposition (USP)

> “Alertify enables emergency activation through multiple hidden triggers and extends help beyond personal contacts using a real-time nearby user alert system.”

---

# 👨‍💻 Team Work Division (4 Members)

---

## 👤 Member 1: Android Core Developer (Triggers + Background Services)

### Responsibilities:

* Implement shake detection using Sensors API
* Implement volume button trigger detection
* Implement secret dial code detection
* Develop background and foreground services

### Key Java Classes:

* `ShakeDetector.java`
* `VolumeButtonReceiver.java`
* `DialCodeReceiver.java`
* `BackgroundService.java`

---

## 👤 Member 2: Communication & Emergency Module Developer

### Responsibilities:

* SMS sending functionality
* Automatic calling system
* Emergency contact management
* Runtime permissions handling

### Key Java Classes:

* `SmsSender.java`
* `CallHandler.java`
* `EmergencyManager.java`
* `ContactManager.java`

---

## 👤 Member 3: Location & Backend Developer

### Responsibilities:

* GPS location tracking
* Google Maps link generation
* Firebase integration
* Nearby alert system logic

### Key Java Classes:

* `LocationService.java`
* `MapsUtils.java`
* `FirebaseHelper.java`
* `NearbyAlertService.java`

---

## 👤 Member 4: UI/UX & Voice System Developer

### Responsibilities:

* UI design using XML
* Voice recognition feature
* Fake calculator interface
* User onboarding flow

### Key Java Classes:

* `MainActivity.java`
* `FakeCalculatorActivity.java`
* `VoiceTriggerService.java`

---

# 🗂️ Suggested Project Structure (Java)

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
│   │   │   ├── ShakeDetector.java
│   │   │   ├── VolumeButtonReceiver.java
│   │   │   ├── VoiceTriggerService.java
│   │   │   └── DialCodeReceiver.java
│   │   │
│   │   ├── emergency/
│   │   │   ├── EmergencyManager.java
│   │   │   ├── SmsSender.java
│   │   │   ├── CallHandler.java
│   │   │   └── AlertDispatcher.java
│   │   │
│   │   ├── location/
│   │   │   ├── LocationService.java
│   │   │   ├── LocationHelper.java
│   │   │   └── MapsUtils.java
│   │   │
│   │   ├── contacts/
│   │   │   ├── ContactModel.java
│   │   │   ├── ContactRepository.java
│   │   │   └── ContactManager.java
│   │   │
│   │   ├── nearby/
│   │   │   ├── NearbyAlertService.java
│   │   │   ├── FirebaseHelper.java
│   │   │   └── AlertListener.java
│   │   │
│   │   ├── services/
│   │   │   ├── BackgroundService.java
│   │   │   └── ForegroundService.java
│   │   │
│   │   ├── stealth/
│   │   │   ├── FakeCalculatorActivity.java
│   │   │   └── StealthManager.java
│   │   │
│   │   ├── utils/
│   │   │   ├── Constants.java
│   │   │   ├── PermissionUtils.java
│   │   │   └── Logger.java
│   │   │
│   │   └── MainApplication.java
│   │
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│   │   └── mipmap/
│   │
│   └── Gradle Scripts
```