package es.studium.pianimals.ui.contacto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.studium.pianimals.R;
import es.studium.pianimals.ui.pets.PetsViewModel;

public class ContactoFragment extends Fragment {

    private ContactoViewModel contactoViewModelViewModel;
    Button btnCNP;
    Button btnCLP;
    Button btnGC;
    Button btnBomb;
    Button btnEmer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contactoViewModelViewModel = new ViewModelProvider(this).get(ContactoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contacto, container, false);
        btnCNP = root.findViewById(R.id.btnCnp);
        btnCLP = root.findViewById(R.id.btnClp);
        btnGC = root.findViewById(R.id.btnGc);
        btnBomb = root.findViewById(R.id.btnBomb);
        btnEmer = root.findViewById(R.id.btnEmer);

        btnCNP.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Al pulsar llamarias a la Policia Nacional", Toast.LENGTH_SHORT).show();
        });
        btnCLP.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Al pulsar llamarias a la Policia Local", Toast.LENGTH_SHORT).show();
        });
        btnGC.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Al pulsar llamarias a la Guardia Civil", Toast.LENGTH_SHORT).show();
        });
        btnBomb.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Al pulsar llamarias a los Bomberos", Toast.LENGTH_SHORT).show();
        });
        btnEmer.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Al pulsar llamarias a Emergencias", Toast.LENGTH_SHORT).show();
        });
        return root;
    }
}