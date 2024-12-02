package com.espparki.molisnail.perfil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_activity_about);
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }
}
