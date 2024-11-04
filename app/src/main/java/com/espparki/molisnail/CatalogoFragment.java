package com.espparki.molisnail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CatalogoAdapter adapter;
    private List<Nail> catalogoList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_catalogo);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();

        // Configura el adaptador y establece el RecyclerView
        adapter = new CatalogoAdapter(catalogoList, product -> {
            // Lógica para abrir la nueva Activity de inspiración
            Intent intent = new Intent(getActivity(), InspiracionActivity.class);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadNailsFromFirebase();  // Cargar productos (método para cargar datos de Firebase)
        return view;
    }


    private void loadNailsFromFirebase() {
        db.collection("nails").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Nail nail = document.toObject(Nail.class);  // Asegúrate de tener una clase modelo Nail
                catalogoList.add(nail);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error al cargar las uñas", e);
        });
    }
}
