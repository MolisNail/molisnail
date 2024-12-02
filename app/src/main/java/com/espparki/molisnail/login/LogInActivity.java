package com.espparki.molisnail.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.admin.AdminActivity;
import com.espparki.molisnail.MainActivity;
import com.espparki.molisnail.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvRegister, adminLoginTextView;
    private SignInButton btnGoogleSignIn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnGoogleSignIn = findViewById(R.id.btnGoogle);
        adminLoginTextView = findViewById(R.id.adminLoginTextView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        configureGoogleSignIn();

        googleSignInClient.signOut();

        findViewById(R.id.btnLogin).setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LogInActivity.this, RegisterActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        adminLoginTextView.setOnClickListener(v -> promptAdminPassword());
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduce un correo electrónico");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduce una contraseña");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "Google sign-in failed", e);
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveGoogleUserData(user);
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveGoogleUserData(FirebaseUser user) {
        if (user == null) return;

        String googlePhotoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("name", user.getDisplayName());
        userData.put("Google_photo", googlePhotoUrl);

        db.collection("usuarios").document(user.getUid()).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Datos guardados"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error al guardar datos", e));
    }

    private void promptAdminPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Inicio de sesión como administrador");

        final EditText input = new EditText(this);
        input.setHint("Introduce la contraseña");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setGravity(Gravity.CENTER);

        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String enteredPassword = input.getText().toString();
            if (TextUtils.isEmpty(enteredPassword)) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyAdminPassword(enteredPassword);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void verifyAdminPassword(String enteredPassword) {
        db.collection("config").document("admin")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String adminPassword = documentSnapshot.getString("admin_password");
                        if (enteredPassword.equals(adminPassword)) {
                            Toast.makeText(this, "Inicio de sesión como administrador exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, AdminActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No se encontró la configuración de administrador", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al verificar la contraseña de administrador", e);
                    Toast.makeText(this, "Error al verificar la contraseña", Toast.LENGTH_SHORT).show();
                });
    }

}

