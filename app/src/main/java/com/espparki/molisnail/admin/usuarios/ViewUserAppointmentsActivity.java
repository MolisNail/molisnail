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
        db.collection("citas").document(appointment.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    futureAppointments.remove(appointment);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("ViewAppointments", "Error al eliminar cita", e));
    }
}
