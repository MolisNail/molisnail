package com.espparki.molisnail.tienda;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String id;
    private String nombre;
    private String imagen;
    private double precio;
    private int quantity;

    // Constructor vacío (necesario para Firebase)
    public CartItem() {
    }

    // Constructor con los parámetros utilizados
    public CartItem(String id, String nombre, double precio, int quantity) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.quantity = quantity;
    }

    // Constructor parcelable
    protected CartItem(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        imagen = in.readString();
        precio = in.readDouble();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(imagen);
        dest.writeDouble(precio);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
