package com.espparki.molisnail.citas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

public class MisCitasFragment extends Fragment {

    private RecyclerView recyclerView;
    private CitasAdapter adapter;
    private List<Cita> citasList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.citas_fragment_mis_citas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMisCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadCitas();

        return view;
    }
    private void loadCitas() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("citas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    citasList.clear();
                    Date today = new Date();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String fecha = document.getString("fecha");
                        String hora = document.getString("hora");
                        String servicio = document.getString("servicio");
                        String id = document.getId();
                        String userIdFromDocument = document.getString("userId");

                        Cita cita = new Cita(fecha, hora, servicio, id, userIdFromDocument);

                        // Filtrar solo citas futuras
                        Date citaDate = cita.getFechaAsDate();
                        if (citaDate != null && !citaDate.before(today)) {
                            citasList.add(cita);
                        }
                    }

                    citasList.sort((c1, c2) -> {
                        Date fecha1 = c1.getFechaAsDate();
                        Date fecha2 = c2.getFechaAsDate();

                        if (fecha1 != null && fecha2 != null) {
                            return fecha1.compareTo(fecha2);
                        }
                        return 0;
                    });

                    adapter = new CitasAdapter(citasList, this::deleteCita);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar citas", Toast.LENGTH_SHORT).show());
    }


    private void deleteCita(Cita cita) {
        db.collection("citas").document(cita.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int puntosARestar = getPuntosPorServicio(cita.getServicio());
                    db.collection("usuarios").document(cita.getUserId())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String userEmail = documentSnapshot.getString("email");

                                db.collection("usuarios").document(cita.getUserId())
                                        .update("puntos", FieldValue.increment(-puntosARestar))
                                        .addOnSuccessListener(aVoid2 -> {
                                            citasList.remove(cita);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "Cita eliminada. Puntos descontados.", Toast.LENGTH_SHORT).show();
                                            sendCancellationEmail(userEmail, cita);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar los puntos", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener el correo del usuario", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar cita", Toast.LENGTH_SHORT).show());
    }

    private void sendCancellationEmail(String userEmail, Cita cita) {
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
                        InternetAddress.parse("contactmolisnail@gmail.com")
                );
                message.setSubject("Cancelación de cita");
                message.setText("El usuario " + userEmail + " ha cancelado la cita del día " + cita.getFecha() +
                        " a las " + cita.getHora() + ".\n\n" +
                        "El servicio de la cita era: " + cita.getServicio() + ".\n\n" +
                        "La cita vuelve a estar disponible para su adquisición.");
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

    private void restarPuntosUsuario(String userId, int puntos) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long puntosActuales = documentSnapshot.getLong("puntos");
                    if (puntosActuales == null) puntosActuales = 0L;

                    db.collection("usuarios").document(userId)
                            .update("puntos", Math.max(0, puntosActuales - puntos))
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Puntos actualizados", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar puntos", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener puntos actuales", Toast.LENGTH_SHORT).show());
    }

}
