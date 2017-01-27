package ru.solovyov.ilya.simplenotes;

import android.app.Application;

// Класс приложения для предоставления ресурсов

public class SimpleNotesApplication extends Application {

    private static String resourceStringNoteDateLastEdited;
    private static String resourceStringNotesSelectedNumber;

    @Override
    public void onCreate() {
        super.onCreate();
        resourceStringNoteDateLastEdited = getResources().getString(R.string.note_date_last_edited);
        resourceStringNotesSelectedNumber = getResources().getString(R.string.notes_selected_number);
    }

    public static String getResourceStringNoteDateLastEdited() {
        return resourceStringNoteDateLastEdited;
    }

    public static String getResourceStringNotesSelectedNumber() {
        return resourceStringNotesSelectedNumber;
    }

}
