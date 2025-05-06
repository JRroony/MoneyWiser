package com.funplux.moneywiser.utils;

import com.funplux.moneywiser.models.Transaction;
import com.funplux.moneywiser.models.Budget;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Transaction methods
    public Task<Void> addTransaction(Transaction transaction) {
        return db.collection("transactions")
                .document(transaction.getId())
                .set(transaction);
    }

    public Query getTransactions() {
        String userId = auth.getCurrentUser().getUid();
        return db.collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING);
    }

    // Budget methods
    public Task<Void> addBudget(Budget budget) {
        return db.collection("budgets")
                .document(budget.getId())
                .set(budget);
    }

    public Query getBudgets() {
        String userId = auth.getCurrentUser().getUid();
        return db.collection("budgets")
                .whereEqualTo("userId", userId);
    }

    public Task<Void> updateBudgetSpent(String budgetId, double amount) {
        return db.collection("budgets")
                .document(budgetId)
                .update("spent", amount);
    }

    // User methods
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public Task<Void> signOut() {
        auth.signOut();
        return Tasks.forResult(null);
    }

    public Task<Void> createUserDocument(String userId, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("createdAt", System.currentTimeMillis());

        return db.collection("users").document(userId).set(userData);
    }

    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return db.collection("users").document(userId).update(updates);
    }

    public Task<Void> deleteUserAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.delete();
        }
        return Tasks.forException(new IllegalStateException("No user is currently signed in"));
    }
} 