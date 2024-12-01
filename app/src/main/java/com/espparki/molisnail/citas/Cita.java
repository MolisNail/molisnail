package com.espparki.molisnail.citas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cita {
    private String fecha;
    private String hora;
    private String servicio;
    private String id; // ID del documento en Firestore
    private String userId;

    public Cita(String fecha, String hora, String servicio, String id, String userId) {
        this.fecha = fecha;
        this.hora = hora;
        this.servicio = servicio;
        this.id = id;
        this.userId = userId;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getServicio() {
        return servicio;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Date getFechaAsDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            return format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
