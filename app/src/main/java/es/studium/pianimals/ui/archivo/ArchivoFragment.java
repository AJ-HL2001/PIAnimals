package es.studium.pianimals.ui.archivo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import es.studium.pianimals.R;
import es.studium.pianimals.articulos.Art1;
import es.studium.pianimals.articulos.Art10;
import es.studium.pianimals.articulos.Art11;
import es.studium.pianimals.articulos.Art12;
import es.studium.pianimals.articulos.Art13;
import es.studium.pianimals.articulos.Art14;
import es.studium.pianimals.articulos.Art2;
import es.studium.pianimals.articulos.Art3;
import es.studium.pianimals.articulos.Art4;
import es.studium.pianimals.articulos.Art5;
import es.studium.pianimals.articulos.Art6;
import es.studium.pianimals.articulos.Art7;
import es.studium.pianimals.articulos.Art8;
import es.studium.pianimals.articulos.Art9;

public class ArchivoFragment extends Fragment {

    private ArchivoViewModel slideshowViewModel;
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(ArchivoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_archivo, container, false);
        listView = root.findViewById(R.id.listaArticulos);
        ArrayList<String> art = new ArrayList<>();
        art.add(getResources().getString(R.string.art1));
        art.add(getResources().getString(R.string.art2));
        art.add(getResources().getString(R.string.art3));
        art.add(getResources().getString(R.string.art4));
        art.add(getResources().getString(R.string.art5));
        art.add(getResources().getString(R.string.art6));
        art.add(getResources().getString(R.string.art7));
        art.add(getResources().getString(R.string.art8));
        art.add(getResources().getString(R.string.art9));
        art.add(getResources().getString(R.string.art10));
        art.add(getResources().getString(R.string.art11));
        art.add(getResources().getString(R.string.art12));
        art.add(getResources().getString(R.string.art13));
        art.add(getResources().getString(R.string.art14));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, art);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(position==0){
                Intent intent = new Intent(getActivity(), Art1.class);
                startActivity(intent);
            }
            if(position==1){
                Intent intent = new Intent(getActivity(), Art2.class);
                startActivity(intent);
            }
            if(position==2){
                Intent intent = new Intent(getActivity(), Art3.class);
                startActivity(intent);
            }
            if(position==3){
                Intent intent = new Intent(getActivity(), Art4.class);
                startActivity(intent);
            }
            if(position==4){
                Intent intent = new Intent(getActivity(), Art5.class);
                startActivity(intent);
            }
            if(position==5){
                Intent intent = new Intent(getActivity(), Art6.class);
                startActivity(intent);
            }
            if(position==6){
                Intent intent = new Intent(getActivity(), Art7.class);
                startActivity(intent);
            }
            if(position==7){
                Intent intent = new Intent(getActivity(), Art8.class);
                startActivity(intent);
            }
            if(position==8){
                Intent intent = new Intent(getActivity(), Art9.class);
                startActivity(intent);
            }
            if(position==9){
                Intent intent = new Intent(getActivity(), Art10.class);
                startActivity(intent);
            }
            if(position==10){
                Intent intent = new Intent(getActivity(), Art11.class);
                startActivity(intent);
            }
            if(position==11){
                Intent intent = new Intent(getActivity(), Art12.class);
                startActivity(intent);
            }
            if(position==12){
                Intent intent = new Intent(getActivity(), Art13.class);
                startActivity(intent);
            }
             if(position==13){
                Intent intent = new Intent(getActivity(), Art14.class);
                startActivity(intent);
            }

        });
        return root;
    }
}