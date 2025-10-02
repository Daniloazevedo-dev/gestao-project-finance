package com.finance.dashboard.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Expense {
    private final boolean paid;
    private final String description;
    private final BigDecimal amount;
    private final int dueDay;
    private final BigDecimal remaining;

    public Expense(boolean paid, String description, BigDecimal amount, int dueDay, BigDecimal remaining) {
        this.paid = paid;
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.dueDay = dueDay;
        this.remaining = remaining == null ? BigDecimal.ZERO : remaining;
    }

    public boolean isPaid() {
        return paid;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getDueDay() {
        return dueDay;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }
}
