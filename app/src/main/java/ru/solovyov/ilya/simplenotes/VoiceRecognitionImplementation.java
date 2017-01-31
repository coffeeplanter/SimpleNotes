package ru.solovyov.ilya.simplenotes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.regex.Pattern;

// Класс обработки голосового ввода

class VoiceRecognitionImplementation implements RecognitionListener {

    private static final String TAG = "VRecognitionListener";
    static final int ADD_NEW_NOTE = 1; // Флаг для добавления новой заметки
    static final int CHANGE_NOTE = 2; // Флаг для дозаписи существующей заметки
    private MainActivity parentActivity;
    private Note currentNote; // Поле для хранения изменяемой заметки
    private int currentMode; // Режим обработки результатов голосового ввода (дополнение заметки или ввод новой)

    VoiceRecognitionImplementation(Context context, int flag) {
        this.parentActivity = (MainActivity) context;
        this.currentMode = flag;
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
            // Обработка голосовой команды "Новая строка"
            noteText = noteText.replaceAll("(?i)" + "(^|\\s)" + Pattern.quote("новая строка") + "($|\\s)", "\n");
            noteText = noteText.replaceAll("(?i)" + Pattern.quote("пробел") + "($|\\s)", " ");
            switch (currentMode) {
                case ADD_NEW_NOTE:
                    if (parentActivity.notes == null) {
                        parentActivity.notes = new ArrayList<>();
                        parentActivity.notes.add(new Note(noteText));
                        parentActivity.adapter = new NoteAdapter(parentActivity, R.layout.note_item, parentActivity.notes);
                        parentActivity.listView.setAdapter(parentActivity.adapter);
                    }
                    else {
                        parentActivity.notes.add(0, new Note(noteText));
                    }
                    break;
                case CHANGE_NOTE:
                    currentNote.addText("\n" + noteText);
                    setCurrentMode(ADD_NEW_NOTE);
                    setCurrentNote(null);
                    break;
            }
            parentActivity.adapter.notifyDataSetChanged();
        }
        parentActivity.progressBar.setVisibility(View.INVISIBLE);
        parentActivity.buttonVoiceAdd.setChecked(false);
    }

    void setCurrentNote(Note note) {
        this.currentNote = note;
    }

    void setCurrentMode(int mode) {
        this.currentMode = mode;
    }

}
