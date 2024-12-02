package com.espparki.molisnail.admin.productos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etNombre, etPrecio;
    private ImageView ivImagen;
    private Button btnActualizar;
    private ProgressBar progressBar;

    private Uri imageUri;
    private String productId;
    private FirebaseFirestore db;
    private String currentImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_edit_product);
        db = FirebaseFirestore.getInstance();
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        ivImagen = findViewById(R.id.ivImagen);
        btnActualizar = findViewById(R.id.btnActualizar);
        progressBar = findViewById(R.id.progressBar);
        Intent intent = getIntent();
        if (intent.hasExtra("productId")) {
            productId = intent.getStringExtra("productId");
            loadProductData(productId);
        } else {
            Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
            finish();
        }
        ivImagen.setOnClickListener(v -> openFileChooser());
        btnActualizar.setOnClickListener(v -> updateProduct());
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
            try {
                Bitmap bitmap = getCorrectedBitmap(imageUri);
                ivImagen.setImageBitmap(bitmap);
                currentImageBase64 = bitmapToBase64(bitmap);
            } catch (IOException e) {
                Log.e("EditProduct", "Error al procesar la imagen", e);
                Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductData(String productId) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        db.collection("productos").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        Double precio = documentSnapshot.getDouble("precio");
                        currentImageBase64 = documentSnapshot.getString("imagenUrl");

                        etNombre.setText(nombre);
                        etPrecio.setText(String.valueOf(precio));
                        if (currentImageBase64 != null) {
                            Glide.with(this).load(currentImageBase64).into(ivImagen);
                        }
                    } else {
                        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Log.e("EditProduct", "Error al cargar datos del producto", e);
                    Toast.makeText(this, "Error al cargar datos del producto", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProduct() {
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

        progressBar.setVisibility(ProgressBar.VISIBLE);

        db.collection("productos").document(productId)
                .update("nombre", nombre, "precio", precio, "imagenUrl", "data:image/jpeg;base64,"+currentImageBase64)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Log.e("EditProduct", "Error al actualizar producto", e);
                    Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                });
    }

    private Bitmap getCorrectedBitmap(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        InputStream exifInputStream = getContentResolver().openInputStream(imageUri);
        android.media.ExifInterface exif = new android.media.ExifInterface(exifInputStream);
        int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);
        exifInputStream.close();

        switch (orientation) {
            case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }
}
