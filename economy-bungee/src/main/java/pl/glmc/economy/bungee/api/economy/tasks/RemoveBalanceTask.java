package pl.glmc.economy.bungee.api.economy.tasks;

import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RemoveBalanceTask implements Runnable {

    private final CompletableFuture<Boolean> response;
    private final ApiEconomyProvider apiEconomyProvider;
    private final UUID accountUUID;
    private final BigDecimal amount;

    public RemoveBalanceTask(CompletableFuture<Boolean> response, ApiEconomyProvider apiEconomyProvider, UUID accountUUID, BigDecimal amount) {
        this.response = response;
        this.apiEconomyProvider = apiEconomyProvider;
        this.accountUUID = accountUUID;
        this.amount = amount;
    }

    @Override
    public void run() {
        final CompletableFuture<BigDecimal> balanceFuture = this.apiEconomyProvider.getBalance(accountUUID);

        final BigDecimal balance = balanceFuture.join();
        if (balance == null) {
            response.complete(false);

            return;
        }

        final BigDecimal updated = balance.subtract(amount);
        if (updated.signum() == -1) {
            response.complete(false);

            return;
        }

        boolean success = this.apiEconomyProvider.insertLog(accountUUID, amount, 0);

        response.complete(success);
    }
}
