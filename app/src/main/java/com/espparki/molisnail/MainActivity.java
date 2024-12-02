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
import com.espparki.molisnail.catalogo.CatalogoFragment;
import com.espparki.molisnail.citas.CitasFragment;
import com.espparki.molisnail.perfil.PerfilFragment;
import com.espparki.molisnail.tienda.TiendaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements OnDataUpdateListener {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Toolbar toolbar;
    private ImageView profileImage;
    private TextView levelTextView;
    private ProgressBar levelProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_main);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileImage = findViewById(R.id.profileImage);
        levelTextView = findViewById(R.id.textRight);
        levelProgressBar = findViewById(R.id.level_progress_bar);
        loadUserData();


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
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

    @Override
    public void updateUserData() {
        loadUserData();
    }
    public void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String photoUrl = documentSnapshot.getString("photo");
                            if (photoUrl != null) {
                                Uri photoUri = Uri.parse(photoUrl);
                                Glide.with(this).load(photoUri).into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.ic_default_profile_picture);
                            }

                            Long puntosLong = documentSnapshot.getLong("puntos");
                            int puntos = (puntosLong != null) ? puntosLong.intValue() : 0;
                            String nivel = calcularNivel(puntos);
                            db.collection("usuarios").document(user.getUid())
                                    .update("nivel", nivel);
                            levelTextView.setText("Nivel " + nivel);
                            setupProgressBar(nivel, puntos);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error al obtener los datos del usuario.", e));
        }
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
            levelProgressBar.setMax(100);
            levelProgressBar.setProgress(puntos);
        } else if (nivel.equals("silver")) {
            levelProgressBar.setMax(200);
            levelProgressBar.setProgress(puntos - 100);
        } else if (nivel.equals("gold")) {
            levelProgressBar.setMax(500);
            levelProgressBar.setProgress(puntos - 300);
        }
    }

    public void updateProfilePhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile_picture);
        }
    }

}
