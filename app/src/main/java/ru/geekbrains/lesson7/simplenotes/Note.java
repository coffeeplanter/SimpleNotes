package ru.geekbrains.lesson7.simplenotes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class Note {

    private String note;
    private Calendar date_last_edited;

    Note(String note) {
        this.note = note;
        this.date_last_edited = Calendar.getInstance();
    }

    Note(String note, Calendar date) {
        this.note = note;
        this.date_last_edited = date;
    }

    void setText(String text) {
        this.note = text;
        this.date_last_edited = Calendar.getInstance();
    }

    String getText() {
        return this.note;
    }

    Calendar getDate() {
        return this.date_last_edited;
    }

    String getFormattedDate() {
        SimpleDateFormat formattedDate= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return "Last edited: " + formattedDate.format(date_last_edited.getTime());
    }

}
