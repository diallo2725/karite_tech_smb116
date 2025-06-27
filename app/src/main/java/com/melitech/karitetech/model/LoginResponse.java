package com.melitech.karitetech.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

        @SerializedName("user")
        private User user;

        @SerializedName("token")
        private String token;

        // Getters and setters
        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public String getToken() {
                return token;
        }

        public void setToken(String token) {
                this.token = token;
        }
}
