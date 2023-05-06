package org.example;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.translation.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.Map;

public class VoiceTranslate {
        private String speechKey;
        private String speechRegion;

        private SpeechTranslationConfig speechTranslationConfig;

        public VoiceTranslate(String speechKey, String speechRegion){
            this.speechRegion = speechRegion;
            this.speechKey = speechKey;
        }
        public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
            Properties properties = new Properties();
            properties.load(new FileReader("src\\main\\resources\\config.properties"));

            String speechKey = properties.getProperty("apiKey");
            String speechRegion = properties.getProperty("region");

            String originLanguage = args[0];
            String translateToLanguage = args[1];
            String voiceName = args[2];

            VoiceTranslate voiceTranslate = new VoiceTranslate(speechKey, speechRegion);
            String translatedText = voiceTranslate.recognizeFromMicrophone(originLanguage, translateToLanguage);
            voiceTranslate.textToVoice(translatedText, voiceName);
        }

    public String recognizeFromMicrophone(String originLanguage, String translateToLanguage) {
        AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechTranslationConfig speechTranslationConfig = SpeechTranslationConfig.fromSubscription(speechKey, speechRegion);
        speechTranslationConfig.setSpeechRecognitionLanguage(originLanguage);
        speechTranslationConfig.addTargetLanguage(translateToLanguage);
        TranslationRecognizer translationRecognizer = new TranslationRecognizer(speechTranslationConfig, audioConfig);

        System.out.println("Speak into your microphone with the origin language.");
        Future<TranslationRecognitionResult> task = translationRecognizer.recognizeOnceAsync();
        try {
            TranslationRecognitionResult translationRecognitionResult = task.get();
            if (translationRecognitionResult.getReason() == ResultReason.TranslatedSpeech) {
                System.out.println("Voice in origin language: " + translationRecognitionResult.getText());
                Map.Entry<String, String> pair = translationRecognitionResult.getTranslations().entrySet().iterator().next();
                return pair.getValue();
            } else{
                System.out.println("Speech could not be recognized.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void textToVoice(String text, String voiceName) {
        if (text.isEmpty())
        {
            return;
        }
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);
        try{
            SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
            if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Translate to [" + text + "]");
            }
            else{
                System.out.println("Text could not be translated to voice");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}