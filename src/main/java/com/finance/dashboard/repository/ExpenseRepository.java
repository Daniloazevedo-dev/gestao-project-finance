package com.finance.dashboard.repository;

import com.finance.dashboard.model.Expense;
import java.util.List;

public interface ExpenseRepository {
    List<Expense> findAll();

    void save(Expense expense);

    void saveAll(List<Expense> expenses);
}
