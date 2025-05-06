package com.funplux.moneywiser.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.funplux.moneywiser.R;
import com.funplux.moneywiser.models.Budget;
import com.funplux.moneywiser.utils.FirebaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BudgetFragment extends Fragment {
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private List<Budget> budgetList;
    private FirebaseHelper firebaseHelper;

    private static final String[] BUDGET_PERIODS = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize FirebaseHelper
        firebaseHelper = FirebaseHelper.getInstance();

        recyclerView = root.findViewById(R.id.budget_recycler_view);
        budgetList = new ArrayList<>();
        adapter = new BudgetAdapter(budgetList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_add_budget);
        fab.setOnClickListener(v -> showAddBudgetDialog());

        loadBudgets();

        return root;
    }

    private void loadBudgets() {
        Query query = firebaseHelper.getBudgets();
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error loading budgets: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
                return;
            }

            budgetList.clear();
            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Budget budget = doc.toObject(Budget.class);
                    if (budget != null) {
                        budgetList.add(budget);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void showAddBudgetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_budget, null);
        AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.category_input);
        AutoCompleteTextView periodInput = dialogView.findViewById(R.id.period_input);

        // Set up category dropdown
        String[] categories = {"Food", "Transport", "Entertainment", "Shopping", "Bills", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categories);
        categoryInput.setAdapter(categoryAdapter);

        // Set up period dropdown
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_dropdown_item_1line, BUDGET_PERIODS);
        periodInput.setAdapter(periodAdapter);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Budget")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String category = categoryInput.getText().toString();
                String period = periodInput.getText().toString();
                String amountStr = ((android.widget.EditText) dialogView.findViewById(R.id.amount_input))
                    .getText().toString();

                if (category.isEmpty() || period.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    String userId = firebaseHelper.getCurrentUserId();
                    if (userId != null) {
                        Budget budget = new Budget(
                            category,
                            amount,
                            period,
                            userId
                        );
                        firebaseHelper.addBudget(budget)
                            .addOnSuccessListener(aVoid -> 
                                Toast.makeText(getContext(), "Budget added successfully", 
                                    Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> 
                                Toast.makeText(getContext(), "Error adding budget: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 