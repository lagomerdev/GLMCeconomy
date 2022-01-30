package pl.glmc.economy.bungee.api.economy.tasks;

import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CreateAccountTask implements Runnable {

    private final CompletableFuture<Boolean> response;
    private final ApiEconomyProvider apiEconomyProvider;
    private final UUID accountUUID;

    public CreateAccountTask(CompletableFuture<Boolean> response, ApiEconomyProvider apiEconomyProvider, UUID accountUUID) {
        this.response = response;
        this.apiEconomyProvider = apiEconomyProvider;
        this.accountUUID = accountUUID;
    }

    @Override
    public void run() {
        boolean registered = this.apiEconomyProvider.getRegisteredAccounts().contains(accountUUID);

        if (registered) {
            this.response.complete(false);

            return;
        }

        boolean success = this.apiEconomyProvider.insertAccount(accountUUID);

        this.response.complete(success);
    }
}
