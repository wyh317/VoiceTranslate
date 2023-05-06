package org.example;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.translation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.Map;

public class VoiceTranslate {
        public static void main(String[] args) throws InterruptedException, ExecutionException {
            System.setProperty("file.encoding", "UTF-8");
            String speechKey = "e911869294294d8e8edc28cd9a001d49";
            String speechRegion = "eastus";
            SpeechTranslationConfig speechTranslationConfig = SpeechTranslationConfig.fromSubscription(speechKey, speechRegion);
            speechTranslationConfig.setSpeechRecognitionLanguage("en-US");

            String[] toLanguages = { "zh-CN" };
            for (String language : toLanguages) {
                speechTranslationConfig.addTargetLanguage(language);
            }
            String text = recognizeFromMicrophone(speechTranslationConfig);
            textToVoice(text);
        }

        public static String recognizeFromMicrophone(SpeechTranslationConfig speechTranslationConfig) throws InterruptedException, ExecutionException {
            AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
            TranslationRecognizer translationRecognizer = new TranslationRecognizer(speechTranslationConfig, audioConfig);

            System.out.println("Speak into your microphone.");
            Future<TranslationRecognitionResult> task = translationRecognizer.recognizeOnceAsync();
            TranslationRecognitionResult translationRecognitionResult = task.get();
            String text = "";
            if (translationRecognitionResult.getReason() == ResultReason.TranslatedSpeech) {
                System.out.println("RECOGNIZED: Text=" + translationRecognitionResult.getText());
                for (Map.Entry<String, String> pair : translationRecognitionResult.getTranslations().entrySet()) {
                    System.out.printf("Translated into '%s': %s\n", pair.getKey(), pair.getValue());
                    text = pair.getValue();
                }
            }
            else if (translationRecognitionResult.getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
            else if (translationRecognitionResult.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(translationRecognitionResult);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you set the speech resource key and region values?");
                }
            }

            return text;
        }


    public static void textToVoice(String text) throws ExecutionException, InterruptedException {
        String speechKey = "e911869294294d8e8edc28cd9a001d49";
        String speechRegion = "eastus";
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

        speechConfig.setSpeechSynthesisVoiceName("zh-CN-XiaochenNeural");

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        if (text.isEmpty())
        {
            return;
        }

        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            System.out.println("Speech synthesized to speaker for text [" + text + "]");
        }
        else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }

        System.exit(0);
    }
}