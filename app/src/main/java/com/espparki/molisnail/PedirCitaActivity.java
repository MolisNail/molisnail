package com.espparki.molisnail;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PedirCitaActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private Spinner serviceSpinner;
    private Button btnConfirmCita;
    private FirebaseFirestore db;
    private String selectedDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_cita);

        db = FirebaseFirestore.getInstance();

        timeSpinner = findViewById(R.id.spinnerTime);
        serviceSpinner = findViewById(R.id.spinnerService);
        btnConfirmCita = findViewById(R.id.btnConfirmCita);

        // Muestra el DatePickerDialog cuando se selecciona la fecha
        Button selectDateButton = findViewById(R.id.selectDateButton); // Botón para abrir el selector de fecha
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Configura el botón de confirmar cita
        btnConfirmCita.setOnClickListener(v -> confirmCita());
    }

    private void showDatePickerDialog() {
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY ||
                    dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                selectedDateStr = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                checkAvailableTimes(selectedDateStr, new ArrayList<>(Arrays.asList(
                        "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                        "15:00", "15:30", "16:00", "16:30"
                )));
            } else {
                Toast.makeText(PedirCitaActivity.this, "Solo puedes seleccionar martes, jueves o fines de semana.", Toast.LENGTH_SHORT).show();
            }
        }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // Solo fechas futuras
        datePickerDialog.show();
    }

    private void checkAvailableTimes(String date, List<String> times) {
        db.collection("citas")
                .whereEqualTo("fecha", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String reservedTime = document.getString("hora");
                        times.remove(reservedTime); // Elimina las horas ya reservadas
                    }

                    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timeSpinner.setAdapter(timeAdapter);
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error al verificar disponibilidad de horas", e));
    }

    private void confirmCita() {
        // Obtén los valores seleccionados y guárdalos en Firestore
        String selectedTime = (String) timeSpinner.getSelectedItem();
        String selectedService = (String) serviceSpinner.getSelectedItem();

        if (selectedDateStr != null && selectedTime != null && selectedService != null) {
            db.collection("citas").add(new Cita(selectedDateStr, selectedTime, selectedService))
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(PedirCitaActivity.this, "Cita confirmada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PedirCitaActivity.this, "Error al confirmar la cita", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Por favor, selecciona una fecha, hora y servicio", Toast.LENGTH_SHORT).show();
        }
    }
}
