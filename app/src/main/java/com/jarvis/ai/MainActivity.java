package com.jarvis.ai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int AUDIO_PERMISSION = 101;

    private TextView status, result;
    private SpeechRecognizer recognizer;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private String language = "bn-BD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(28, 28, 28, 28);
        layout.setBackgroundColor(Color.rgb(8, 15, 30));

        TextView title = new TextView(this);
        title.setText("JARVIS AI");
        title.setTextSize(34);
        title.setTextColor(Color.rgb(0, 220, 255));
        title.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setText("System ready • সিস্টেম প্রস্তুত");
        status.setTextSize(16);
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 24, 0, 24);

        result = new TextView(this);
        result.setText("তোমার ভাষা বেছে নাও");
        result.setTextSize(18);
        result.setTextColor(Color.WHITE);
        result.setGravity(Gravity.CENTER);
        result.setPadding(0, 12, 0, 24);

        Button bangla = new Button(this);
        bangla.setText("বাংলা");
        bangla.setOnClickListener(v -> {
            language = "bn-BD";
            setStatus("বাংলা ভাষা নির্বাচিত");
            setTtsLanguage();
        });

        Button english = new Button(this);
        english.setText("English");
        english.setOnClickListener(v -> {
            language = "en-US";
            setStatus("English selected");
            setTtsLanguage();
        });

        Button speak = new Button(this);
        speak.setText("🎙 কথা বলুন / Speak");
        speak.setTextSize(18);
        speak.setOnClickListener(v -> startListening());

        layout.addView(title);
        layout.addView(status);
        layout.addView(result);
        layout.addView(bangla);
        layout.addView(english);
        layout.addView(speak);
        setContentView(layout);

        tts = new TextToSpeech(this, code -> {
            if (code == TextToSpeech.SUCCESS) {
                ttsReady = true;
                setTtsLanguage();
            }
        });

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override public void onReadyForSpeech(Bundle params) {
                    setStatus(language.startsWith("bn")
                            ? "শুনছি... এখন কথা বলো" : "Listening... speak now");
                }
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {
                    setStatus(language.startsWith("bn")
                            ? "কথা বুঝতে চেষ্টা করছি..." : "Processing speech...");
                }
                @Override public void onError(int error) {
                    setStatus(language.startsWith("bn")
                            ? "শোনা যায়নি। আবার চেষ্টা করো।"
                            : "Could not hear you. Please try again.");
                }
                @Override public void onResults(Bundle results) {
                    ArrayList<String> matches =
                            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        handleCommand(matches.get(0));
                    } else {
                        setStatus("No speech detected");
                    }
                }
                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        } else {
            setStatus("Speech recognition is not available on this device.");
        }
    }

    private void startListening() {
        if (recognizer == null) {
            setStatus("Speech recognition is unavailable.");
            return;
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    AUDIO_PERMISSION
            );
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        try {
            recognizer.startListening(intent);
        } catch (Exception e) {
            setStatus("Could not start microphone. Try again.");
        }
    }

    private void handleCommand(String heard) {
        result.setText("You said / তুমি বলেছ:\n" + heard);
        String text = heard.toLowerCase(Locale.ROOT);
        String response;

        if (text.contains("সময়") || text.contains("সময়")
                || text.contains("কটা বাজে") || text.contains("time")
                || text.contains("what time")) {
            String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date());
            response = language.startsWith("bn")
                    ? "এখন সময় " + time : "The time is " + time;
        } else if (text.contains("তোমার নাম") || text.contains("তোমার পরিচয়")
                || text.contains("your name") || text.contains("who are you")) {
            response = language.startsWith("bn")
                    ? "আমি জার্ভিস, তোমার AI সহকারী।"
                    : "I am Jarvis, your AI assistant.";
        } else if (text.contains("হ্যালো") || text.contains("হাই")
                || text.contains("hello") || text.contains("hi jarvis")
                || text.contains("hey jarvis")) {
            response = language.startsWith("bn")
                    ? "হ্যালো স্যার! আমি কীভাবে সাহায্য করতে পারি?"
                    : "Hello, Sir! How can I help you?";
        } else if (text.contains("চুপ") || text.contains("stop speaking")
                || text.contains("be quiet")) {
            if (tts != null) tts.stop();
            response = language.startsWith("bn") ? "ঠিক আছে।" : "Okay.";
        } else {
            response = language.startsWith("bn")
                    ? "দুঃখিত, এই কমান্ডটি এখনো শিখিনি।"
                    : "Sorry, I do not know that command yet.";
        }

        setStatus(response);
        speakResponse(response);
    }

    private void setTtsLanguage() {
        if (ttsReady && tts != null) {
            Locale locale = language.startsWith("bn")
                    ? new Locale("bn", "BD") : Locale.US;
            int availability = tts.isLanguageAvailable(locale);
            if (availability >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                setStatus(language.startsWith("bn")
                        ? "এই ফোনে বাংলা voice নেই; English voice ব্যবহার করো।"
                        : "English voice may not be available on this device.");
            }
        }
    }

    private void speakResponse(String response) {
        if (ttsReady && tts != null) {
            tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply");
        }
    }

    private void setStatus(String message) {
        if (status != null) status.setText(message);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else if (requestCode == AUDIO_PERMISSION) {
            setStatus("Microphone permission is required.");
        }
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
