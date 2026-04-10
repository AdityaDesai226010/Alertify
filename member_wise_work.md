

# 👨‍💻 **TEAM STRUCTURE (Java - Android)**

| Member      | Role                   | Main Responsibility            |
| ----------- | ---------------------- | ------------------------------ |
| 👤 Member 1 | Core Trigger Developer | Sensors + Detection            |
| 👤 Member 2 | Emergency System Dev   | SMS + Calling + Contacts       |
| 👤 Member 3 | Backend & Location Dev | GPS + Firebase + Nearby Alerts |
| 👤 Member 4 | UI/UX + Voice Dev      | UI + Voice + Stealth Mode      |

---

# 🔴 👤 **MEMBER 1 – TRIGGER SYSTEM (CORE LOGIC)**

## 🎯 Goal:

Detect emergency using **hardware + sensors**

---

## 📦 MODULES:

* ShakeDetector.java
* VolumeButtonReceiver.java
* DialCodeReceiver.java

---

## 📳 1. Shake Detection (Accelerometer)

### 📌 Concept:

Detect rapid movement using sensor data.

---

### 🧠 Logic:

* Measure acceleration (x, y, z)
* Calculate shake force
* Count shakes within time window

---

### ✅ Java Implementation:

```java
public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final int SHAKE_TIME_LAPSE = 500;
    private long lastShakeTime = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x*x + y*y + z*z);

        if (acceleration > SHAKE_THRESHOLD) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastShakeTime > SHAKE_TIME_LAPSE) {
                lastShakeTime = currentTime;

                // Trigger Emergency
                EmergencyManager.triggerEmergency(event.context);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
```

---

## 🔘 2. Volume Button Trigger

### 📌 Concept:

Detect multiple presses of volume button

---

### ⚠️ Note:

Requires:

* Accessibility Service OR
* KeyEvent detection in activity

---

### 🧠 Logic:

* Count presses
* Reset after time window

---

### Sample Logic:

```java
int count = 0;
long lastPressTime = 0;

public void onVolumePressed() {
    long currentTime = System.currentTimeMillis();

    if (currentTime - lastPressTime < 2000) {
        count++;
    } else {
        count = 1;
    }

    lastPressTime = currentTime;

    if (count >= 3) {
        EmergencyManager.triggerEmergency(context);
        count = 0;
    }
}
```

---

## 📞 3. Dial Code Trigger

### 📌 Concept:

User dials `*123#` → trigger alert

---

### Implementation:

* Use BroadcastReceiver
* Detect outgoing call intent

---

```java
public class DialCodeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String dialedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if ("*123#".equals(dialedNumber)) {
            setResultData(null); // cancel actual call
            EmergencyManager.triggerEmergency(context);
        }
    }
}
```

---

# 🚨 👤 **MEMBER 2 – EMERGENCY SYSTEM**

## 🎯 Goal:

Execute actions when emergency is triggered

---

## 📦 MODULES:

* EmergencyManager.java
* SmsSender.java
* CallHandler.java
* ContactManager.java

---

## 🧠 CENTRAL CLASS (VERY IMPORTANT)

### 🔥 EmergencyManager.java

```java
public class EmergencyManager {

    public static void triggerEmergency(Context context) {

        String location = LocationHelper.getLocationLink(context);

        SmsSender.sendSMS(context, location);
        CallHandler.makeCall(context);
        NearbyAlertService.sendAlert(context, location);
    }
}
```

---

## 📩 SMS Sender

```java
public class SmsSender {

    public static void sendSMS(Context context, String location) {
        SmsManager smsManager = SmsManager.getDefault();

        String message = "I am in danger! Help me!\nLocation: " + location;

        for (String number : ContactManager.getContacts(context)) {
            smsManager.sendTextMessage(number, null, message, null, null);
        }
    }
}
```

---

## 📞 Call Handler

```java
public class CallHandler {

    public static void makeCall(Context context) {
        String number = ContactManager.getPrimaryContact(context);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
```

---

## 👥 Contact Manager

* Store contacts in SharedPreferences / SQLite

---

# 📍 👤 **MEMBER 3 – LOCATION + BACKEND**

## 🎯 Goal:

Handle GPS + Firebase + Nearby Alerts

---

## 📦 MODULES:

* LocationHelper.java
* LocationService.java
* FirebaseHelper.java
* NearbyAlertService.java

---

## 📍 Location Fetching

```java
public class LocationHelper {

    public static String getLocationLink(Context context) {
        double lat = 18.5204;  // demo
        double lng = 73.8567;

        return "https://maps.google.com/?q=" + lat + "," + lng;
    }
}
```

---

## 📡 Firebase Alert System

```java
public class FirebaseHelper {

    public static void sendAlert(String location) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("alerts");

        String id = ref.push().getKey();

        ref.child(id).setValue(location);
    }
}
```

---

## 📡 Nearby Alert

```java
public class NearbyAlertService {

    public static void sendAlert(Context context, String location) {
        FirebaseHelper.sendAlert(location);
    }
}
```

---

# 🎨 👤 **MEMBER 4 – UI + VOICE + STEALTH**

## 🎯 Goal:

User interface + hidden features

---

## 📦 MODULES:

* MainActivity.java
* VoiceTriggerService.java
* FakeCalculatorActivity.java

---

## 🎙️ Voice Recognition

```java
SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(context);

recognizer.setRecognitionListener(new RecognitionListener() {

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (data.get(0).toLowerCase().contains("help me")) {
            EmergencyManager.triggerEmergency(context);
        }
    }
});
```

---

## 🔒 Fake Calculator Screen

👉 UI looks like calculator
👉 Secret input → opens real app

---

### Logic:

```java
if (input.equals("1234")) {
    openMainApp();
}
```

---

# 🔄 FINAL FLOW (IMPORTANT FOR TEAM)

```text
[Trigger Detected]
   ↓
Member 1 Module
   ↓
EmergencyManager (Member 2)
   ↓
→ SMS
→ Call
→ Location (Member 3)
→ Firebase Alert (Member 3)
   ↓
Nearby Users Notified
```

---

# 🧠 TEAM COORDINATION RULE (VERY IMPORTANT)

👉 EVERY trigger MUST call:

```java
EmergencyManager.triggerEmergency(context);
```

This keeps:

* Clean architecture ✅
* No confusion ✅
* Easy debugging ✅

---

