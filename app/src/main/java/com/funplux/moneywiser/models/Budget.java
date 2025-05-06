package com.funplux.moneywiser.models;

public class Budget {
    private String id;
    private String category;
    private double limit;
    private double spent;
    private String userId;
    private String period; // "MONTHLY", "WEEKLY", "YEARLY"

    public Budget() {
        // Required empty constructor for Firebase
    }

    public Budget(String category, double limit, String period, String userId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.category = category;
        this.limit = limit;
        this.spent = 0.0;
        this.period = period;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }

    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public double getRemaining() {
        return limit - spent;
    }

    public double getProgressPercentage() {
        return (spent / limit) * 100;
    }
} 