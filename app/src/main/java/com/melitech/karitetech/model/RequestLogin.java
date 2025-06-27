package com.melitech.karitetech.model;

public class RequestLogin {
    private String username;
    private String password;

    // Constructeur vide requis pour les bibliothèques comme Gson ou Jackson
    public RequestLogin() {}

    // Constructeur avec paramètres
    public RequestLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
