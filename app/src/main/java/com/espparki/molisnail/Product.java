package com.espparki.molisnail;

public class Product {
    private String id; // Nuevo campo para el ID del producto
    private String nombre;
    private String imagenUrl;
    private double precio;

    public Product() {}  // Constructor vacío requerido para Firebase

    public Product(String id, String nombre, String imagenUrl, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
    }

    public String getId() { return id; } // Nuevo método para obtener el ID
    public void setId(String id) { this.id = id; } // Método para establecer el ID

    public String getNombre() { return nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public double getPrecio() { return precio; }
}
