package com.funplux.moneywiser.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.funplux.moneywiser.R;
import com.funplux.moneywiser.models.Transaction;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<Transaction> transactions;
    private final NumberFormat currencyFormat;
    private final SimpleDateFormat dateFormat;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.titleText.setText(transaction.getTitle());
        holder.categoryText.setText(transaction.getCategory());
        holder.dateText.setText(dateFormat.format(transaction.getDate()));
        
        String amount = currencyFormat.format(transaction.getAmount());
        if ("INCOME".equals(transaction.getType())) {
            holder.amountText.setTextColor(holder.itemView.getContext().getColor(R.color.income_green));
            holder.amountText.setText("+" + amount);
        } else {
            holder.amountText.setTextColor(holder.itemView.getContext().getColor(R.color.expense_red));
            holder.amountText.setText("-" + amount);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView categoryText;
        TextView amountText;
        TextView dateText;

        TransactionViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.transaction_title);
            categoryText = itemView.findViewById(R.id.transaction_category);
            amountText = itemView.findViewById(R.id.transaction_amount);
            dateText = itemView.findViewById(R.id.transaction_date);
        }
    }
} 