package es.studium.pianimals.articulos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import es.studium.pianimals.R;

public class Art5 extends AppCompatActivity {

    Button btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art5);
        btnAtras = findViewById(R.id.btnAtras5);

        btnAtras.setOnClickListener(v -> {
            finish();
        });
    }
}