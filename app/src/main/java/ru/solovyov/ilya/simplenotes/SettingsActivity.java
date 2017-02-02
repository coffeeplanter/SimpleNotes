package ru.solovyov.ilya.simplenotes;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Отображение фрагмента с настройками
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            // Загрузка настроек из XML-ресурса
            addPreferencesFromResource(R.xml.preferences);

        }
    }

}
