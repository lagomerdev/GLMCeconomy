package pl.glmc.economy.bukkit.api.economy;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.bukkit.api.economy.listener.BalanceInfoHandler;
import pl.glmc.economy.bukkit.api.economy.listener.EconomyListener;
import pl.glmc.economy.bukkit.api.economy.listener.TransactionLogHandler;
import pl.glmc.economy.packets.*;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyType;
import pl.glmc.exchange.common.TransactionLog;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ApiEconomyProvider implements Economy {
    private final GlmcEconomyBukkit plugin;
    private final EconomyConfig economyConfig;
    private final ConcurrentHashMap<UUID, BigDecimal> accountsCache;

    private final EconomyListener<AccountCreateResponse> accountCreateHandler;
    private final EconomyListener<AccountExistsResponse> accountExistsHandler;
    private final EconomyListener<BalanceAddResponse> balanceAddHandler;
    private final EconomyListener<BalanceRemoveResponse> balanceRemoveHandler;
    private final EconomyListener<BalanceTransferResponse> balanceTransferHandler;
    private final EconomyListener<BalanceSetResponse> balanceSetHandler;

    private final BalanceInfoHandler balanceInfoHandler;
    private final TransactionLogHandler transactionLogHandler;

    private final DecimalFormat decimalFormat;

    public ApiEconomyProvider(final GlmcEconomyBukkit plugin, final EconomyConfig economyConfig) {
        this.plugin = plugin;
        this.economyConfig = economyConfig;

        this.accountsCache = new ConcurrentHashMap<>();

        this.accountCreateHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.ACCOUNT_CREATE_RESPONSE, AccountCreateResponse.class);
        this.accountExistsHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.ACCOUNT_EXISTS_RESPONSE, AccountExistsResponse.class);
        this.balanceAddHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.BALANCE_ADD_RESPONSE, BalanceAddResponse.class);
        this.balanceRemoveHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.BALANCE_REMOVE_RESPONSE, BalanceRemoveResponse.class);
        this.balanceTransferHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.BALANCE_TRANSFER_RESPONSE, BalanceTransferResponse.class);
        this.balanceSetHandler = new EconomyListener<>(EconomyPacketRegistry.ECONOMY.BALANCE_SET_RESPONSE, BalanceSetResponse.class);

        this.balanceInfoHandler = new BalanceInfoHandler();
        this.transactionLogHandler = new TransactionLogHandler();

        this.decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pl_PL"));
        this.decimalFormat.applyPattern("###,###.## " + economyConfig.getCurrencySign());
    }

    public void load() {
        ApiEconomyListener apiEconomyListener = new ApiEconomyListener(this.plugin, this);

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.accountCreateHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.accountExistsHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.balanceAddHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.balanceRemoveHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.balanceTransferHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.balanceSetHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.balanceInfoHandler, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this.transactionLogHandler, this.economyConfig.getName(), this.plugin);
    }

    public void updateBalance(UUID accountUUID, BigDecimal balance) {
        this.accountsCache.put(accountUUID, balance);

        this.plugin.getLogger().info(ChatColor.GREEN + "Updated economy account balance " + accountUUID + " in economy " + economyConfig.getName());
    }

    @Override
    public void cacheAccount(UUID accountUUID) {
        BalanceRequest request = new BalanceRequest(this.economyConfig.getName(), accountUUID);

        this.balanceInfoHandler.create(request.getUniqueId())
                .thenAccept(amount -> {
                    if (amount != null) {
                        this.accountsCache.put(accountUUID, amount);

                        this.plugin.getLogger().info(ChatColor.GREEN + "Successfully cached economy account " + accountUUID + " in economy " + this.economyConfig.getName());
                    } else {
                        this.plugin.getLogger().info(ChatColor.RED + "Failed to cache economy account " + accountUUID.toString() + " in economy " + this.economyConfig.getName());
                    }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());
    }

    @Override
    public void removeFromCache(UUID accountUUID) {
        this.accountsCache.remove(accountUUID);
    }

    @Override
    public boolean isCached(UUID accountUUID) {
        return this.accountsCache.containsKey(accountUUID);
    }

    @Override
    public CompletableFuture<Boolean> accountExists(UUID accountUUID) {
        final CompletableFuture<Boolean> exists;

        if (this.accountsCache.containsKey(accountUUID)) {
            exists = new CompletableFuture<>();

            exists.complete(true);
        } else {
            final AccountExistsRequest request = new AccountExistsRequest(accountUUID);

            exists = this.accountExistsHandler.create(request.getUniqueId());

            this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());
        }

        exists.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Account " + accountUUID.toString() + " exists on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Account " + accountUUID.toString() + " does not exist on economy " + this.economyConfig.getName());
            }
        });


        return exists;
    }

    @Override
    public CompletableFuture<BigDecimal> getBalance(UUID accountUUID) {
        final CompletableFuture<BigDecimal> result;

        final BigDecimal balance = this.accountsCache.get(accountUUID);

        if (balance == null) {
            BalanceRequest request = new BalanceRequest(this.economyConfig.getName(), accountUUID);

            result = this.balanceInfoHandler.create(request.getUniqueId());

            this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());
        } else {
            result = new CompletableFuture<>();

            result.complete(balance);
        }

        return result;
    }

    @Override
    public CompletableFuture<TransactionLog> getTransactionLog(UUID accountUUID, int limit, TransactionLog.SortingMode sortingMode, TransactionLog.OrderBy orderBy) {
        final TransactionLogRequest request = new TransactionLogRequest(accountUUID, limit, sortingMode, orderBy);

        final CompletableFuture<TransactionLog> result = this.transactionLogHandler.create(request.getUniqueId());
        result.thenAccept(transactionLog -> {
            if (transactionLog != null) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully received transaction log from account " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to receive transaction log from account " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return result;
    }

    @Override
    public CompletableFuture<Boolean> createAccount(UUID accountUUID) {
        AccountCreateRequest request = new AccountCreateRequest(accountUUID);

        CompletableFuture<Boolean> create = this.accountCreateHandler.create(request.getUniqueId());
        create.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully created account " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to create account " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return create;
    }

    @Override
    public CompletableFuture<Boolean> add(UUID accountUUID, BigDecimal amount) {
        BalanceAddRequest request = new BalanceAddRequest(accountUUID, amount);

        CompletableFuture<Boolean> add = this.balanceAddHandler.create(request.getUniqueId());
        add.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully added " + amount.toString() + " to " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to add " + amount.toString() + " to " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return add;
    }

    @Override
    public CompletableFuture<Boolean> remove(UUID accountUUID, BigDecimal amount) {
        BalanceRemoveRequest request = new BalanceRemoveRequest(accountUUID, amount);

        CompletableFuture<Boolean> remove = this.balanceRemoveHandler.create(request.getUniqueId());
        remove.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully removed " + amount.toString() + " from " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to remove " + amount.toString() + " from " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return remove;
    }

    @Override
    public CompletableFuture<Boolean> set(UUID accountUUID, BigDecimal amount) {
        BalanceSetRequest request = new BalanceSetRequest(accountUUID, amount);

        CompletableFuture<Boolean> set = this.balanceSetHandler.create(request.getUniqueId());
        set.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully set balance to " + amount.toString() + " on " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to set balance to " + amount.toString() + " on " + accountUUID.toString() + " on economy " + this.economyConfig.getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return set;
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID accountUUID, BigDecimal amount, Economy economy) {
        return this.transfer(accountUUID, accountUUID, amount, economy);
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID accountUUID, UUID targetUUID, BigDecimal amount, Economy economy) {
        BalanceTransferRequest request = new BalanceTransferRequest(accountUUID, targetUUID, amount, economy.getEconomyConfig().getName());

        CompletableFuture<Boolean> transfer = this.balanceTransferHandler.create(request.getUniqueId());
        transfer.thenAccept(success -> {
            if (success) {
                this.plugin.getLogger().info(ChatColor.GREEN + "Successfully transferred " + amount.toString() + " from " + accountUUID.toString() + " on economy " + this.economyConfig.getName() + " to " + targetUUID.toString() + " on economy " + economy.getEconomyConfig().getName());
            } else {
                this.plugin.getLogger().info(ChatColor.RED + "Failed to transfer " + amount.toString() + " from " + accountUUID.toString() + " on economy " + this.economyConfig.getName() + " to " + targetUUID.toString() + " on economy " + economy.getEconomyConfig().getName());
            }
        });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy", this.economyConfig.getName());

        return transfer;
    }

    @Override
    public BigDecimal getCachedBalance(UUID accountUUID) {
        return this.accountsCache.getOrDefault(accountUUID, null);
    }

    @Override
    public EconomyConfig getEconomyConfig() {
        return this.economyConfig;
    }

    @Override
    public EconomyType getEconomyType() {
        return this.economyConfig.getEconomyType();
    }

    @Override
    public DecimalFormat getDecimalFormat() {
        return this.decimalFormat;
    }
}
