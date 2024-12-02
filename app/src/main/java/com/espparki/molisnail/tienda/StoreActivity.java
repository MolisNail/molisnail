package com.espparki.molisnail.tienda;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class StoreActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_welcome);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Simulamos una compra y actualizamos los puntos del usuario
        realizarCompra();
    }

    // Función que actualiza los puntos del usuario
    private void realizarCompra() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            updatePoints(user.getUid(), 10);  // Sumar 10 puntos por una compra
        }
    }

    // Función para actualizar los puntos
    private void updatePoints(String userId, int pointsToAdd) {
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.update("puntos", FieldValue.increment(pointsToAdd))
                .addOnSuccessListener(aVoid -> {
                    // Puntos actualizados con éxito
                    Log.d("Firestore", "Puntos actualizados correctamente.");
                    checkAndUpdateLevel(userId);  // Verificar si el nivel del usuario cambia
                })
                .addOnFailureListener(e -> {
                    // Error al actualizar los puntos
                    Log.w("Firestore", "Error al actualizar los puntos.", e);
                });
    }

    // Función que verifica y actualiza el nivel del usuario
    private void checkAndUpdateLevel(String userId) {
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int puntos = documentSnapshot.getLong("puntos").intValue();
                String nivelActual = documentSnapshot.getString("nivel");

                String nuevoNivel;
                if (puntos >= 500) {
                    nuevoNivel = "gold";
                } else if (puntos >= 100) {
                    nuevoNivel = "silver";
                } else {
                    nuevoNivel = nivelActual;
                }

                if (!nuevoNivel.equals(nivelActual)) {
                    userRef.update("nivel", nuevoNivel)
                            .addOnSuccessListener(aVoid -> {
                                // Nivel actualizado con éxito
                                Log.d("Firestore", "Nivel actualizado correctamente a " + nuevoNivel);
                            });
                }
            }
        });
    }
}
