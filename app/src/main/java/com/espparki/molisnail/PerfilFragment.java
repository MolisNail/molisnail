package com.espparki.molisnail;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class PerfilFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emailTextView, levelTextView, pointsTextView, userNameTextView, userPhoneTextView;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<String> optionsList = Arrays.asList("Cuenta y configuración", "Reseñas", "Sobre Moli’s Nail", "Cerrar sesión");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

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

    // Función para cargar los datos del usuario desde Firestore
    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener y mostrar el correo
                            String email = documentSnapshot.getString("email");
                            emailTextView.setText("Correo: "+ email);

                            // Obtener y mostrar el nombre
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                userNameTextView.setText(name);
                            }

                            // Obtener y mostrar la foto de perfil (si está disponible)
                            String photoUrl = documentSnapshot.getString("photo");
                            if (photoUrl != null) {
                                Uri photoUri = Uri.parse(photoUrl);
                                Glide.with(this).load(photoUri).into(profileImageView); // Usando Glide para cargar la imagen
                            }

                            // Verificar y asignar nivel y puntos por defecto
                            String nivel = documentSnapshot.getString("nivel");
                            if (nivel == null) {
                                nivel = "Nivel: bronze"; // Valor por defecto
                                db.collection("usuarios").document(user.getUid())
                                        .update("nivel", nivel); // Guarda el valor por defecto en Firestore
                            }

                            // Verificar si el campo "puntos" existe y asignar un valor por defecto si es null
                            Long puntosLong = documentSnapshot.getLong("puntos");
                            int puntos = (puntosLong != null) ? puntosLong.intValue() : 0;

                            // Mostrar datos en la interfaz
                            levelTextView.setText(nivel);
                            pointsTextView.setText(String.valueOf(puntos));

                            // Configurar la barra de progreso
                            setupProgressBar(nivel, puntos);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error al obtener los datos del usuario.", e));
        }
    }

    // Configurar la barra de progreso según el nivel
    private void setupProgressBar(String nivel, int puntos) {
        if (nivel.equals("bronze")) {
            progressBar.setMax(100);  // Puntos necesarios para pasar a silver
            progressBar.setProgress(puntos);
        } else if (nivel.equals("silver")) {
            progressBar.setMax(500);  // Puntos necesarios para pasar a gold
            progressBar.setProgress(puntos);
        } else if (nivel.equals("gold")) {
            progressBar.setMax(500);  // Máximo alcanzado
            progressBar.setProgress(puntos);
        }
    }

    // Función para manejar los clics en las opciones del perfil
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
                getActivity().finish();
                break;
        }
    }
}
