package com.espparki.molisnail;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Toolbar toolbar;
    private ImageView profileImage;
    private TextView levelTextView;
    private ProgressBar levelProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializa el Toolbar y los elementos del layout
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileImage = findViewById(R.id.profileImage);
        levelTextView = findViewById(R.id.textRight);  // Texto del nivel
        levelProgressBar = findViewById(R.id.level_progress_bar); // Barra de progreso del nivel

        // Cargar los datos del usuario
        loadUserData();


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Cargar el fragmento inicial (HomeFragment) si no hay estado guardado
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.nav_catalogo) {
                        toolbar.setVisibility(Toolbar.VISIBLE);
                        selectedFragment = new CatalogoFragment();
                    } else if (item.getItemId() == R.id.nav_citas) {
                        toolbar.setVisibility(Toolbar.VISIBLE);
                        selectedFragment = new CitasFragment();
                    } else if (item.getItemId() == R.id.nav_home) {
                        toolbar.setVisibility(Toolbar.VISIBLE);
                        selectedFragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.nav_tienda) {
                        toolbar.setVisibility(Toolbar.VISIBLE);
                        selectedFragment = new TiendaFragment();
                    } else if (item.getItemId() == R.id.nav_perfil) {
                        toolbar.setVisibility(Toolbar.GONE);
                        selectedFragment = new PerfilFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }

            };

    // Función para cargar los datos del usuario desde Firestore
    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Cargar la imagen del perfil
                            String photoUrl = documentSnapshot.getString("photo");
                            if (photoUrl != null) {
                                Uri photoUri = Uri.parse(photoUrl);
                                Glide.with(this).load(photoUri).into(profileImage); // Cargar imagen con Glide
                            } else {
                                profileImage.setImageResource(R.drawable.garras_icon); // Imagen predeterminada
                            }

                            // Verificar el nivel del usuario
                            String nivel = documentSnapshot.getString("nivel");
                            if (nivel == null) {
                                nivel = "bronze"; // Nivel por defecto
                                db.collection("usuarios").document(user.getUid())
                                        .update("nivel", nivel);
                            }
                            levelTextView.setText("Nivel " + nivel); // Mostrar el nivel del usuario

                            // Verificar los puntos
                            Long puntosLong = documentSnapshot.getLong("puntos");
                            int puntos = (puntosLong != null) ? puntosLong.intValue() : 0;

                            // Configurar la barra de progreso según el nivel y puntos
                            setupProgressBar(nivel, puntos);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error al obtener los datos del usuario.", e);
                    });
        }
    }

    // Configura la barra de progreso según el nivel
    private void setupProgressBar(String nivel, int puntos) {
        if (nivel.equals("bronze")) {
            levelProgressBar.setMax(100);  // Puntos necesarios para pasar a silver
        } else if (nivel.equals("silver")) {
            levelProgressBar.setMax(500);  // Puntos necesarios para pasar a gold
        } else if (nivel.equals("gold")) {
            levelProgressBar.setMax(500);  // Máximo alcanzado
        }
        levelProgressBar.setProgress(puntos); // Configura la barra de progreso
    }
}
