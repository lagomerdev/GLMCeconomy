package pl.glmc.exchange.common;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class Transaction {
    private final BigDecimal amount;
    private final int action, id;
    private final Timestamp timestamp;

    public Transaction(BigDecimal amount, int action, int id, Timestamp timestamp) {
        this.amount = amount;
        this.action = action;
        this.id = id;
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public int getAction() {
        return action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
