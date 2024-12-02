package com.espparki.molisnail.admin.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewUserActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private TextView tvName, tvEmail, tvAddress, tvLevel, tvPoints;
    private Button btnBack, btnViewAppointments;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_user);

        ivPhoto = findViewById(R.id.ivUserPhotoDetail);
        tvName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvUserEmailDetail);
        tvAddress = findViewById(R.id.tvUserAddress);
        tvLevel = findViewById(R.id.tvUserLevel);
        tvPoints = findViewById(R.id.tvUserPoints);
        btnBack = findViewById(R.id.btnBack);
        btnViewAppointments = findViewById(R.id.btnViewAppointments);

        db = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("userId");
        loadUserDetails(userId);

        btnBack.setOnClickListener(v -> finish());
        btnViewAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewUserAppointmentsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void loadUserDetails(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvName.setText(documentSnapshot.getString("name"));
                        tvEmail.setText(documentSnapshot.getString("email"));
                        tvAddress.setText(documentSnapshot.getString("direccion") != null ? documentSnapshot.getString("direccion") : "Sin dirección");
                        tvLevel.setText(documentSnapshot.getString("nivel"));
                        tvPoints.setText(String.valueOf(documentSnapshot.getLong("puntos")));

                        String photoUrl = documentSnapshot.getString("Google_photo") != null
                                ? documentSnapshot.getString("Google_photo")
                                : documentSnapshot.getString("photo");

                        Glide.with(this).load(photoUrl).into(ivPhoto);
                    }
                });
    }
}
