package ru.solovyov.ilya.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Класс активности для редактирования заметки

public class EditActivity extends AppCompatActivity {

    public static final String TAG = "EditActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        final EditText editText = (EditText) findViewById(R.id.editText);
        final Boolean isNew;
        final int position = getIntent().getIntExtra(MainActivity.NOTE_POSITION_FLAG_INTENT, -1);
        String noteText = getIntent().getStringExtra(MainActivity.NOTE_FLAG_INTENT);
        if (position != -1) {
            isNew = false;
        }
        else {
            isNew = true;
        }
        if (noteText != null) {
            editText.setText(noteText);
        }

        Button btnSave = (Button) findViewById(R.id.save_button);
        Button btnCancel = (Button) findViewById(R.id.cancel_button);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buffer = editText.getText().toString();
                if (buffer.length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.NOTE_FLAG_INTENT, buffer);
                    intent.putExtra(MainActivity.NOTE_NEW_FLAG_INTENT, isNew);
                    intent.putExtra(MainActivity.NOTE_POSITION_FLAG_INTENT, position);
                    setResult(RESULT_OK, intent);
                }
                else {
                    Toast.makeText(EditActivity.this, R.string.no_text_was_entered_toast, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditActivity.this, R.string.edit_cancelled_toast, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
