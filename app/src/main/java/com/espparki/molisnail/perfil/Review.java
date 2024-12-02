package com.espparki.molisnail.perfil;

public class Review {
    private String userId;
    private String review;
    private float rating;

    public Review() {
    }

    public Review(String userId, String review, float rating) {
        this.userId = userId;
        this.review = review;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public String getReview() {
        return review;
    }

    public float getRating() {
        return rating;
    }
}
