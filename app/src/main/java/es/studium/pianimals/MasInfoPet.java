package es.studium.pianimals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MasInfoPet extends AppCompatActivity {

    TextView nombre;
    TextView nacimiento;
    TextView raza;
    TextView tipo;
    TextView genero;

    ConsultaRemota acceso;

    JSONObject result;
    JSONObject jsonobject;
    int posicion;

    String animal;
    String[] idAnimal;

    String servidor="192.168.1.149";

    Button btnNotas;
    Button btnVacunas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas_info_pet);

        nombre = findViewById(R.id.txtDatoNombre);
        nacimiento = findViewById(R.id.txtDatoNacimiento);
        raza = findViewById(R.id.txtDatoRaza);
        tipo = findViewById(R.id.txtDatoAnimal);
        genero = findViewById(R.id.txtDatoGenero);

        btnNotas = findViewById(R.id.btnNotas);
        btnVacunas = findViewById(R.id.btnVacunas);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        animal = bundle.getString("mascota");
        idAnimal = animal.split("  -  ");

        acceso = new ConsultaRemota(idAnimal[0]);
        acceso.execute();

        btnNotas.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ListaNotas.class); //Creamos el intent
            intent1.putExtra("notas", idAnimal[0]); //Añadimos el item a un bundle
            startActivity(intent1);
        });

        btnVacunas.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, ListaVacunas.class); //Creamos el intent
            intent2.putExtra("idAnimalFK", idAnimal[0]); //Añadimos el item a un bundle
            startActivity(intent2);
        });
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        String idAnimal;

        public ConsultaRemota(String id)
        {
            this.idAnimal = id;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new  URL("http://" + servidor + "/ApiMascotas/animales.php?idAnimal="+idAnimal);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("GET"); //Indicamos que haremos un get
                if (myConnection.getResponseCode() == 200) {
                    //Creamos imputs
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                    //Creamos un buffer
                    BufferedReader bR = new BufferedReader(responseBodyReader);
                    String line;
                    StringBuilder responseStrBuilder = new StringBuilder();
                    while ((line = bR.readLine()) != null) {
                        responseStrBuilder.append(line);
                    }
                    result = new JSONObject(responseStrBuilder.toString()); //Iniciamos un jsonArray
                    posicion = 0; //Introducimos en el jsonobject el resultado del result en posicion
                    //Cerramos todas las conexiones
                    responseBody.close();
                    responseBodyReader.close();
                    myConnection.disconnect();
                }
                else {
                    Log.println(Log.ERROR, "Error", "¡Conexión fallida");
                }
            }
            catch (Exception e) {
                Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
                Log.println(Log.ERROR, "Error", e.getMessage());
            }
            return (null);
        }

        protected void onPostExecute(String mensaje) {
            try {
                //Si el result no esta vacio
                if (result != null) {
                    nombre.setText(result.getString("nombreAnimal"));
                    nacimiento.setText(result.getString("fechaNacimientoAnimal"));
                    raza.setText(result.getString("razaAnimal"));
                    tipo.setText(result.getString("tipoAnimal"));
                    genero.setText(result.getString("generoAnimal"));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}