package com.espparki.molisnail.perfil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private TextView averageRatingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_activity_review);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewReviews);
        reviewEditText = findViewById(R.id.editTextReview);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitReviewButton);
        averageRatingTextView = findViewById(R.id.averageRatingTextView);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        // Cargar reseñas existentes
        loadReviews();

        // Manejar el botón de enviar reseña
        submitButton.setOnClickListener(v -> submitReview());
    }

    private void loadReviews() {
        db.collection("reviews")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reviewList.clear();
                    float totalRating = 0;
                    int reviewCount = 0;

                    for (DocumentSnapshot document : querySnapshot) {
                        Review review = document.toObject(Review.class);
                        if (review != null) {
                            reviewList.add(review);
                            totalRating += review.getRating();
                            reviewCount++;
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // Calcular la media de las valoraciones
                    if (reviewCount > 0) {
                        float averageRating = totalRating / reviewCount;
                        averageRatingTextView.setText(String.format("Valoración media: %.1f", averageRating));
                    } else {
                        averageRatingTextView.setText("No hay reseñas todavía.");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show());
    }

    private void submitReview() {
        String userId = auth.getCurrentUser().getUid();
        String reviewText = reviewEditText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (reviewText.isEmpty() || rating == 0) {
            Toast.makeText(this, "Por favor, escribe una reseña y selecciona una valoración.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto de la reseña
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("userId", userId);
        reviewData.put("review", reviewText);
        reviewData.put("rating", rating);

        // Subir la reseña a Firestore
        db.collection("reviews").add(reviewData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reseña enviada con éxito", Toast.LENGTH_SHORT).show();
                    reviewEditText.setText("");
                    ratingBar.setRating(0);
                    loadReviews(); // Recargar reseñas
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al enviar la reseña", Toast.LENGTH_SHORT).show());
    }
}
