package com.espparki.molisnail.catalogo;

public class Nail {
    private String id;
    private String nombre;
    private String imagenBase64;
    public Nail() {}

    public Nail(String id, String nombre, String imagenBase64) {
        this.id = id;
        this.nombre = nombre;
        this.imagenBase64 = imagenBase64;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }
}
