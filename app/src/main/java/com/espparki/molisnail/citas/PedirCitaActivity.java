package com.espparki.molisnail.citas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PedirCitaActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private Spinner serviceSpinner;
    private Button btnSelectDate;
    private Button btnConfirmCita;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String selectedDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citas_activity_pedir_cita);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        timeSpinner = findViewById(R.id.spinnerTime);
        serviceSpinner = findViewById(R.id.spinnerService);
        btnSelectDate = findViewById(R.id.selectDateButton);
        btnConfirmCita = findViewById(R.id.btnConfirmCita);

        initializeSpinners();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnConfirmCita.setOnClickListener(v -> confirmCita());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initializeSpinners() {
        List<String> initialTimeList = new ArrayList<>();
        initialTimeList.add("Seleccionar una fecha");
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, initialTimeList);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        List<String> services = new ArrayList<>();
        services.add("Seleccionar un servicio");
        services.addAll(Arrays.asList("Manicura", "Pedicura", "Uñas Acrílicas", "Uñas de Gel", "Decoración de Uñas"));
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(serviceAdapter);
    }

    private void showDatePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        long todayInMillis = System.currentTimeMillis();
        constraintsBuilder.setStart(todayInMillis);

        constraintsBuilder.setValidator(new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(date);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                return (dayOfWeek >= Calendar.TUESDAY && dayOfWeek <= Calendar.SATURDAY) && (date >= todayInMillis);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(android.os.Parcel dest, int flags) {
            }
        });

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(todayInMillis)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            selectedCalendar.setTimeInMillis(selection);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            selectedDateStr = dateFormat.format(selectedCalendar.getTime());

            btnSelectDate.setText(selectedDateStr);

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
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String reservedTime = document.getString("hora");
                        if (reservedTime != null) {
                            times.remove(reservedTime);
                        }
                    }

                    times.add(0, "Seleccionar una hora"); // Añadir mensaje predeterminado al principio
                    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timeSpinner.setAdapter(timeAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al verificar disponibilidad", Toast.LENGTH_SHORT).show());
    }

    private void confirmCita() {
        String selectedTime = (String) timeSpinner.getSelectedItem();
        String selectedService = (String) serviceSpinner.getSelectedItem();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (selectedDateStr != null && selectedTime != null && selectedService != null
                && !selectedTime.equals("Seleccionar una hora")
                && !selectedService.equals("Seleccionar un servicio")
                && userId != null) {

            int puntosASumar = getPuntosPorServicio(selectedService);
            Cita cita = new Cita(selectedDateStr, selectedTime, selectedService, null, userId);

            db.collection("citas").add(cita)
                    .addOnSuccessListener(documentReference -> {
                        db.collection("usuarios").document(userId)
                                .update("puntos", FieldValue.increment(puntosASumar))
                                .addOnSuccessListener(aVoid -> {
                                    sendConfirmationEmail(auth.getCurrentUser().getEmail(), selectedDateStr, selectedTime, selectedService);
                                    Toast.makeText(this, "Cita confirmada. Puntos añadidos.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar los puntos", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al confirmar la cita", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Por favor, selecciona una fecha, hora y servicio válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendConfirmationEmail(String recipientEmail, String date, String time, String service) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                final String username = "contactmolisnail@gmail.com";
                final String password = "dxpc knnr rncc otnb";


                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("contactmolisnail@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Confirmación de cita - Moli's Nail");
                message.setText(String.format(
                        "Su cita ha sido confirmada el día %s, a las %s.\nEl servicio seleccionado ha sido: %s.\n\n¡Gracias por utilizar Moli´s Nail!",
                        date, time, service
                ));

                Transport.send(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int getPuntosPorServicio(String servicio) {
        switch (servicio) {
            case "Manicura":
                return 7;
            case "Pedicura":
                return 6;
            case "Uñas Acrílicas":
                return 8;
            case "Uñas de Gel":
                return 9;
            case "Decoración de Uñas":
                return 3;
            default:
                return 0;
        }
    }
}
