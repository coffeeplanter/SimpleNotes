package ru.solovyov.ilya.simplenotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
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

// Класс главной активности со списком заметок

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    static final int REQUEST_CODE_NOTE = 1; // Код для запуска активности редактирования
    static final int FIRST_LAUNCH_MODE = 0;
    static final int CREATION_ACTIVITY_MODE = 1;
    static final int RESTORED_ACTIVITY_MODE = 2;

    ListView listView; // Наш список
    ToggleButton buttonVoiceAdd; // Кнопка для голосового ввода
    ProgressBar progressBar; // Визуальные компоненты объявлены здесь для доступа из других классов
    NoteAdapter adapter; // экземпляр адаптера
    List<Note> notes; // Контейнер для данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Режим запуска приложений для инициалиации данных
        int application_mode;

        // Устанавливаем разметку
        setContentView(R.layout.activity_main);

        // Устанавливаем режим создания активности
        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
        String json = options.getString("NOTES", "");
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
        Log.d(TAG, "applicaion_mode: " + application_mode);
        Log.d(TAG, "json: " + json);

        // Получаем экземпляр элемента ListView
        listView = (ListView)findViewById(android.R.id.list);

        Gson gson;

        switch (application_mode) {
            case FIRST_LAUNCH_MODE:
                notes = null;
                break;
            case RESTORED_ACTIVITY_MODE:
                json = savedInstanceState.getString("NOTES");
            case CREATION_ACTIVITY_MODE:
                gson = new Gson();
                notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
                adapter = new NoteAdapter(this, R.layout.note_item, notes);
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
        final SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new VoiceRecognitionImplementation(this, adapter));

        // Готовим интент для голосового ввода
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de"); // Установка других языков

        // Назначаем слушатель кнопке голосового добавления заметки
        buttonVoiceAdd = (ToggleButton) findViewById(R.id.add_by_voice_button);
        buttonVoiceAdd.setAllCaps(true);
        buttonVoiceAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    speechRecognizer.startListening(intent);
                    Log.d(TAG, "onResults");
                }
                else {
                    speechRecognizer.stopListening();
                    Toast.makeText(MainActivity.this, "Voice listening stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Сохраняем список заметок в настройках
    @Override
    protected void onStop() {
        // Сортируем коллекцию по убыванию даты
        if (notes != null) {
            Collections.sort(notes, new Comparator<Note>() {
                @Override
                public int compare(Note note, Note t1) {
                    return t1.getDate().compareTo(note.getDate());
                }
            });
        }
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = options.edit();
        editor.putString("NOTES", json);
        editor.apply();
        super.onStop();
    }

    // Сохраняем текущий список заметок при уничтожении активности, вызванном системой
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        outState.putString("NOTES", json);
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
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_context:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("NOTE", adapter.getItem(info.position).getText());
                intent.putExtra("POSITION", info.position);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
                return true;
            case R.id.delete_context:
                adapter.remove(adapter.getItem(info.position));
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Обрабатываем информацию от активности редактирования
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTE && data != null) {
            String noteText = data.getStringExtra("NOTE");
            Boolean isNew = data.getBooleanExtra("isNew", true);
            int position = data.getIntExtra("POSITION", -1);
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
                    listView.smoothScrollToPosition(0);
                }
            }
            else {
                adapter.getItem(position).setText(noteText);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
