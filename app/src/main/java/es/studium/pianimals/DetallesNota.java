package es.studium.pianimals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class DetallesNota extends AppCompatActivity {

    Button btnAtras;
    TextView txtTextoNota;
    TextView txtTitulo;

    String idNota;
    String servidor="192.168.1.149";

    ConsultaRemota acceso;
    JSONArray result;
    JSONObject jsonobject;
    int posicion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_nota);
        btnAtras = findViewById(R.id.btnAtrasNotas);
        txtTextoNota = findViewById(R.id.txtTextoNota);
        txtTitulo = findViewById(R.id.txtNotaTitle);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idNota = bundle.getString("notaPulsada");

        acceso = new ConsultaRemota(idNota);
        acceso.execute();

        btnAtras.setOnClickListener(v -> {
            finish();
        });
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        String idNota;

        public ConsultaRemota(String idFK)
        {
            this.idNota = idFK;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new  URL("http://" + servidor + "/ApiMascotas/notas.php?idNota="+idNota);
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
                    result = new JSONArray(responseStrBuilder.toString()); //Iniciamos un jsonArray
                    posicion = 0;
                    jsonobject = result.getJSONObject(posicion); //Introducimos en el jsonobject el resultado del result en posicion
                    //Cerramos todas las conexiones
                    responseBody.close();
                    responseBodyReader.close();
                    myConnection.disconnect();
                }
                else {
                    Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
                }
            }
            catch (Exception e) {
                Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
            }
            return (null);
        }

        protected void onPostExecute(String mensaje) {
            try {
                //Si el result no esta vacio
                if (result != null) {
                    txtTextoNota.setText(jsonobject.getString("textoNota"));
                    txtTitulo.setText(txtTitulo.getText()+" - "+jsonobject.getString("fechaNota"));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}