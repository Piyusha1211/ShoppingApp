// HelperClass.java
package com.example.aifit;

public class HelperClass {
    private String password;
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HelperClass(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public HelperClass() {
        // Default constructor required for calls to DataSnapshot.getValue(HelperClass.class)
    }
}
