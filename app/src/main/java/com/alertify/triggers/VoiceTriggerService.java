package com.alertify.triggers;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;

public class VoiceTriggerService extends Service {

    private SpeechRecognizer speechRecognizer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListening();
        return START_STICKY;
    }

    private void startListening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    for (String text : matches) {
                        if (text.toLowerCase().contains("help me")) {
                            triggerAlert();
                        }
                    }
                }
                startListening(); // Re-start listening
            }

            @Override
            public void onError(int i) {
                startListening(); // Re-start on error
            }

            // Other required overrides
            @Override public void onReadyForSpeech(Bundle bundle) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float v) {}
            @Override public void onBufferReceived(byte[] bytes) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle bundle) {}
            @Override public void onEvent(int i, Bundle bundle) {}
        });

        speechRecognizer.startListening(recognizerIntent);
    }

    private void triggerAlert() {
        // This will be implemented by Member 2
        // EmergencyManager.triggerEmergency(this);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
