package com.espparki.molisnail;

public class Product {
    private String nombre;
    private String imagenUrl;
    private double precio;

    public Product() {}  // Constructor vacío requerido para Firebase

    public Product(String nombre, String imagenUrl, double precio) {
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public double getPrecio() { return precio; }
}
