package com.funplux.moneywiser.ui.analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.funplux.moneywiser.R;
import com.funplux.moneywiser.models.Transaction;
import com.funplux.moneywiser.utils.FirebaseHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment {
    private PieChart expensePieChart;
    private BarChart monthlyBarChart;
    private FirebaseHelper firebaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_analytics, container, false);

        expensePieChart = root.findViewById(R.id.expense_pie_chart);
        monthlyBarChart = root.findViewById(R.id.monthly_bar_chart);

        // Initialize FirebaseHelper
        firebaseHelper = FirebaseHelper.getInstance();

        setupCharts();
        loadTransactionData();

        return root;
    }

    private void setupCharts() {
        // Setup Pie Chart
        expensePieChart.getDescription().setEnabled(false);
        expensePieChart.setDrawHoleEnabled(true);
        expensePieChart.setHoleColor(Color.WHITE);
        expensePieChart.setTransparentCircleRadius(61f);
        expensePieChart.setTransparentCircleColor(Color.WHITE);
        expensePieChart.setHoleRadius(58f);
        expensePieChart.setRotationAngle(0);
        expensePieChart.setRotationEnabled(true);
        expensePieChart.setHighlightPerTapEnabled(true);
        expensePieChart.animateY(1400);
        expensePieChart.setEntryLabelColor(Color.WHITE);
        expensePieChart.setEntryLabelTextSize(12f);

        // Setup Bar Chart
        monthlyBarChart.getDescription().setEnabled(false);
        monthlyBarChart.setDrawGridBackground(false);
        monthlyBarChart.setDrawBarShadow(false);
        monthlyBarChart.setDrawValueAboveBar(true);
        monthlyBarChart.setPinchZoom(false);
        monthlyBarChart.setScaleEnabled(false);
        monthlyBarChart.animateY(1000);
    }

    private void loadTransactionData() {
        Query query = firebaseHelper.getTransactions();
        query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            Map<String, Float> categoryExpenses = new HashMap<>();
            Map<String, Float> monthlyIncome = new HashMap<>();
            Map<String, Float> monthlyExpenses = new HashMap<>();

            for (DocumentSnapshot doc : value.getDocuments()) {
                Transaction transaction = doc.toObject(Transaction.class);
                if (transaction == null) continue;

                String month = new java.text.SimpleDateFormat("MMM", java.util.Locale.US)
                        .format(transaction.getDate());

                if ("EXPENSE".equals(transaction.getType())) {
                    // Update category expenses
                    categoryExpenses.merge(transaction.getCategory(), 
                        (float) transaction.getAmount(), Float::sum);
                    
                    // Update monthly expenses
                    monthlyExpenses.merge(month, 
                        (float) transaction.getAmount(), Float::sum);
                } else {
                    // Update monthly income
                    monthlyIncome.merge(month, 
                        (float) transaction.getAmount(), Float::sum);
                }
            }

            updatePieChart(categoryExpenses);
            updateBarChart(monthlyIncome, monthlyExpenses);
        });
    }

    private void updatePieChart(Map<String, Float> categoryExpenses) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryExpenses.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        expensePieChart.setData(data);
        expensePieChart.invalidate();
    }

    private void updateBarChart(Map<String, Float> monthlyIncome, Map<String, Float> monthlyExpenses) {
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        int index = 0;

        for (String month : new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun"}) {
            incomeEntries.add(new BarEntry(index, monthlyIncome.getOrDefault(month, 0f)));
            expenseEntries.add(new BarEntry(index, monthlyExpenses.getOrDefault(month, 0f)));
            index++;
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.income_green));

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColor(getResources().getColor(R.color.expense_red));

        BarData data = new BarData(incomeDataSet, expenseDataSet);
        data.setBarWidth(0.3f);
        monthlyBarChart.setData(data);
        monthlyBarChart.invalidate();
    }
} 