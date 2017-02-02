package ru.solovyov.ilya.simplenotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        TextView email = (TextView) findViewById(R.id.textView);
        email.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
