package pl.glmc.exchange.common;

import java.util.LinkedHashSet;
import java.util.UUID;

public class TransactionLog {
    private final UUID accountUniqueId;
    private final LinkedHashSet<Transaction> transactions;
    private final SortingMode sortingMode;
    private final OrderBy orderBy;
    private final int amount;

    public TransactionLog(UUID accountUniqueId, LinkedHashSet<Transaction> transactions, SortingMode sortingMode, OrderBy orderBy) {
        this.accountUniqueId = accountUniqueId;
        this.transactions = transactions;
        this.sortingMode = sortingMode;
        this.orderBy = orderBy;

        this.amount = transactions.size();
    }

    public UUID getAccountUniqueId() {
        return accountUniqueId;
    }

    public LinkedHashSet<Transaction> getTransactions() {
        return transactions;
    }

    public SortingMode getSortingMode() {
        return sortingMode;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public int getAmount() {
        return amount;
    }

    public enum SortingMode {
        ASC,
        DESC;
    }

    public enum OrderBy {
        CREATED_AT,
        AMOUNT;
    }
}
