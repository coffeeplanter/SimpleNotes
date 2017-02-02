package ru.solovyov.ilya.simplenotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.Voice;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// Класс главной активности со списком заметок

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    static final int REQUEST_CODE_NOTE = 1; // Код для запуска активности редактирования
    static final int FIRST_LAUNCH_MODE = 0;
    static final int CREATION_ACTIVITY_MODE = 1;
    static final int RESTORED_ACTIVITY_MODE = 2;
    static final String NOTES_JSON_STRING = "NOTES";
    static final String NOTE_FLAG_INTENT = "NOTE";
    static final String NOTE_POSITION_FLAG_INTENT = "POSITION";
    static final String NOTE_NEW_FLAG_INTENT = "ISNEW";
    static final int VOICE_PROCESS_LIST = 0;
    static final int VOICE_PROCESS_EDITOR = 1;

    ListView listView; // Наш список
    ToggleButton buttonVoiceAdd; // Кнопка для голосового ввода
    ProgressBar progressBar; // Визуальные компоненты объявлены здесь для доступа из других классов
    NoteAdapter adapter; // экземпляр адаптера
    List<Note> notes; // Контейнер для данных
    SpeechRecognizer speechRecognizer;
    VoiceRecognitionImplementation recognitionListener;
    Intent voiceIntent;
    static int noteTextAppearance = android.R.style.TextAppearance_Medium;
    static boolean isEditedDate = true;
    static int voiceTimeout = 10000;
    static int voiceProcessMethod = VOICE_PROCESS_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Режим запуска приложений для инициалиации данных
        int application_mode;

        // Устанавливаем разметку
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Проверка и удаление настроек из SettingsActivity
//        SharedPreferences def = PreferenceManager.getDefaultSharedPreferences(this);
//        def.edit().clear().commit();
//        Map<String, ?> all = def.getAll();
//        for (Map.Entry<String, ?> entry : all.entrySet()) {
//            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
//        }

        // Устанавливаем режим создания активности
        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
        String json = options.getString(NOTES_JSON_STRING, "");
        if ((json.equals(""))) {
            // Приложение запускаем первый раз или если нет файла настроек
            application_mode = FIRST_LAUNCH_MODE;
        }
        else if (savedInstanceState == null) {
            // Активность создана с нуля
            application_mode = CREATION_ACTIVITY_MODE;
        }
        else {
            // Активность воссоздана после уничтожения системой
            application_mode = RESTORED_ACTIVITY_MODE;
        }
        Log.d(TAG, "application_mode: " + application_mode);

        // Получаем экземпляр элемента ListView
        listView = (ListView)findViewById(android.R.id.list);

        Gson gson;

        switch (application_mode) {
            case FIRST_LAUNCH_MODE:
                notes = new ArrayList<>();
                break;
            case RESTORED_ACTIVITY_MODE:
                json = savedInstanceState.getString(NOTES_JSON_STRING);
            case CREATION_ACTIVITY_MODE:
                gson = new Gson();
                notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                Log.d(TAG, "notes: " + notes.toString());
                adapter = new NoteAdapter(this, R.layout.note_item, notes);
                Log.d(TAG, "adapter: " + adapter.toString());
                listView.setAdapter(adapter);
                break;
        }

        // Указываем ListView то мы хотим режим с мультивыделеним
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        // Указываем обработчик такого режима
        listView.setMultiChoiceModeListener(new MultiChoiceImplementation(this, listView));

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });

        // Назначаем слушатель кнопке текстового добавления заметки
        Button buttonAdd = (Button) findViewById(R.id.add_by_text_button);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NOTE);
            }
        });

        // Подготовка обработки голосового ввода
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionListener = new VoiceRecognitionImplementation(this, VoiceRecognitionImplementation.ADD_NEW_NOTE_LIST);
        speechRecognizer.setRecognitionListener(recognitionListener);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout);

        // Готовим интент для голосового ввода
        voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, voiceTimeout); // Похоже, этот параметр не работает
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de"); // Установка других языков

        // Назначаем слушатель кнопке голосового добавления заметки
        buttonVoiceAdd = (ToggleButton) findViewById(R.id.add_by_voice_button);
        buttonVoiceAdd.setAllCaps(true);
        buttonVoiceAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorVoiceRecording));
                    Log.d(TAG, "" + voiceTimeout);
                    speechRecognizer.startListening(voiceIntent);
                }
                else {
                    linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorBackground));
                    speechRecognizer.stopListening();
                    SharedPreferences optionsFromSettingsActivity = PreferenceManager.getDefaultSharedPreferences(buttonView.getContext());
                    sortNotes(notes, optionsFromSettingsActivity);
                    adapter.notifyDataSetChanged();
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.smoothScrollToPosition(0);
                            listView.setSelection(0);
                        }
                    });
                    Toast.makeText(MainActivity.this, R.string.voice_listening_stopped_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        SharedPreferences optionsFromSettingsActivity = PreferenceManager.getDefaultSharedPreferences(this);
        switch (optionsFromSettingsActivity.getString("notes_text_size", "1")) {
            case "textAppearanceSmall":
                noteTextAppearance = android.R.style.TextAppearance_Small;
                break;
            case "textAppearanceMedium":
                noteTextAppearance = android.R.style.TextAppearance_Medium;
                break;
            case "textAppearanceLarge":
                noteTextAppearance = android.R.style.TextAppearance_Large;
                break;
        }
        sortNotes(notes, optionsFromSettingsActivity);
        switch (Integer.parseInt(optionsFromSettingsActivity.getString("voice_process_method", "0"))) {
            case VOICE_PROCESS_LIST:
                voiceProcessMethod = VOICE_PROCESS_LIST;
                recognitionListener.setCurrentMode(VoiceRecognitionImplementation.ADD_NEW_NOTE_LIST);
                break;
            case VOICE_PROCESS_EDITOR:
                voiceProcessMethod = VOICE_PROCESS_EDITOR;
                recognitionListener.setCurrentMode(VoiceRecognitionImplementation.ADD_NEW_NOTE_EDITOR);
                break;
        }
//        voiceTimeout = getVoiceTimeout(optionsFromSettingsActivity); // Отключено
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    // Сохраняем список заметок в настройках
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = options.edit();
        editor.putString(NOTES_JSON_STRING, json);
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        speechRecognizer.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_settings_activity:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.open_activity_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Сохраняем текущий список заметок при уничтожении активности, вызванном системой
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        outState.putString(NOTES_JSON_STRING, json);
        super.onSaveInstanceState(outState);
    }


    // Показываем контекстное меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            menu.setHeaderTitle(getResources().getText(R.string.note_context_menu));
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    // Обрабатываем нажатия в контекстном меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.copy_context:
                String textToCopy = adapter.getItem(info.position).getText();
                String label = "Note text";
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(label, textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.copied_to_clipboard_toast, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_context:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra(NOTE_FLAG_INTENT, adapter.getItem(info.position).getText());
                intent.putExtra(NOTE_POSITION_FLAG_INTENT, info.position);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
                return true;
            case R.id.voice_edit_context:
                recognitionListener.setCurrentNote(adapter.getItem(info.position));
                if (voiceProcessMethod == VOICE_PROCESS_EDITOR) {
                    recognitionListener.setCurrentMode(VoiceRecognitionImplementation.CHANGE_NOTE_EDITOR);
                    recognitionListener.setCurrentNotePosition(info.position);
                }
                else {
                    recognitionListener.setCurrentMode(VoiceRecognitionImplementation.CHANGE_NOTE_LIST);
                }
                buttonVoiceAdd.setChecked(true);
                return true;
            case R.id.delete_context:
                adapter.remove(adapter.getItem(info.position));
                Toast.makeText(this, R.string.note_deleted_toast, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Обрабатываем информацию от активности редактирования
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTE && data != null) {
            String noteText = data.getStringExtra(NOTE_FLAG_INTENT);
            Boolean isNew = data.getBooleanExtra(NOTE_NEW_FLAG_INTENT, true);
            int position = data.getIntExtra(NOTE_POSITION_FLAG_INTENT, -1);
            // Если создаётся новая заметка
            if (isNew) {
                if (noteText != null) {
                    if (notes == null) {
                        notes = new ArrayList<>();
                        notes.add(new Note(noteText));
                        adapter = new NoteAdapter(this, R.layout.note_item, notes);
                        listView.setAdapter(adapter);
                    }
                    else {
                        notes.add(0, new Note(noteText));
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, R.string.note_added_toast, Toast.LENGTH_SHORT).show();
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.smoothScrollToPosition(0);
                            listView.setSelection(0);
                        }
                    });
                }
            }
            // Если редактируется существующая заметка
            else {
                adapter.getItem(position).setText(noteText);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.note_edited_toast, Toast.LENGTH_SHORT).show();
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPosition(0);
                        listView.setSelection(0);
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Сортировка заметок в зависимости от настроек
    List<Note> sortNotes(List<Note> notesList, SharedPreferences options) {
        if (notesList != null) {
            if (Integer.parseInt(options.getString("notes_sort", "0")) == 0) {
                isEditedDate = true;
                if (Integer.parseInt(options.getString("notes_sort_order", "0")) == 0) {
                    Collections.sort(notesList, new Comparator<Note>() {
                        @Override
                        public int compare(Note note, Note t1) {
                            return t1.getLastEditedDate().compareTo(note.getLastEditedDate());
                        }
                    });
                }
                else if (Integer.parseInt(options.getString("notes_sort_order", "0")) == 1) {
                    Collections.sort(notesList, new Comparator<Note>() {
                        @Override
                        public int compare(Note note, Note t1) {
                            return note.getLastEditedDate().compareTo(t1.getLastEditedDate());
                        }
                    });
                }
            }
            else if (Integer.parseInt(options.getString("notes_sort", "0")) == 1) {
                isEditedDate = false;
                if (Integer.parseInt(options.getString("notes_sort_order", "0")) == 0) {
                    Collections.sort(notesList, new Comparator<Note>() {
                        @Override
                        public int compare(Note note, Note t1) {
                            return t1.getCreatedDate().compareTo(note.getCreatedDate());
                        }
                    });
                }
                else if (Integer.parseInt(options.getString("notes_sort_order", "0")) == 1) {
                    Collections.sort(notesList, new Comparator<Note>() {
                        @Override
                        public int compare(Note note, Note t1) {
                            return note.getCreatedDate().compareTo(t1.getCreatedDate());
                        }
                    });
                }
            }
        }
        return notesList;
    }

    // Установка таймаута голосового ввода в зависимости от настроек
    private int getVoiceTimeout(SharedPreferences options) {
        String timeoutString = options.getString("voice_input_timeout", "10");
        try {
            int timeout = Integer.parseInt(timeoutString);
            if (timeout <= 0) {
                return 10000;
            }
            else if (timeout > 120) {
                return 120000;
            }
            return timeout * 1000;
        }
        catch (NumberFormatException nfe) {
            return 10000;
        }
    }

}
