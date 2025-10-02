package com.finance.dashboard.controller;

import com.finance.dashboard.model.Expense;
import com.finance.dashboard.model.ExpenseSummary;
import com.finance.dashboard.service.FinanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        return financeService.listExpenses();
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        ExpenseSummary summary = financeService.calculateSummary();
        Map<String, Object> payload = new HashMap<>();
        payload.put("summary", summary);
        payload.put("expenses", financeService.listExpenses());
        return payload;
    }
}
