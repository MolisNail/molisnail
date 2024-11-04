package com.espparki.molisnail;

public class Cita {
    private String fecha;
    private String hora;
    private String servicio;

    // Constructor vacío requerido para Firebase
    public Cita() {
    }

    // Constructor completo
    public Cita(String fecha, String hora, String servicio) {
        this.fecha = fecha;
        this.hora = hora;
        this.servicio = servicio;
    }

    // Getters y Setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }
}
