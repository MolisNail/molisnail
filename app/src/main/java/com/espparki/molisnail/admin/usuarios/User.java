package com.espparki.molisnail.admin.usuarios;

public class User {
    private String id;
    private String email;
    private String Google_photo;
    private String photo;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogle_photo() {
        return Google_photo;
    }

    public void setGoogle_photo(String Google_photo) {
        this.Google_photo = Google_photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
