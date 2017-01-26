package ru.geekbrains.lesson7.simplenotes;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "NOTES_LOG";

    ListView listView;
    ArrayAdapter<Note> adapter; // экземпляр адаптера
    List<Note> notes; // Контейнер для данных
    //ArrayAdapter<String> adapter;
    //List<String> notesString;
    static final int REQUEST_CODE_NOTE = 1; // Код для запуска активности редактирования
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //RelativeLayout rl = (LinearLayout)findViewById(R.id.note_item);

        // Устанавливаем разметку
        setContentView(R.layout.activity_main);

        // Получаем экземпляр элемента ListView
        listView = (ListView)findViewById(android.R.id.list);

        // Временные данные
        notes = new ArrayList<>();
        notes.add(new Note("Первая заметка"));
        notes.add(new Note("Вторая заметка"));
        notes.add(new Note("Третья заметка"));
        notes.add(new Note("Минувшей осенью компания LeEco представила у себя на родине в Китае свой новый смартфон из третьего уже поколения, LeEco Le Pro 3, который позиционировался как первый китайский смартфон, оснащенный однокристальной системой Snapdragon 821. Теперь аппарат официально поступает в продажу в России: компания, ранее носившая название LeTV, а нынче именуемая LeEco, с недавнего времени вознамерилась покорить российский рынок, для чего предпринимает заметные усилия. Виктор Сюй, президент LeRee (LeEco Russia and Eastern Europe), дочерней компании холдинга LeEco, специально созданной для ведения бизнеса в России и Восточной Европе, а также в странах СНГ, заявил в частности о планах инвестировать в российскую экономику от 0,5 до 1 млрд долларов в течение трех лет."));
        notes.add(new Note("Четвёртая заметка"));
        notes.add(new Note("Пятая заметка"));

//        // Проверяем, была активность восстановлена или запущена заново
//        boolean wasRestored = (savedInstanceState != null);
//        if (!wasRestored) {
//            // Если запущена заново, читаем список заметок из настроек и сортируем его
//            SharedPreferences options = this.getPreferences(MODE_PRIVATE);
//            String json = options.getString("NOTES", "");
//            Gson gson = new Gson();
//            notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
////            Collections.sort(notes, new Comparator<Note>() {
////                @Override
////                public int compare(Note note, Note t1) {
////                    return t1.getDate().compareTo(note.getDate());
////                }
////            });
//        }
//        else {
//            // Если восстановлена, восстанавливаем список из объекта Bundle
//            String json = savedInstanceState.getString("NOTES");
//            Gson gson = new Gson();
//            notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
//        }
//
//        if (notes == null) {
//            notes = new ArrayList<>();
//            notes.add(new Note("Первая заметка"));
//        }

        adapter = new NoteAdapter(this, R.layout.note_item, notes, listView);
        // Используем наш адаптер данных
        //adapter = new NoteAdapter(this, notes);
        listView.setAdapter(adapter);

        //Указываем ListView то мы хотим режим с мультивыделеним
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //Указываем обработчик такого режима
        listView.setMultiChoiceModeListener(new MultiChoiceImplementation(listView));

        //registerForContextMenu(listView);

        // Назначаем слушатель кнопке
//        btnAdd = (Button) findViewById(R.id.add_button);
//        btnAdd.setOnClickListener(this);
    }

//    @Override
//    protected void onStop() {
//        Gson gson = new Gson();
//        String json = gson.toJson(notes);
//        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = options.edit();
//        editor.putString("NOTES", json);
//        editor.apply();
//        super.onStop();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Gson gson = new Gson();
//        String json = gson.toJson(notes);
//        outState.putString("NOTES", json);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view.equals(btnAdd)) {
//            Intent intent = new Intent(this, EditActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_NOTE);
//        }
//    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        if (v.getId() == android.R.id.list) {
//            menu.setHeaderTitle(getResources().getText(R.string.note_context_menu));
//            getMenuInflater().inflate(R.menu.context_menu, menu);
//        }
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete_context:
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//                adapter.remove(adapter.getItem(info.position));
//                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.delete_all_context:
//                adapter.clear();
//                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.add_context:
//                onClick(btnAdd);
//                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.edit_context:
//                Intent intent = new Intent(this, EditActivity.class);
//                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//                intent.putExtra("NOTE", adapter.getItem(info2.position).getText());
//                intent.putExtra("POSITION", info2.position);
//                startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_NOTE && data != null) {
//            String noteText = data.getStringExtra("NOTE");
//            Boolean isNew = data.getBooleanExtra("isNew", true);
//            int position = data.getIntExtra("POSITION", -1);
//            if (isNew) {
//                if (noteText != null) {
//                    //adapter.add(new Note(noteText));
//                    adapter.notifyDataSetChanged();
//                    listView.smoothScrollToPosition(listView.getAdapter().getCount()-1);
//                }
//            }
//            else {
//                //adapter.getItem(position).setText(noteText);
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }

}
