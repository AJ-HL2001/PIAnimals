package es.studium.pianimals;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

public class splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Orientación de la pantalla bloqueada en vertical

        //Thread que contendrá el código para cambiar de activity
        Thread myThread = new Thread(() -> {
            try {
                Thread.sleep(500); // Segundos que mostrara la pantalla splash
                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Intent con la activity que se iniciara
                startActivity(intent); // Iniciar el intent
                finish();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        myThread.start(); // Iniciar el Thread
    }
}