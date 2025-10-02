package com.finance.dashboard.service;

import com.finance.dashboard.model.Expense;
import com.finance.dashboard.model.ExpenseSummary;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FinanceService {

    private final List<Expense> expenses = List.of(
        new Expense(true, "Banco", new BigDecimal("1000.00"), 5, BigDecimal.ZERO),
        new Expense(false, "Cartão Nubank", new BigDecimal("1683.43"), 10, new BigDecimal("296.85")),
        new Expense(false, "Emprestimo", new BigDecimal("921.68"), 8, BigDecimal.ZERO),
        new Expense(false, "Cartão Itaú", new BigDecimal("1500.00"), 15, BigDecimal.ZERO),
        new Expense(true, "Vivo", new BigDecimal("80.00"), 21, BigDecimal.ZERO),
        new Expense(false, "Internet", new BigDecimal("150.00"), 18, BigDecimal.ZERO),
        new Expense(false, "Farmácia", new BigDecimal("100.00"), 20, BigDecimal.ZERO),
        new Expense(false, "Mercado Pago", new BigDecimal("1120.66"), 28, BigDecimal.ZERO)
    );

    public List<Expense> listExpenses() {
        return expenses;
    }

    public ExpenseSummary calculateSummary() {
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
