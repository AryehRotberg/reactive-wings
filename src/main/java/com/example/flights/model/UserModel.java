package com.example.flights.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserModel
{
    @Id
    private String id;

    private String email;

    private List<SubscriptionModel> subscriptions = new ArrayList<>();

    public UserModel() {}

    public UserModel(String email) {
        this.id = email;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<SubscriptionModel> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<SubscriptionModel> subscriptions) { this.subscriptions = subscriptions; }
}
