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
        final int position;
        String noteText = getIntent().getStringExtra("NOTE");
        if (noteText != null) {
            editText.setText(noteText);
            position = getIntent().getIntExtra("POSITION", -1);
            isNew = false;
        }
        else {
            position = -1;
            isNew = true;
        }

        Button btnSave = (Button) findViewById(R.id.save_button);
        Button btnCancel = (Button) findViewById(R.id.cancel_button);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buffer = editText.getText().toString();
                if (buffer.length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("NOTE", buffer);
                    intent.putExtra("isNew", isNew);
                    intent.putExtra("POSITION", position);
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
