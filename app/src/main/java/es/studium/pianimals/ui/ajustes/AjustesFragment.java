package es.studium.pianimals.ui.ajustes;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import es.studium.pianimals.MainActivity;
import es.studium.pianimals.R;
import es.studium.pianimals.splash_activity;

public class AjustesFragment extends Fragment {

    private AjustesViewModel ajustesViewModel;

    Button esBtn;
    Button enBtn;

    private Locale locale;
    private final Configuration config = new Configuration();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ajustesViewModel = new ViewModelProvider(this).get(AjustesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ajustes, container, false);
        esBtn = root.findViewById(R.id.btnEs);
        enBtn = root.findViewById(R.id.btnEn);
        esBtn.setOnClickListener(v -> {
            locale = new Locale("es");
            config.locale = locale;
            getResources().updateConfiguration(config, null);
            Intent refresh = new Intent(getActivity(), splash_activity.class);
            startActivity(refresh);
        });
        enBtn.setOnClickListener(v -> {
            locale = new Locale("en");
            config.locale = locale;
            getResources().updateConfiguration(config, null);
            Intent refresh = new Intent(getActivity(), splash_activity.class);
            startActivity(refresh);
        });
        return root;
    }
}