package ru.solovyov.ilya.simplenotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

// Класс обработки голосового ввода

class VoiceRecognitionImplementation implements RecognitionListener {

    private static final String TAG = "VRecognitionListener";
    static final int ADD_NEW_NOTE_LIST = 0; // Флаг для добавления новой заметки в список
    static final int ADD_NEW_NOTE_EDITOR = 1; // Флаг для добавления новой заметки в редакторе
    static final int CHANGE_NOTE_LIST = 2; // Флаг для дозаписи существующей заметки в список
    static final int CHANGE_NOTE_EDITOR = 3; // Флаг для дозаписи существующей заметки в редакторе
    private MainActivity parentActivity;
    private Note currentNote; // Поле для хранения изменяемой заметки
    private int currentNotePosition; // Поле для передачи позиции в списке
    private int currentMode; // Режим обработки результатов голосового ввода (дополнение заметки или ввод новой)

    VoiceRecognitionImplementation(Context context, int flag) {
        this.parentActivity = (MainActivity) context;
        this.currentMode = flag;
        this.currentNote = null;
        this.currentNotePosition = -1;
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
            Log.d(TAG, "currentMode: " + currentMode);
            switch (currentMode) {
                case ADD_NEW_NOTE_LIST:
                    setCurrentNote(null);
                    currentNotePosition = -1;
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
                case ADD_NEW_NOTE_EDITOR:
                    setCurrentNote(null);
                    currentNotePosition = -1;
                    Intent intentAdd = new Intent(parentActivity, EditActivity.class);
                    intentAdd.putExtra(MainActivity.NOTE_FLAG_INTENT, noteText);
                    parentActivity.startActivityForResult(intentAdd, MainActivity.REQUEST_CODE_NOTE);
                    break;
                case CHANGE_NOTE_LIST:
                    currentNote.addText("\n" + noteText);
                    SharedPreferences optionsFromSettingsActivity = PreferenceManager.getDefaultSharedPreferences(parentActivity);
                    parentActivity.sortNotes(parentActivity.notes, optionsFromSettingsActivity);
                    setCurrentMode(ADD_NEW_NOTE_LIST);
                    setCurrentNote(null);
                    break;
                case CHANGE_NOTE_EDITOR:
                    Intent intentEdit = new Intent(parentActivity, EditActivity.class);
                    intentEdit.putExtra(MainActivity.NOTE_FLAG_INTENT, currentNote.getText() + "\n" + noteText);
                    Log.d(TAG, "Текущая заметка: " + currentNote.getText());
                    intentEdit.putExtra(MainActivity.NOTE_POSITION_FLAG_INTENT, currentNotePosition);
                    parentActivity.startActivityForResult(intentEdit, MainActivity.REQUEST_CODE_NOTE);
                    setCurrentNote(null);
                    break;
            }
            parentActivity.adapter.notifyDataSetChanged();
//            parentActivity.listView.smoothScrollToPosition(0);
//            parentActivity.listView.setSelection(0);
        }
        parentActivity.progressBar.setVisibility(View.INVISIBLE);
        parentActivity.buttonVoiceAdd.setChecked(false);
    }

    void setCurrentNote(Note note) {
        this.currentNote = note;
    }

    void setCurrentNotePosition(int position) {
        this.currentNotePosition = position;
    }

    void setCurrentMode(int mode) {
        this.currentMode = mode;
    }

    int getCurrentMode() {
        return this.currentMode;
    }

}
