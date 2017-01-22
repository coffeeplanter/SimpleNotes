package ru.geekbrains.lesson7.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

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

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("NOTE", editText.getText().toString());
                intent.putExtra("isNew", isNew);
                intent.putExtra("POSITION", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}