package com.finance.dashboard.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ExpenseRequest(
    @NotNull(message = "Status de pagamento é obrigatório")
    Boolean paid,

    @NotBlank(message = "Descrição é obrigatória")
    String description,

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.00", inclusive = false, message = "Valor deve ser maior que zero")
    BigDecimal amount,

    @Min(value = 1, message = "Dia de vencimento deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia de vencimento deve ser entre 1 e 31")
    int dueDay,

    @NotNull(message = "Valor restante é obrigatório")
    @DecimalMin(value = "0.00", message = "Valor restante não pode ser negativo")
    BigDecimal remaining
) {
    public Expense toExpense() {
        boolean isPaid = Boolean.TRUE.equals(paid);
        BigDecimal normalizedRemaining = isPaid ? BigDecimal.ZERO : remaining;
        return new Expense(isPaid, description, amount, dueDay, normalizedRemaining);
    }
}
