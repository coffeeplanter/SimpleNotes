package ru.solovyov.ilya.simplenotes;

import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class Note {

    public static final String TAG = "Note";
    private String note;
    private Calendar date_created;
    private Calendar date_last_edited;

    Note(String note) {
        this.note = note;
        this.date_last_edited = this.date_created = Calendar.getInstance();
    }

    Note(String note, Calendar date) {
        this.note = note;
        this.date_last_edited = this.date_created = date;
    }

    void setText(String text) {
        this.note = text;
        this.date_last_edited = Calendar.getInstance();
    }

    void addText(String text) {
        if (this.note != null) {
            this.note = this.note + text;
            this.date_last_edited = Calendar.getInstance();
        }
        else {
            this.note = text;
        }
    }

    String getText() {
        return this.note;
    }

    Calendar getCreatedDate() {
        return this.date_created;
    }

    Calendar getLastEditedDate() {
        return this.date_last_edited;
    }

    String getLastEditedFormattedDate() {
        SimpleDateFormat formattedDate= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return SimpleNotesApplication.getResourceStringNoteDateLastEdited() + formattedDate.format(date_last_edited.getTime());
    }

    String getCreatedFormattedDate() {
        SimpleDateFormat formattedDate= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return SimpleNotesApplication.getResourceStringNoteDateCreated() + formattedDate.format(date_created.getTime());
    }

}
