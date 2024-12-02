package com.espparki.molisnail.tienda;

import java.util.ArrayList;
import java.util.List;

public class CartDataHolder {
    private static CartDataHolder instance;
    private List<CartItem> cartItems;

    private CartDataHolder() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartDataHolder getInstance() {
        if (instance == null) {
            instance = new CartDataHolder();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> items) {
        cartItems = items;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
