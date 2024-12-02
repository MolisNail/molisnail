package com.espparki.molisnail.admin.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.espparki.molisnail.admin.AdminActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_users_admin);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        adapter = new UsersAdapter(userList, this::onViewUser, this::onDeleteUser);
        recyclerView.setAdapter(adapter);
        loadUsersFromFirebase();
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(UsersAdminActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUsersFromFirebase() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            userList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("UsersAdminActivity", "Error al cargar usuarios", e));
    }

    private void onViewUser(User user) {
        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    //borrar el usuario (borrar también sus citas asociadas)
    private void onDeleteUser(User user) {
        db.collection("citas")
                .whereEqualTo("userId", user.getId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        db.collection("citas").document(document.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("UsersAdminActivity", "Cita eliminada: " + document.getId()))
                                .addOnFailureListener(e -> Log.e("UsersAdminActivity", "Error al eliminar cita", e));
                    }
                    db.collection("usuarios").document(user.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                userList.remove(user);
                                adapter.notifyDataSetChanged();
                                Log.d("UsersAdminActivity", "Usuario eliminado correctamente");
                            })
                            .addOnFailureListener(e -> Log.e("UsersAdminActivity", "Error al eliminar usuario", e));
                })
                .addOnFailureListener(e -> Log.e("UsersAdminActivity", "Error al buscar citas del usuario", e));
    }
}
