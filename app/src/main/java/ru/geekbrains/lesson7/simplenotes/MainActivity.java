package ru.geekbrains.lesson7.simplenotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    ListView listView;
    ArrayAdapter<Note> adapter;
    List<Note> notes;
    static final int REQUEST_CODE_NOTE = 1;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // получаем экземпляр элемента ListView
        listView = (ListView)findViewById(R.id.notes_list);

//        notes = new ArrayList<>();
//        notes.add(new Note("Проверка"));
//        notes.add(new Note("Проверка 2", Calendar.getInstance()));
//        notes.add(new Note("Purpose of this tutorial and the project is not developing a perfect commercial application, but providing a basic idea to develop a simple application in Android inorder to apply your knowledge in database and ListView. The application has not been tested completely, so if there are any bugs, please comment below and I will try my best to fix them as soon as possible."));

        boolean wasRestored = (savedInstanceState != null);
        if (!wasRestored) {
            SharedPreferences options = this.getPreferences(MODE_PRIVATE);
            String json = options.getString("NOTES", "");
            Gson gson = new Gson();
            notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
        }
        else {
            String json = savedInstanceState.getString("NOTES");
            Gson gson = new Gson();
            notes = gson.fromJson(json, new TypeToken<List<Note>>(){}.getType());
        }

        // используем адаптер данных
        adapter = new NoteAdapter(this, notes);

        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        btnAdd = (Button) findViewById(R.id.add_button);
        btnAdd.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        SharedPreferences options = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = options.edit();
        editor.putString("NOTES", json);
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        outState.putString("NOTES", json);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnAdd)) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NOTE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.notes_list) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_context:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                adapter.remove(adapter.getItem(info.position));
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_all_context:
                adapter.clear();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.add_context:
                onClick(btnAdd);
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_context:
                Intent intent = new Intent(this, EditActivity.class);
                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                intent.putExtra("NOTE", adapter.getItem(info2.position).getText());
                intent.putExtra("POSITION", info2.position);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTE && data != null) {
            String noteText = data.getStringExtra("NOTE");
            Boolean isNew = data.getBooleanExtra("isNew", true);
            int position = data.getIntExtra("POSITION", -1);
            if (isNew) {
                if (noteText != null) {
                    adapter.add(new Note(noteText));
                    adapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(listView.getAdapter().getCount()-1);
                }
            }
            else {
                adapter.getItem(position).setText(noteText);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
