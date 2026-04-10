
---

# 📄 **Software Design Document (SDD)**

## 🛡️ Project: **Alertify – Smart Emergency Response System**

---

# 1. 📌 System Overview

**Alertify** is an Android-based safety system that uses **multi-trigger detection + automated emergency response** to assist users in danger situations.

The system operates in the background and activates emergency protocols using:

* Motion sensors
* Hardware buttons
* Voice commands
* Hidden dial codes

---

# 2. 🏗️ System Architecture

## 🔷 Architecture Type:

👉 **Modular + MVVM (Model-View-ViewModel)**

---

## 📊 High-Level Architecture

```id="kq9i4g"
+----------------------+
|      UI Layer        |
| (Activities/Views)   |
+----------+-----------+
           |
           v
+----------------------+
|   ViewModel Layer    |
|  (Business Logic)    |
+----------+-----------+
           |
           v
+----------------------+
|   Core Modules       |
| (Triggers, Emergency)|
+----------+-----------+
           |
           v
+----------------------+
|   Services Layer     |
| (Location, SMS, Call)|
+----------+-----------+
           |
           v
+----------------------+
| External Systems     |
| (Firebase, Maps API) |
+----------------------+
```

---

# 3. 🧩 Module Design

---

## 🔴 3.1 Trigger Module

### 📌 Purpose:

Detect emergency situations using multiple input sources.

---

### 📦 Components:

* ShakeDetector
* VolumeButtonReceiver
* VoiceTriggerService
* DialCodeReceiver

---

### 🔄 Flow:

```id="z41r4l"
Sensor Input → Pattern Detection → Validation → Trigger Emergency
```

---

### ⚠️ Design Considerations:

* Avoid false positives
* Configurable sensitivity
* Low battery usage

---

## 🚨 3.2 Emergency Module

### 📌 Purpose:

Execute all emergency actions when triggered

---

### 📦 Components:

* EmergencyManager (central controller)
* SmsSender
* CallHandler
* AlertDispatcher

---

### 🔄 Flow:

```id="7uqnpq"
Trigger → EmergencyManager → Parallel Execution:
   → SMS
   → Call
   → Location Sharing
   → Nearby Alert
```

---

### 🧠 Design Principle:

👉 **Single Responsibility + Centralized Control**

---

## 📍 3.3 Location Module

### 📌 Purpose:

Provide accurate and continuous user location

---

### 📦 Components:

* LocationService (background updates)
* LocationHelper
* MapsUtils

---

### ⚙️ Design Decisions:

* Use FusedLocationProvider (better accuracy)
* Fallback to last known location

---

## 📡 3.4 Nearby Alert Module

### 📌 Purpose:

Notify nearby users using Alertify

---

### 📦 Components:

* NearbyAlertService
* FirebaseHelper
* AlertListener

---

### 🔄 Flow:

```id="1r1bdg"
User Alert → Firebase Upload → Nearby Users Fetch → Push Notification
```

---

### 🧠 Design Choice:

* Cloud-based detection (scalable)
* Optional BLE for offline proximity

---

## 🧑‍🤝‍🧑 3.5 Contact Module

### 📌 Purpose:

Manage emergency contacts

---

### 📦 Components:

* ContactModel
* ContactRepository
* ContactManager

---

### 📊 Data Structure:

```id="2ixq4l"
Contact {
  name: String
  phone: String
  priority: Int
}
```

---

## 🔒 3.6 Stealth Module

### 📌 Purpose:

Hide app functionality for user safety

---

### 📦 Components:

* FakeCalculatorActivity
* StealthManager

---

### Features:

* Fake UI
* Silent triggers
* Background execution

---

## ⚙️ 3.7 Services Module

### 📌 Purpose:

Run app continuously in background

---

### 📦 Components:

* ForegroundService
* BackgroundService

---

### Design Decision:

* Foreground service required for Android 10+

---

# 4. 🔄 Data Flow Design

---

## 🔁 End-to-End Flow

```id="18qu2n"
User Trigger (Shake/Button/Voice/Code)
            ↓
Trigger Module
            ↓
Validation Engine
            ↓
EmergencyManager
            ↓
Parallel Execution:
   → Send SMS
   → Make Call
   → Fetch Location
   → Notify Nearby Users
            ↓
User Gets Help 🚨
```

---

# 5. 🗄️ Data Design

---

## 📊 Local Storage:

* SharedPreferences:

  * Settings
  * Trigger sensitivity
* SQLite / Room DB:

  * Contacts

---

## ☁️ Cloud Storage (Firebase):

* User alerts
* Location updates
* Nearby user data

---

# 6. 🔐 Security Design

---

## 🔒 Measures:

* Permission-based access
* Minimal data storage
* Encrypted Firebase communication (HTTPS)
* No unnecessary data collection

---

## ⚠️ Risks:

* Misuse of location data
* Unauthorized access

---

# 7. ⚡ Performance Design

---

## 🎯 Goals:

* Trigger response time < 2 seconds
* Low battery consumption
* Efficient background execution

---

## ⚙️ Optimization:

* Use lightweight services
* Limit GPS polling frequency
* Optimize voice recognition usage

---

# 8. ❗ Error Handling

---

## 🛠️ Cases:

* No internet → fallback to SMS
* GPS off → use last known location
* Call failed → retry

---

# 9. 🧪 Testing Strategy

---

## ✅ Unit Testing:

* Trigger detection logic
* SMS sending

## ✅ Integration Testing:

* Full emergency flow

## ✅ User Testing:

* Real-world simulation (shake, button)

---

# 10. 📱 UI/UX Design Principles

---

* Simple and minimal UI
* One-tap emergency button
* Hidden/stealth mode support
* Fast access to contacts

---

# 11. 🚀 Deployment Plan

---

* APK build for testing
* Install on multiple devices
* Real-time demo

---
