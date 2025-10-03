package com.finance.dashboard.controller;

import com.finance.dashboard.model.Expense;
import com.finance.dashboard.model.ExpenseRequest;
import com.finance.dashboard.model.ExpenseSummary;
import com.finance.dashboard.model.ExpenseSummaryResponse;
import com.finance.dashboard.service.FinanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
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

    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public Expense createExpense(@Valid @RequestBody ExpenseRequest request) {
        return financeService.registerExpense(request.toExpense());
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        List<Expense> expenses = financeService.listExpenses();
        ExpenseSummary summary = financeService.calculateSummary(expenses);
        ExpenseSummaryResponse response = ExpenseSummaryResponse.from(summary, expenses);

        Map<String, Object> payload = new HashMap<>();
        payload.put("summary", response);
        payload.put("expenses", expenses);
        return payload;
    }
}
