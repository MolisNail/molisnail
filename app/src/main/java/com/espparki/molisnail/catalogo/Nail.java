package com.espparki.molisnail.catalogo;

public class Nail {
    private String nombre;
    private String imagenUrl;
    private String estilo;

    public Nail() {}  // Constructor vacío requerido para Firebase

    public Nail(String nombre, String imagenUrl, String estilo) {
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.estilo = estilo;
    }

    public String getNombre() { return nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public String getEstilo() { return estilo; }
}