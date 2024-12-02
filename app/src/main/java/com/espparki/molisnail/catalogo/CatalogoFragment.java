package com.espparki.molisnail.catalogo;

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

import com.espparki.molisnail.R;
import com.espparki.molisnail.admin.catalogo.Design;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {
    private RecyclerView recyclerView;
    private NailAdapter adapter;
    private List<Design> designList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catalogo_fragment_catalogo, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_catalogo);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();

        adapter = new NailAdapter(designList);
        recyclerView.setAdapter(adapter);

        loadDesignsFromFirebase();
        return view;
    }

    private void loadDesignsFromFirebase() {
        db.collection("designs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    designList.clear();
                    Log.d("CatalogoFragment", "Documentos obtenidos: " + queryDocumentSnapshots.size());
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Design design = document.toObject(Design.class);
                        if (design != null) {
                            design.setId(document.getId());
                            Log.d("CatalogoFragment", "Diseño añadido: ID=" + design.getId() + ", Imagen Base64=" +
                                    (design.getImagenBase64() != null ? design.getImagenBase64().substring(0, Math.min(design.getImagenBase64().length(), 50)) : "NULO"));
                            designList.add(design);
                        } else {
                            Log.e("CatalogoFragment", "Error al mapear un documento");
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error al cargar los diseños", e));
    }
}
