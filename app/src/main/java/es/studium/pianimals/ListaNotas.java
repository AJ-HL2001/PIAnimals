package es.studium.pianimals;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.studium.pianimals.ui.pets.PetsFragment;

public class ListaNotas extends AppCompatActivity {

    ListView listaNotas;

    ArrayList<String> notas;
    String servidor="192.168.1.149";

    ConsultaRemota acceso;
    BajaRemota baja;
    AltaRemota alta;
    JSONArray result;
    JSONObject jsonobject;
    int posicion;
    ArrayAdapter<String> adapter;

    String nota;
    String idAnimal;

    FloatingActionButton btnNuevaNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        btnNuevaNota = findViewById(R.id.btnNuevaNota);

        listaNotas = findViewById(R.id.listaNotas);
        notas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notas);
        listaNotas.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nota = bundle.getString("notas");


         acceso = new ConsultaRemota(nota);
         acceso.execute();

         listaNotas.setOnItemLongClickListener((arg0, v, index, arg3) ->{
             AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(this);
             dialogoEliminar.setTitle("Eliminar Notas").setMessage("Se va a eliminar esta nota de la lista")
                     .setCancelable(false)
                     .setPositiveButton("Aceptar", ((dialog, which) -> {
                         String idNota[] = listaNotas.getItemAtPosition(index).toString().split("-");
                         baja = new BajaRemota(idNota[0]); //Usamos el metodo de baja y lo ejecutamos
                         baja.execute();
                         acceso = new ConsultaRemota(nota); //Volvemos a usar el metodo de consulta
                         acceso.execute();
                     }))
                     .setNegativeButton("Cancelar", ((dialog, which) -> {
                         dialog.cancel();
                     }));
             dialogoEliminar.show();
             return true;
         });

         listaNotas.setOnItemClickListener((arg0, v, index, arg3) ->{
             Intent intent1 = new Intent(this, DetallesNota.class); //Creamos el intent
             intent1.putExtra("notaPulsada", notas.get(index)); //Añadimos el item a un bundle
             startActivity(intent1); //Iniciamos la activity de las traducciones
         });

         btnNuevaNota.setOnClickListener(v -> {
             AlertDialog.Builder dialogoAgregarNota = new AlertDialog.Builder(this);
             //Indicamos un titulo y un mensaje
             dialogoAgregarNota.setTitle("Nota");
             dialogoAgregarNota.setMessage("Información de la mascota");
             EditText fecha = new EditText(this); //Iniciamos un EditText donde ira la palabra en castellano
             fecha.setHint("Fecha");
             EditText texto = new EditText(this); //Iniciamos un EditText donde ira la traduccion
             texto.setHint("Texto");
             LinearLayout layout = new LinearLayout(this); //Inicializamos el layout del dialogo
             layout.addView(fecha);
             layout.addView(texto);
             dialogoAgregarNota.setView(layout);

             //Si pulsamos en confirmar
             dialogoAgregarNota.setPositiveButton("Confirmar", (dialog, whichButton) -> {
                 alta = new AltaRemota(fecha.getText().toString(), texto.getText().toString(), nota); //Usamos el metodo para hacer un alta de traduccion y lo ejecutamos
                 alta.execute();
                 acceso = new ConsultaRemota(nota); //Volvemos a usar el metodo de consulta
                 acceso.execute();
             });
             //Si pulsamos el boton cancelar
             dialogoAgregarNota.setNegativeButton("Cancelar", (dialog, whichButton) -> dialog.cancel());
             dialogoAgregarNota.show(); //Mostramos el dialogo
         });
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        String idAnimalFK;

        public ConsultaRemota(String idFK)
        {
            this.idAnimalFK = idFK;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new  URL("http://" + servidor + "/ApiMascotas/notas.php?idAnimalFK="+idAnimalFK);
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
                notas.clear(); //Eliminamos los datos del ArrayList
                //Si el result no esta vacio
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        jsonobject = result.getJSONObject(i); //Introducimos en el jsonobject el resultado del result en i
                        //Introducimos en el arrayList el id de la traduccion y la palabra en los dos idiomas
                        notas.add(jsonobject.getString("idNota")+" - "+jsonobject.getString("fechaNota"));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String> {
        String idNota;
        public BajaRemota(String id){this.idNota = id;}

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Crear la URL de conexión al API
                URI baseUri = new URI("http://"+servidor+"/ApiMascotas/notas.php");
                String[] parametros = {"id", this.idNota}; //Array con los parametros
                URI uri = applyParameters(baseUri, parametros); //Añadimos la conexion y los parametros
                HttpURLConnection myConnection = (HttpURLConnection) uri.toURL().openConnection(); //Abrimos la conexion
                myConnection.setRequestMethod("DELETE"); //Indicamos que vamos a hacer un delete
                if (myConnection.getResponseCode() == 200) {
                    Log.println(Log.ASSERT, "Resultado", "Nota eliminada");
                    myConnection.disconnect();
                }
                else {
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            }
            catch (Exception e) {
                Log.println(Log.ASSERT, "Excepción", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String mensaje) {}
        URI applyParameters(URI uri, String[] urlParameters) {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for(int i = 0; i < urlParameters.length; i+= 2) {
                if (first) {
                    first = false;
                }
                else {
                    query.append("&");
                }
                try {
                    query.append(urlParameters[i]).append("=").append(URLEncoder.encode(urlParameters[i + 1], "UTF-8"));
                }
                catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query.toString(), null);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class AltaRemota extends AsyncTask<Void, Void, String> {
        String fecha, texto, idMascotaFK;

        public AltaRemota(String fecha, String texto, String idMascotaFK) {
            this.fecha = fecha;
            this.texto = texto;
            this.idMascotaFK = idMascotaFK;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://"+servidor+"/ApiRest/notas.php");
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("POST"); //Indicamos que haremos un post
                HashMap<String, String> postDataParams = new HashMap<>();
                //Usando postData indicamos el nombre del campo de la BD y el String con la informacion
                postDataParams.put("fechaNota", this.fecha);
                postDataParams.put("textoNota", this.texto);
                postDataParams.put("idAnimalFK", this.idMascotaFK);
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
                if (myConnection.getResponseCode() == 200) {
                    // Success
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
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
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



