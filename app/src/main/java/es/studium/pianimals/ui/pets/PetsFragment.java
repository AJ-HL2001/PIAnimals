package es.studium.pianimals.ui.pets;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import es.studium.pianimals.MasInfoPet;
import es.studium.pianimals.NuevaMascota;
import es.studium.pianimals.R;

public class PetsFragment extends Fragment {

    private PetsViewModel homeViewModel;

    FloatingActionButton nuevoAnimal;

    ListView listaMascotas;
    ArrayList<String> mascotas;
    String idMascota, nombreMascota;
    String servidor="192.168.1.149";

    ConsultaRemota acceso;
    BajaRemota baja;
    JSONArray result;
    JSONObject jsonobject;
    int posicion;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(PetsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pets, container, false);
        listaMascotas = root.findViewById(R.id.listaAnimales);
        nuevoAnimal = root.findViewById(R.id.addPetBtn);

        mascotas = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mascotas);

        listaMascotas.setAdapter(adapter);

        acceso = new ConsultaRemota();
        acceso.execute();

        listaMascotas.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(getActivity());
            dialogoEliminar.setTitle("Eliminar Mascota").setMessage("Se va a eliminar esta mascota de la lista")
                           .setCancelable(false)
                           .setPositiveButton("Aceptar", ((dialog, which) -> {
                               String[] idAnimal = listaMascotas.getItemAtPosition(index).toString().split("  -  ");
                               baja = new BajaRemota(idAnimal[0]); //Usamos el metodo de baja y lo ejecutamos
                               baja.execute();
                               acceso = new ConsultaRemota(); //Volvemos a usar el metodo de consulta
                               acceso.execute();
                           }))
                            .setNegativeButton("Cancelar", ((dialog, which) -> {
                                dialog.cancel();
                            }));
            dialogoEliminar.show();
            return true;
        });

        listaMascotas.setOnItemClickListener((arg0, v, index, arg3) -> {
            Intent intent = new Intent(root.getContext(), MasInfoPet.class); //Creamos el intent
            intent.putExtra("mascota", mascotas.get(index)); //Añadimos el item a un bundle
            startActivity(intent); //Iniciamos la activity de las traducciones
        });

        nuevoAnimal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevaMascota.class);
            startActivity(intent);
        });

        return root;
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... arguments) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://"+servidor+"/ApiMascotas/animales.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("GET"); //Indicamos que haremos un get
                if (myConnection.getResponseCode() == 200) {
                    //Creamos imputs
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    //Creamos un buffer
                    BufferedReader bR = new BufferedReader(responseBodyReader);
                    String line = "";
                    StringBuilder strBuilder = new StringBuilder();
                    while ((line = bR.readLine()) != null) {
                        strBuilder.append(line);
                    }
                    result = new JSONArray(strBuilder.toString()); //Iniciamos un jsonArray
                    posicion = 0;
                    jsonobject = result.getJSONObject(posicion); //Introducimos en el jsonobject el resultado del result en posicion
                    //Introducimos el resultado del jsonobject en variables
                    idMascota = jsonobject.getString("idAnimal");
                    nombreMascota = jsonobject.getString("nombreAnimal");
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
                Log.println(Log.ERROR, "Error", "¡Conexión fallida");
                Log.println(Log.ERROR, "Error", e.getMessage());
            }
            return (null);
        }

        protected void onPostExecute(String mensaje) {
            try {
                mascotas.clear(); //Eliminamos los datos del ArrayList
                //Si el result no esta vacio
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        jsonobject = result.getJSONObject(i); //Introducimos en el jsonobject el resultado del result en i
                        //Introducimos en el arrayList el id del idioma y su nombre
                        mascotas.add(jsonobject.getString("idAnimal") + "  -  " + jsonobject.getString("nombreAnimal") + " ("+jsonobject.getString("tipoAnimal")+")");
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo para la baja de idiomas
    private class BajaRemota extends AsyncTask<Void, Void, String> {
        String idMascota;
        public BajaRemota(String id){this.idMascota = id;}

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Crear la URL de conexión al API
                URI baseUri = new URI("http://"+servidor+"/ApiMascotas/animales.php");
                String[] parametros = {"id", this.idMascota}; //Array con los parametros
                URI uri = applyParameters(baseUri, parametros); //Añadimos la conexion y los parametros
                HttpURLConnection myConnection = (HttpURLConnection) uri.toURL().openConnection(); //Abrimos la conexion
                myConnection.setRequestMethod("DELETE"); //Indicamos que vamos a hacer un delete
                if (myConnection.getResponseCode() == 200) {
                    Log.println(Log.ASSERT, "Resultado", "Animal eliminado");
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
}



