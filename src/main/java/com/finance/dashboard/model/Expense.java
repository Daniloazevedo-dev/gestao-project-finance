package com.finance.dashboard.model;

import java.math.BigDecimal;

public class Expense {
    private final boolean paid;
    private final String description;
    private final BigDecimal amount;
    private final int dueDay;
    private final BigDecimal remaining;

    public Expense(boolean paid, String description, BigDecimal amount, int dueDay, BigDecimal remaining) {
        this.paid = paid;
        this.description = description;
        this.amount = amount;
        this.dueDay = dueDay;
        this.remaining = remaining;
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
