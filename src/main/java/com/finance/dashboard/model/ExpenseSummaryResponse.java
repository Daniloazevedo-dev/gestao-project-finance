package com.finance.dashboard.model;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseSummaryResponse(
    BigDecimal totalPlanned,
    BigDecimal totalPaid,
    BigDecimal totalRemaining,
    List<Expense> expenses
) {
    public static ExpenseSummaryResponse from(ExpenseSummary summary, List<Expense> expenses) {
        return new ExpenseSummaryResponse(
            summary.getTotalPlanned(),
            summary.getTotalPaid(),
            summary.getTotalRemaining(),
            List.copyOf(expenses)
        );
    }
}
