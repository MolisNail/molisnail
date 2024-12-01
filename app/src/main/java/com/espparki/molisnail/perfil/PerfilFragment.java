package com.espparki.molisnail.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;
import com.espparki.molisnail.login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class PerfilFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emailTextView, levelTextView, pointsTextView, userNameTextView;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<String> optionsList = Arrays.asList("Cuenta y configuración", "Reseñas", "Sobre Moli’s Nail", "Cerrar sesión");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perfil_fragment_perfil, container, false);

        // Inicializar Firestore y Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la interfaz
        emailTextView = view.findViewById(R.id.emailTextView);
        levelTextView = view.findViewById(R.id.levelTextView);
        pointsTextView = view.findViewById(R.id.pointsTextView);
        userNameTextView = view.findViewById(R.id.user_name);
        profileImageView = view.findViewById(R.id.profile_image);
        progressBar = view.findViewById(R.id.progressBar);

        // Cargar los datos del usuario
        loadUserData();

        // Inicializar RecyclerView para las opciones del perfil
        recyclerView = view.findViewById(R.id.profile_options_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProfileOptionsAdapter(optionsList, this::onOptionClick));

        return view;
    }

    private void loadUserData() {
        if (auth.getCurrentUser() == null) return;

        db.collection("usuarios").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded() || getContext() == null) {
                        Log.w("PerfilFragment", "Fragment no está adjunto.");
                        return;
                    }

                    if (documentSnapshot.exists()) {
                        // Obtener y mostrar el correo
                        String email = documentSnapshot.getString("email");
                        emailTextView.setText("Correo: " + email);

                        // Obtener y mostrar el nombre
                        String name = documentSnapshot.getString("name");
                        if (name != null) {
                            userNameTextView.setText(name);
                        }

                        // Obtener y mostrar la foto de perfil
                        String photoUrl = documentSnapshot.getString("photo");
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Uri photoUri = Uri.parse(photoUrl);
                            Glide.with(this).load(photoUri).into(profileImageView);
                        }

                        // Obtener y mostrar los puntos
                        Long puntosLong = documentSnapshot.getLong("puntos");
                        int puntos = (puntosLong != null) ? puntosLong.intValue() : 0;

                        // Calcular el nivel basado en los puntos
                        String nivel = calcularNivel(puntos);

                        // Actualizar los datos del perfil
                        levelTextView.setText("Nivel: " + nivel);
                        pointsTextView.setText("Puntos: " + puntos);

                        // Configurar la barra de progreso
                        setupProgressBar(nivel, puntos);

                        // Actualizar el color de la tarjeta del nivel
                        updateLevelCardColor(nivel);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error al obtener los datos del usuario.", e));
    }

    private String calcularNivel(int puntos) {
        if (puntos >= 300) {
            return "gold";
        } else if (puntos >= 100) {
            return "silver";
        } else {
            return "bronze";
        }
    }

    private void setupProgressBar(String nivel, int puntos) {
        if (nivel.equals("bronze")) {
            progressBar.setMax(100);
            progressBar.setProgress(puntos);
        } else if (nivel.equals("silver")) {
            progressBar.setMax(200);
            progressBar.setProgress(puntos - 100);
        } else if (nivel.equals("gold")) {
            progressBar.setMax(500);
            progressBar.setProgress(puntos - 300);
        }
    }

    private void updateLevelCardColor(String nivel) {
        View levelCard = getView().findViewById(R.id.level_card);
        if (levelCard != null) {
            int colorRes;
            switch (nivel) {
                case "gold":
                    colorRes = R.color.gold;
                    break;
                case "silver":
                    colorRes = R.color.silver;
                    break;
                default:
                    colorRes = R.color.bronze;
            }
            levelCard.setBackgroundColor(getResources().getColor(colorRes, null));
        }
    }

    private void onOptionClick(int position) {
        switch (position) {
            case 0: // Cuenta y configuración
                startActivity(new Intent(getActivity(), ConfigurationActivity.class));
                break;
            case 1: // Reseñas
                startActivity(new Intent(getActivity(), ReviewActivity.class));
                break;
            case 2: // Sobre Moli’s Nail
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case 3: // Cerrar sesión
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LogInActivity.class));
                break;
        }
    }
}
