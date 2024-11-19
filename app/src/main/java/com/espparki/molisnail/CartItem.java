package com.espparki.molisnail;

public class CartItem {
    private String id; // Identificador único del producto
    private String nombre;
    private double precio;
    private int quantity;

    public CartItem() {} // Constructor vacío requerido por Firebase

    public CartItem(String id, String nombre, double precio, int quantity) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
