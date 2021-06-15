package es.studium.pianimals.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.Transliterator;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import es.studium.pianimals.R;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private static final String[] permiso = {Manifest.permission.ACCESS_FINE_LOCATION};
    private MapViewModel galleryViewModel;
    private final LatLng posicion = new LatLng(37.586661772466464, -5.871243985000119);
    private final LatLng vetVilla = new LatLng(37.591447454763696, -5.874939552753079);
    private final LatLng vetBrenes = new LatLng(37.615383328508464, -5.8265310471570135);
    private final LatLng vetCant = new LatLng(37.55420625365951, -5.869445802766616);
    GoogleMap mapa;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        ActivityCompat.requestPermissions(getActivity(), permiso, 1000);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation().addOnSuccessListener(location -> mapa.setMyLocationEnabled(true));
            return;
        }
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13));
        mapa.addMarker(new MarkerOptions().position(vetVilla).title("Clínica Veterinaria RIVERA"));
        mapa.addMarker(new MarkerOptions().position(vetBrenes).title("Clínica Veterinaria REYCAN"));
        mapa.addMarker(new MarkerOptions().position(vetCant).title("Clinica Veterinaria GODOVET"));
    }
}