package com.espparki.molisnail;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PedirCitaActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private Spinner serviceSpinner;
    private Button btnSelectDate;
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
        btnSelectDate = findViewById(R.id.selectDateButton);
        btnConfirmCita = findViewById(R.id.btnConfirmCita);

        // Configura el botón de selección de fecha
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Configura el botón de confirmar cita
        btnConfirmCita.setOnClickListener(v -> confirmCita());
    }

    private void showDatePicker() {
        // Define restricciones de fecha
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(date);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // Solo permite martes, jueves y fines de semana
                return dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY ||
                        dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(android.os.Parcel dest, int flags) {
            }
        });

        // Crea el MaterialDatePicker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // Manejador de selección de fecha
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Validar la fecha seleccionada
            Calendar selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            selectedCalendar.setTimeInMillis(selection);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            selectedDateStr = dateFormat.format(selectedCalendar.getTime());

            checkAvailableTimes(selectedDateStr, new ArrayList<>(Arrays.asList(
                    "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                    "15:00", "15:30", "16:00", "16:30"
            )));
        });

        datePicker.show(getSupportFragmentManager(), "MaterialDatePicker");
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
                .addOnFailureListener(e -> Toast.makeText(this, "Error al verificar disponibilidad", Toast.LENGTH_SHORT).show());
    }

    private void confirmCita() {
        String selectedTime = (String) timeSpinner.getSelectedItem();
        String selectedService = (String) serviceSpinner.getSelectedItem();

        if (selectedDateStr != null && selectedTime != null && selectedService != null) {
            db.collection("citas").add(new Cita(selectedDateStr, selectedTime, selectedService))
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Cita confirmada", Toast.LENGTH_SHORT).show();

                        // Volver al fragmento de citas
                        finish(); // Finaliza la actividad actual y regresa a la anterior
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al confirmar la cita", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Por favor, selecciona una fecha, hora y servicio", Toast.LENGTH_SHORT).show();
        }
    }
}
