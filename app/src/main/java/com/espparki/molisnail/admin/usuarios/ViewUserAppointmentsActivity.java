package com.espparki.molisnail.admin.usuarios;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ViewUserAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> futureAppointments = new ArrayList<>();
    private List<Appointment> pastAppointments = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        recyclerView = findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBack = findViewById(R.id.btnBack);
        db = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("userId");
        loadAppointments(userId);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAppointments(String userId) {
        db.collection("citas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    futureAppointments.clear();
                    pastAppointments.clear();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date currentDate = new Date();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Appointment appointment = document.toObject(Appointment.class);
                        appointment.setId(document.getId());

                        try {
                            Date appointmentDate = sdf.parse(appointment.getFecha());
                            if (appointmentDate != null) {
                                if (appointmentDate.before(currentDate)) {
                                    pastAppointments.add(appointment);
                                } else {
                                    futureAppointments.add(appointment);
                                }
                            }
                        } catch (ParseException e) {
                            Log.e("Appointments", "Error parsing date", e);
                        }
                    }

                    adapter = new AppointmentAdapter(futureAppointments, pastAppointments, this::deleteFutureAppointment);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("ViewAppointments", "Error al cargar citas", e));
    }

    private void deleteFutureAppointment(Appointment appointment) {
        String userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            Log.e("ViewAppointments", "No se pudo obtener el userId");
            return;
        }

        db.collection("citas").document(appointment.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    futureAppointments.remove(appointment);
                    adapter.notifyDataSetChanged();
                    db.collection("usuarios").document(userId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String userEmail = documentSnapshot.getString("email");
                                if (userEmail != null && !userEmail.isEmpty()) {
                                    sendCancellationEmailToUser(appointment, userEmail);
                                } else {
                                    Log.e("ViewAppointments", "El usuario no tiene un correo registrado");
                                }
                            })
                            .addOnFailureListener(e -> Log.e("ViewAppointments", "Error al obtener usuario", e));
                })
                .addOnFailureListener(e -> Log.e("ViewAppointments", "Error al eliminar cita", e));
    }

    private void sendCancellationEmailToUser(Appointment appointment, String userEmail) {
        new Thread(() -> {
            try {
                // Configuración de propiedades para Gmail SMTP
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                final String username = "contactmolisnail@gmail.com";
                final String password = "dxpc knnr rncc otnb";
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("contactmolisnail@gmail.com"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(userEmail)
                );
                message.setSubject("Cancelación de cita");
                message.setText("Estimado cliente,\n\n" +
                        "Su cita del día " + appointment.getFecha() + " a las " + appointment.getHora() + " ha sido eliminada por motivos empresariales.\n\n" +
                        "El servicio de la cita era: " + appointment.getServicio() + ".\n\n" +
                        "Desde Moli´s Nail queremos pedirle disculpas por los inconvenientes generados y le invitamos a adquirir una cita un día futuro.\n\n" +
                        "Disculpe las molestias.\n\n" +
                        "Atentamente,\n" +
                        "El equipo de Moli´s Nail");
                Transport.send(message);
                Log.i("Email", "Correo enviado exitosamente a " + userEmail);
            } catch (Exception e) {
                Log.e("EmailError", "Error al enviar el correo", e);
            }
        }).start();
    }


}
