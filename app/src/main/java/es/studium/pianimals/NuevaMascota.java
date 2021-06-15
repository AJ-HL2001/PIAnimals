package es.studium.pianimals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.studium.pianimals.ui.pets.PetsFragment;

public class NuevaMascota extends AppCompatActivity {

    Button btnNuevo;
    Button btnAtras;

    String servidor="192.168.1.149";
    AltaRemota alta;

    EditText nombre;
    EditText fecha;
    EditText genero;
    EditText tipo;
    EditText raza;

    ListView listaAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_mascota);

        btnAtras = findViewById(R.id.btnCancelar);
        btnNuevo = findViewById(R.id.btnNuevaMascota);

        listaAnimal = findViewById(R.id.listaAnimales);

        nombre = findViewById(R.id.insertNombre);
        fecha = findViewById(R.id.insertNacimiento);
        genero = findViewById(R.id.insertGenero);
        tipo = findViewById(R.id.insertTipo);
        raza = findViewById(R.id.insertRaza);

        btnAtras.setOnClickListener(v -> {
            finish();
        });

        btnNuevo.setOnClickListener(v -> {
            alta = new AltaRemota(nombre.getText().toString(), fecha.getText().toString(), genero.getText().toString(), tipo.getText().toString(), raza.getText().toString());
            alta.execute();
            Toast.makeText(this, "Animal Guardado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private class AltaRemota extends AsyncTask<Void, Void, String> {

        String nombreAnimal;
        String fechaNacimientoAnimal;
        String generoAnimal;
        String tipoAnimal;
        String razaAnimal;

        public AltaRemota (String nombre, String fecha, String genero, String tipo, String raza) {
            this.nombreAnimal = nombre;
            this.fechaNacimientoAnimal = fecha;
            this.generoAnimal = genero;
            this.tipoAnimal = tipo;
            this.razaAnimal = raza;

        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://"+servidor+"/ApiMascotas/animales.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("POST"); //Indicamos que haremos un post
                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("nombreAnimal", this.nombreAnimal);
                postDataParams.put("fechaNacimientoAnimal", this.fechaNacimientoAnimal);
                postDataParams.put("generoAnimal", this.generoAnimal);
                postDataParams.put("tipoAnimal", this.tipoAnimal);
                postDataParams.put("razaAnimal", this.razaAnimal);//Usando postData indicamos el nombre del campo de la BD y el String con la informacion
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                //Creamos un OutPut
                OutputStream os = myConnection.getOutputStream();
                //Iniciamos un BufferedWriter
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams)); //Usando el buffered le pasamos los datos del PostData
                //Cerramos todas las conexiones
                writer.flush();
                writer.close();
                os.close();
                myConnection.getResponseCode();
                if(myConnection.getResponseCode() == 200) {
                    myConnection.disconnect();
                }
                else {
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            }
            catch (Exception e) {
                Log.println(Log.ASSERT, "Excepción", e.getMessage());
            }
            return (null);
        }
        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()) {
                if(first) {
                    first = false;
                }
                else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}