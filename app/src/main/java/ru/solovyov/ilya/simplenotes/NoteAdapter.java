package ru.solovyov.ilya.simplenotes;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Класс адаптера для списка заметок

class NoteAdapter extends ArrayAdapter<Note> {

    public static final String TAG = "NoteAdapter";

    private int resourceId;

    NoteAdapter(Context context, int resourceId, List<Note> notes) {
        super(context, resourceId, notes);
        this.resourceId = resourceId;
    }

    NoteAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.resourceId = resourceId;
    }

    private static class ViewHolder {
        TextView note_text;
        TextView date;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        Note note = getItem(position);

        // Оптимизация, позволяющая повторно использовать объекты
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.note_text = (TextView) convertView.findViewById(R.id.note);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date_last_edited);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        assert note != null;
        viewHolder.note_text.setText(note.getText());
        setTextAppearance(parent.getContext(), viewHolder.note_text, MainActivity.noteTextAppearance);
        if (MainActivity.isEditedDate) {
            viewHolder.date.setText(note.getLastEditedFormattedDate());
        }
        else {
            viewHolder.date.setText(note.getCreatedFormattedDate());
        }

        return convertView;
    }

    @SuppressWarnings("deprecation")
    private void setTextAppearance(Context context, TextView textView, int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, resId);
        }
        else {
            textView.setTextAppearance(resId);
        }
    }

}
