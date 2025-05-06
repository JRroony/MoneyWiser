package com.funplux.moneywiser.ui.budget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.funplux.moneywiser.R;
import com.funplux.moneywiser.models.Budget;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private final List<Budget> budgets;
    private final NumberFormat currencyFormat;

    public BudgetAdapter(List<Budget> budgets) {
        this.budgets = budgets;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.categoryText.setText(budget.getCategory());
        holder.periodText.setText(budget.getPeriod());
        holder.limitText.setText(currencyFormat.format(budget.getLimit()));
        holder.spentText.setText(currencyFormat.format(budget.getSpent()));
        holder.remainingText.setText(currencyFormat.format(budget.getRemaining()));

        int progress = (int) budget.getProgressPercentage();
        holder.progressBar.setProgress(progress);

        // Update colors based on progress
        if (progress > 90) {
            holder.progressBar.setIndicatorColor(holder.itemView.getContext().getColor(R.color.expense_red));
            holder.remainingText.setTextColor(holder.itemView.getContext().getColor(R.color.expense_red));
        } else if (progress > 70) {
            holder.progressBar.setIndicatorColor(holder.itemView.getContext().getColor(R.color.orange));
            holder.remainingText.setTextColor(holder.itemView.getContext().getColor(R.color.orange));
        } else {
            holder.progressBar.setIndicatorColor(holder.itemView.getContext().getColor(R.color.income_green));
            holder.remainingText.setTextColor(holder.itemView.getContext().getColor(R.color.income_green));
        }

        // Add warning text if budget is close to limit
        if (progress > 90) {
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText("Warning: Budget almost exceeded!");
        } else if (progress > 70) {
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText("Notice: Budget getting close to limit");
        } else {
            holder.warningText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        TextView periodText;
        TextView limitText;
        TextView spentText;
        TextView remainingText;
        TextView warningText;
        LinearProgressIndicator progressBar;

        BudgetViewHolder(View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.budget_category);
            periodText = itemView.findViewById(R.id.budget_period);
            limitText = itemView.findViewById(R.id.budget_limit);
            spentText = itemView.findViewById(R.id.budget_spent);
            remainingText = itemView.findViewById(R.id.budget_remaining);
            warningText = itemView.findViewById(R.id.budget_warning);
            progressBar = itemView.findViewById(R.id.budget_progress);
        }
    }
} 