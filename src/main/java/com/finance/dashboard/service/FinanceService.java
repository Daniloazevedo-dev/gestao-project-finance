package com.finance.dashboard.service;

import com.finance.dashboard.model.Expense;
import com.finance.dashboard.model.ExpenseSummary;
import com.finance.dashboard.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FinanceService {

    private final ExpenseRepository expenseRepository;

    public FinanceService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> listExpenses() {
        return expenseRepository.findAll();
    }

    public Expense registerExpense(Expense expense) {
        expenseRepository.save(expense);
        return expense;
    }

    public ExpenseSummary calculateSummary() {
        List<Expense> expenses = expenseRepository.findAll();
        return calculateSummary(expenses);
    }

    public ExpenseSummary calculateSummary(List<Expense> expenses) {
        BigDecimal totalPlanned = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = expenses.stream()
            .filter(Expense::isPaid)
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = expenses.stream()
            .map(Expense::getRemaining)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ExpenseSummary(
            scaleCurrency(totalPlanned),
            scaleCurrency(totalPaid),
            scaleCurrency(totalRemaining)
        );
    }

    private BigDecimal scaleCurrency(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
