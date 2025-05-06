package com.funplux.moneywiser.models;

import java.util.Date;

public class Transaction {
    private String id;
    private String title;
    private double amount;
    private String category;
    private Date date;
    private String type; // "INCOME" or "EXPENSE"
    private String description;
    private String userId;

    public Transaction() {
        // Required empty constructor for Firebase
    }

    public Transaction(String title, double amount, String category, String type, String description, String userId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = new Date();
        this.type = type;
        this.description = description;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
} 