package com.espparki.molisnail.admin.citas;

public class CitaCompleta {
    private String id;
    private String userId;
    private String servicio;
    private String fecha;
    private String hora;
    private String correo;

    public CitaCompleta(String id, String userId, String servicio, String fecha, String hora, String correo) {
        this.id = id;
        this.userId = userId;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.correo = correo;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getServicio() {
        return servicio;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getCorreo() {
        return correo;
    }
}
