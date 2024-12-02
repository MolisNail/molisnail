package com.espparki.molisnail.admin.catalogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public class AddDesignActivity extends AppCompatActivity {

    private EditText etNombre;
    private ImageView ivDesign;
    private Uri imageUri;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_add_design);

        etNombre = findViewById(R.id.etNombreDesign);
        ivDesign = findViewById(R.id.ivDesignImage);
        Button btnUpload = findViewById(R.id.btnUploadDesign);

        db = FirebaseFirestore.getInstance();

        ivDesign.setOnClickListener(v -> selectImage());
        btnUpload.setOnClickListener(v -> uploadDesign());
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap correctedBitmap = correctImageOrientation(bitmap, imageUri);
                Bitmap compressedBitmap = compressBitmap(correctedBitmap);

                ivDesign.setImageBitmap(compressedBitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar la imagen.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void uploadDesign() {
        String nombre = etNombre.getText().toString().trim();

        if (nombre.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Por favor, ingresa un nombre y selecciona una imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap correctedBitmap = correctImageOrientation(bitmap, imageUri);
            Bitmap compressedBitmap = compressBitmap(correctedBitmap);
            String base64Image = encodeImageToBase64(compressedBitmap);
            saveDesignToFirestore(nombre, base64Image);
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar la imagen.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //Corregir imagen en caso de orientación errónea
    private Bitmap correctImageOrientation(Bitmap bitmap, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            android.media.ExifInterface exif = new android.media.ExifInterface(inputStream);

            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION,
                    android.media.ExifInterface.ORIENTATION_UNDEFINED);

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
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //comprimir la imagen en caso de tamaño superior a 1MB
    private Bitmap compressBitmap(Bitmap bitmap) {
        int newWidth = 600;
        int newHeight = (int) (bitmap.getHeight() * (600.0 / bitmap.getWidth()));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        return scaledBitmap;
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveDesignToFirestore(String nombre, String base64Image) {
        String id = UUID.randomUUID().toString();

        HashMap<String, Object> designData = new HashMap<>();
        designData.put("id", id);
        designData.put("nombre", nombre);
        designData.put("imagenBase64", base64Image);

        db.collection("designs").document(id).set(designData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Diseño subido correctamente.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el diseño.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
