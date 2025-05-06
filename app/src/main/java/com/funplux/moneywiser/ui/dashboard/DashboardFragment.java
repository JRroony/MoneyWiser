package com.funplux.moneywiser.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.funplux.moneywiser.R;
import com.funplux.moneywiser.models.Transaction;
import com.funplux.moneywiser.utils.FirebaseHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import com.funplux.moneywiser.utils.CategoryUtils;

public class DashboardFragment extends Fragment {
    private MaterialCardView balanceCard;
    private MaterialCardView incomeCard;
    private MaterialCardView expensesCard;
    private FloatingActionButton addTransactionFab;
    private TextView balanceAmount;
    private TextView incomeAmount;
    private TextView expensesAmount;
    private RecyclerView recentTransactionsRecycler;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private double totalIncome = 0;
    private double totalExpenses = 0;
    private FirebaseHelper firebaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize FirebaseHelper
        firebaseHelper = FirebaseHelper.getInstance();

        initializeViews(root);
        setupRecyclerView();
        setupClickListeners();
        loadTransactions();

        return root;
    }

    private void initializeViews(View root) {
        balanceCard = root.findViewById(R.id.balance_card);
        incomeCard = root.findViewById(R.id.income_card);
        expensesCard = root.findViewById(R.id.expenses_card);
        addTransactionFab = root.findViewById(R.id.add_transaction_fab);
        balanceAmount = root.findViewById(R.id.balance_amount);
        incomeAmount = root.findViewById(R.id.income_amount);
        expensesAmount = root.findViewById(R.id.expenses_amount);
        recentTransactionsRecycler = root.findViewById(R.id.recent_transactions_recycler);
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        recentTransactionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTransactionsRecycler.setAdapter(transactionAdapter);
    }

    private void setupClickListeners() {
        addTransactionFab.setOnClickListener(v -> showAddTransactionDialog());
    }

    private void loadTransactions() {
        Query query = firebaseHelper.getTransactions();
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error loading transactions: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
                return;
            }

            transactions.clear();
            totalIncome = 0;
            totalExpenses = 0;

            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Transaction transaction = doc.toObject(Transaction.class);
                    if (transaction != null) {
                        transactions.add(transaction);
                        if ("INCOME".equals(transaction.getType())) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpenses += transaction.getAmount();
                        }
                    }
                }
            }

            updateFinancialOverview();
            transactionAdapter.notifyDataSetChanged();
        });
    }

    private void updateFinancialOverview() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        double balance = totalIncome - totalExpenses;

        balanceAmount.setText(currencyFormat.format(balance));
        incomeAmount.setText(currencyFormat.format(totalIncome));
        expensesAmount.setText(currencyFormat.format(totalExpenses));
    }

    private void showAddTransactionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        TextInputEditText titleInput = dialogView.findViewById(R.id.transaction_title);
        TextInputEditText amountInput = dialogView.findViewById(R.id.transaction_amount);
        AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.transaction_category);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.transaction_description);
        RadioGroup typeGroup = dialogView.findViewById(R.id.transaction_type_group);

        // Set up category dropdown
        String[] categories = CategoryUtils.EXPENSE_CATEGORIES.toArray(new String[0]);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categories);
        categoryInput.setAdapter(categoryAdapter);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Transaction")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String title = titleInput.getText().toString();
                String amountStr = amountInput.getText().toString();
                String category = categoryInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String type = typeGroup.getCheckedRadioButtonId() == R.id.radio_income ? "INCOME" : "EXPENSE";

                if (title.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    String userId = firebaseHelper.getCurrentUserId();
                    if (userId != null) {
                        Transaction transaction = new Transaction(
                            title,
                            amount,
                            category,
                            type,
                            description,
                            userId
                        );
                        firebaseHelper.addTransaction(transaction)
                            .addOnSuccessListener(aVoid -> 
                                Toast.makeText(getContext(), "Transaction added successfully", 
                                    Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> 
                                Toast.makeText(getContext(), "Error adding transaction: " + e.getMessage(),
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