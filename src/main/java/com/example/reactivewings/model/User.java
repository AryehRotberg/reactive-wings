package com.example.reactivewings.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    private String email;
    private List<Flight> subscriptions = new ArrayList<>();

    public User() {}

    public User(String email) {
        this.id = email;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Flight> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<Flight> subscriptions) { this.subscriptions = subscriptions; }
}
