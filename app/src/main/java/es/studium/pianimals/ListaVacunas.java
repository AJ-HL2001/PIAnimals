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

public class ListaVacunas extends AppCompatActivity {

    ListView listaVacunas;

    ArrayList<String> vacunas;
    String servidor="192.168.1.149";

    ConsultaRemota acceso;
    BajaRemota baja;
    AltaRemota alta;
    JSONArray result;
    JSONObject jsonobject;
    int posicion;
    ArrayAdapter<String> adapter;

    String idAnimalFK;

    FloatingActionButton btnNuevaVacuna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vacunas);
        listaVacunas = findViewById(R.id.listaVacunas);
        btnNuevaVacuna = findViewById(R.id.btnNuevaVacuna);

        vacunas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vacunas);
        listaVacunas.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idAnimalFK = bundle.getString("idAnimalFK");

        acceso = new ConsultaRemota(idAnimalFK);
        acceso.execute();

        listaVacunas.setOnItemLongClickListener((arg0, v, index, arg3) ->{
            AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(this);
            dialogoEliminar.setTitle("Eliminar Vacuna").setMessage("Se va a eliminar esta vacuna de la lista")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", ((dialog, which) -> {
                        String idVacuna[] = listaVacunas.getItemAtPosition(index).toString().split("-");
                        baja = new BajaRemota(idVacuna[0]); //Usamos el metodo de baja y lo ejecutamos
                        baja.execute();
                        acceso = new ConsultaRemota(idAnimalFK); //Volvemos a usar el metodo de consulta
                        acceso.execute();
                    }))
                    .setNegativeButton("Cancelar", ((dialog, which) -> {
                        dialog.cancel();
                    }));
            dialogoEliminar.show();
            return true;
        });

        btnNuevaVacuna.setOnClickListener(v -> {
            AlertDialog.Builder dialogoAgregarNota = new AlertDialog.Builder(this);
            //Indicamos un titulo y un mensaje
            dialogoAgregarNota.setTitle("Vacuna");  
            dialogoAgregarNota.setMessage("Datos de la Vacuna");
            EditText fecha = new EditText(this); //Iniciamos un EditText donde ira la palabra en castellano
            fecha.setHint("Fecha");
            EditText vacuna = new EditText(this); //Iniciamos un EditText donde ira la traduccion
            vacuna.setHint("vacuna");
            LinearLayout layout = new LinearLayout(this); //Inicializamos el layout del dialogo
            layout.addView(fecha);
            layout.addView(vacuna);
            dialogoAgregarNota.setView(layout);

            //Si pulsamos en confirmar
            dialogoAgregarNota.setPositiveButton("Confirmar", (dialog, whichButton) -> {
                alta = new AltaRemota(fecha.getText().toString(), vacuna.getText().toString(), idAnimalFK); //Usamos el metodo para hacer un alta de traduccion y lo ejecutamos
                alta.execute();
                acceso = new ConsultaRemota(idAnimalFK); //Volvemos a usar el metodo de consulta
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
                URL url = new  URL("http://" + servidor + "/ApiMascotas/vacunas.php?idAnimalFK="+idAnimalFK);
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
                vacunas.clear(); //Eliminamos los datos del ArrayList
                //Si el result no esta vacio
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        jsonobject = result.getJSONObject(i); //Introducimos en el jsonobject el resultado del result en i
                        //Introducimos en el arrayList el id de la traduccion y la palabra en los dos idiomas
                        vacunas.add(jsonobject.getString("idVacuna")+" - "+jsonobject.getString("nombreVacuna")+" ("+jsonobject.getString("fechaVacuna")+")");
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
        String idVacuna;
        public BajaRemota(String id){this.idVacuna = id;}

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Crear la URL de conexión al API
                URI baseUri = new URI("http://"+servidor+"/ApiMascotas/vacunas.php");
                String[] parametros = {"id", this.idVacuna}; //Array con los parametros
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
        String fecha, vacuna, idMascotaFK;

        public AltaRemota(String fecha, String texto, String idMascotaFK) {
            this.fecha = fecha;
            this.vacuna = texto;
            this.idMascotaFK = idMascotaFK;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://"+servidor+"/ApiRest/vacunas.php");
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("POST"); //Indicamos que haremos un post
                HashMap<String, String> postDataParams = new HashMap<>();
                //Usando postData indicamos el nombre del campo de la BD y el String con la informacion
                postDataParams.put("fechaVacuna", this.fecha);
                postDataParams.put("nombreVacuna", this.vacuna);
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




