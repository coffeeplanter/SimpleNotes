package ru.geekbrains.lesson7.simplenotes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class NoteAdapter extends ArrayAdapter<Note> {

    private ListView listView;
    private int resourceId;

    NoteAdapter(Context context, int resourceId, List<Note> notes, ListView listView) {
        super(context, resourceId, notes);
        this.listView = listView;
        this.resourceId = resourceId;
    }

    static class ViewHolder {
        TextView note;
        TextView date_last_edited;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);

        //Небольшая оптимизация, которая позволяет повторно использовать объекты
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.note = (TextView) convertView.findViewById(R.id.note);
            viewHolder.date_last_edited = (TextView) convertView.findViewById(R.id.date_last_edited);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.note.setText(note.getText());
        viewHolder.date_last_edited.setText(note.getFormattedDate());

        return convertView;
    }

//    private AppCompatActivity parentActivity;
//
//    NoteAdapter(Context context, List<Note> notes) {
//        super(context, R.layout.note_item, notes);
//        this.parentActivity = (AppCompatActivity) context;
//    }
//
//    @NonNull
//    @Override
//    public View getView(final int position, View convertView, final ViewGroup parent) {
//        final Note note = getItem(position);
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_item, parent, false);
//            convertView.setLongClickable(true);
//        }
//        ((TextView) convertView.findViewById(R.id.note)).setText(note.getText());
//        ((TextView) convertView.findViewById(R.id.date_last_edited)).setText(note.getFormattedDate());
////        (convertView.findViewById(R.id.note_item)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                parentActivity.registerForContextMenu(view);
////                parentActivity.openContextMenu(view);
////            }
////        });
//
//
////        (convertView.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                NoteAdapter adapter = (NoteAdapter) ((ListView)parent).getAdapter();
////                adapter.remove(note);
////                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
////            }
////        });
////        (convertView.findViewById(R.id.edit_button)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(getContext(), EditActivity.class);
////                intent.putExtra("NOTE", note.getText());
////                intent.putExtra("POSITION", position);
////                ((AppCompatActivity)getContext()).startActivityForResult(intent, MainActivity.REQUEST_CODE_NOTE);
////            }
////        });
//        return  convertView;
//    }
}
