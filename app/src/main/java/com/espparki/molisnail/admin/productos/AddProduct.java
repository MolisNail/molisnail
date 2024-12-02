package com.espparki.molisnail.admin.productos;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etNombre, etPrecio;
    private ImageView ivImagen;
    private Button btnGuardar;
    private ProgressBar progressBar;

    private Uri imageUri;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_add_product);
        db = FirebaseFirestore.getInstance();
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        ivImagen = findViewById(R.id.ivImagen);
        btnGuardar = findViewById(R.id.btnGuardar);
        progressBar = findViewById(R.id.progressBar);

        ivImagen.setOnClickListener(v -> openFileChooser());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(precioStr)) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Introduce un precio válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProduct(nombre, precio, imageUri);
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivImagen.setImageURI(imageUri);
        }
    }

    private void saveProduct(String nombre, double precio, Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap rotatedBitmap = rotateImageIfRequired(bitmap);

            // Comprimir la imagen
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();
            String imageBase64 = android.util.Base64.encodeToString(imageData, android.util.Base64.DEFAULT);
            Map<String, Object> product = new HashMap<>();
            product.put("nombre", nombre);
            product.put("precio", precio);
            product.put("imagenUrl", "data:image/jpeg;base64,"+imageBase64);

            db.collection("productos")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap) {
        try {
            int rotation = getImageRotation(imageUri);
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private int getImageRotation(Uri uri) throws IOException {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        try (Cursor cursor = getContentResolver().query(uri, orientationColumn, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
            }
        }
        return 0;
    }
}
