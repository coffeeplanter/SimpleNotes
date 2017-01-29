package ru.solovyov.ilya.simplenotes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

// Класс обработки голосового ввода

class VoiceRecognitionImplementation implements RecognitionListener {

    private static final String TAG = "VRecognitionListener";
    //private NoteAdapter adapter;
    //private Context parentContext;
    private MainActivity parentActivity;

    VoiceRecognitionImplementation(Context context) {
        //this.adapter = adapter;
        //this.parentContext = context;
        this.parentActivity = (MainActivity) context;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
        parentActivity.progressBar.setVisibility(View.VISIBLE);
        parentActivity.buttonVoiceAdd.setChecked(true);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "onError: " + error);
        parentActivity.progressBar.setVisibility(View.INVISIBLE);
        parentActivity.buttonVoiceAdd.setChecked(false);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults: " + results);
        processResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults: " + partialResults);
        processResults(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent: " + eventType);
    }

    // Обработка результатов вынесена в отдельный метод
    private void processResults(Bundle results) {
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        assert data != null;
        String noteText = data.get(0).toString();
        if (noteText != null) {
            if (parentActivity.notes == null) {
                parentActivity.notes = new ArrayList<>();
                parentActivity.notes.add(new Note(noteText));
                parentActivity.adapter = new NoteAdapter(parentActivity, R.layout.note_item, parentActivity.notes);
                parentActivity.listView.setAdapter(parentActivity.adapter);
            }
            else {
                parentActivity.notes.add(0, new Note(noteText));
            }
            parentActivity.adapter.notifyDataSetChanged();
            parentActivity.listView.smoothScrollToPosition(0);
        }
        parentActivity.progressBar.setVisibility(View.INVISIBLE);
        parentActivity.buttonVoiceAdd.setChecked(false);
        parentActivity.listView.smoothScrollToPosition(0);
    }

}
