package es.studium.pianimals.articulos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import es.studium.pianimals.R;

public class Art6 extends AppCompatActivity {

    Button btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art6);
        btnAtras = findViewById(R.id.btnAtras6);

        btnAtras.setOnClickListener(v -> {
            finish();
        });
    }
}