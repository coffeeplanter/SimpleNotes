package ru.geekbrains.lesson7.simplenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class NoteAdapter extends ArrayAdapter<Note> {

    NoteAdapter(Context context, List<Note> notes) {
        super(context, R.layout.note_item, notes);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Note note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_item, null);
        }
        ((TextView) convertView.findViewById(R.id.note)).setText(note.getText());
        ((TextView) convertView.findViewById(R.id.date_last_edited)).setText(note.getFormattedDate());

        ((ImageView) convertView.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteAdapter adapter = (NoteAdapter) ((ListView)parent).getAdapter();
                adapter.remove(note);
                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
            }
        });
        ((ImageView) convertView.findViewById(R.id.edit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditActivity.class);
                intent.putExtra("NOTE", note.getText());
                intent.putExtra("POSITION", position);
                ((AppCompatActivity)getContext()).startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
            }
        });
        return  convertView;
    }
}
