package com.finance.dashboard.repository;

import com.finance.dashboard.model.Expense;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CsvExpenseRepository implements ExpenseRepository {

    private static final String[] HEADER = {"paid", "description", "amount", "dueDay", "remaining"};
    private final Path csvPath;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CsvExpenseRepository() {
        this(Paths.get("data", "expenses.csv"));
    }

    CsvExpenseRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @PostConstruct
    void initializeRepository() {
        lock.writeLock().lock();
        try {
            Path parent = csvPath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(csvPath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
                    writer.write(String.join(",", HEADER));
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize expenses CSV file", exception);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Expense> findAll() {
        lock.readLock().lock();
        try {
            return readAllInternal().stream()
                .sorted(Comparator.comparingInt(Expense::getDueDay))
                .collect(Collectors.toUnmodifiableList());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read expenses from CSV file", exception);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Expense expense) {
        Objects.requireNonNull(expense, "expense must not be null");
        lock.writeLock().lock();
        try {
            List<Expense> expenses = readAllInternal();
            expenses.add(expense);
            writeAll(expenses);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save expense to CSV file", exception);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void saveAll(List<Expense> expenses) {
        Objects.requireNonNull(expenses, "expenses must not be null");
        lock.writeLock().lock();
        try {
            writeAll(expenses);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist expenses to CSV file", exception);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Expense toExpense(String line) {
        List<String> columns = parseCsvLine(line);
        if (columns.size() != HEADER.length) {
            throw new IllegalStateException("Invalid expense line: " + line);
        }
        boolean paid = Boolean.parseBoolean(columns.get(0));
        String description = columns.get(1);
        BigDecimal amount = new BigDecimal(columns.get(2));
        int dueDay = Integer.parseInt(columns.get(3));
        BigDecimal remaining = new BigDecimal(columns.get(4));
        return new Expense(paid, description, amount, dueDay, remaining);
    }

    private void writeAll(List<Expense> expenses) throws IOException {
        List<Expense> sorted = expenses.stream()
            .sorted(Comparator.comparingInt(Expense::getDueDay).thenComparing(Expense::getDescription))
            .toList();
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            writer.write(String.join(",", HEADER));
            writer.newLine();
            for (Expense expense : sorted) {
                writer.write(formatExpense(expense));
                writer.newLine();
            }
        }
    }

    private List<Expense> readAllInternal() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            return reader.lines()
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(this::toExpense)
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private String formatExpense(Expense expense) {
        return String.join(",",
            Boolean.toString(expense.isPaid()),
            escape(expense.getDescription()),
            expense.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
            Integer.toString(expense.getDueDay()),
            expense.getRemaining().setScale(2, RoundingMode.HALF_UP).toPlainString()
        );
    }

    private String escape(String value) {
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }
}
