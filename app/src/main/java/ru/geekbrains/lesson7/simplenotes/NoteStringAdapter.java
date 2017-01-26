package ru.geekbrains.lesson7.simplenotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class NoteStringAdapter extends ArrayAdapter<String> {

    //private AppCompatActivity parentActivity;
    List<String> notesString;
    List<Note> notes;

    NoteStringAdapter(Context context, List<Note> notes) {
        super(context, R.layout.note_item);
        this.notes = notes;
        this.notesString = new ArrayList<>();
        for (int j = 0; j < notes.size(); j++) {
            notesString.add(notes.toString());
        }
        this.addAll(notesString);
        //this.parentActivity = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final String noteString = getItem(position);
        final Note note = notes.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
            convertView.setLongClickable(true);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(note.getText());
        //((TextView) convertView.findViewById(R.id.date_last_edited)).setText(note.getFormattedDate());
//        (convertView.findViewById(R.id.note_item)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                parentActivity.registerForContextMenu(view);
//                parentActivity.openContextMenu(view);
//            }
//        });


//        (convertView.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NoteAdapter adapter = (NoteAdapter) ((ListView)parent).getAdapter();
//                adapter.remove(note);
//                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
//        (convertView.findViewById(R.id.edit_button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), EditActivity.class);
//                intent.putExtra("NOTE", note.getText());
//                intent.putExtra("POSITION", position);
//                ((AppCompatActivity)getContext()).startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
//            }
//        });
        return  convertView;
    }

}
