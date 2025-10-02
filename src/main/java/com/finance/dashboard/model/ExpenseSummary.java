package com.finance.dashboard.model;

import java.math.BigDecimal;

public class ExpenseSummary {
    private final BigDecimal totalPlanned;
    private final BigDecimal totalPaid;
    private final BigDecimal totalRemaining;

    public ExpenseSummary(BigDecimal totalPlanned, BigDecimal totalPaid, BigDecimal totalRemaining) {
        this.totalPlanned = totalPlanned;
        this.totalPaid = totalPaid;
        this.totalRemaining = totalRemaining;
    }

    public BigDecimal getTotalPlanned() {
        return totalPlanned;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }
}
