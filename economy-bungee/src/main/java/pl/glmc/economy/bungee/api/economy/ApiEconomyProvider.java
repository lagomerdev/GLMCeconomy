package pl.glmc.economy.bungee.api.economy;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.tasks.AddBalanceTask;
import pl.glmc.economy.bungee.api.economy.tasks.CreateAccountTask;
import pl.glmc.economy.bungee.api.economy.tasks.RemoveBalanceTask;
import pl.glmc.economy.bungee.api.economy.tasks.SetBalanceTask;
import pl.glmc.economy.bungee.api.economy.listener.*;
import pl.glmc.economy.packets.BalanceUpdated;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyType;
import pl.glmc.exchange.common.Transaction;
import pl.glmc.exchange.common.TransactionLog;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;

public class ApiEconomyProvider implements Economy {
    private String insertLogStatement, createAccountStatement, getTransactionLogStatement;

    private final GlmcEconomyBungee plugin;
    private final EconomyConfig economyConfig;
    private final ConcurrentHashMap<UUID, BigDecimal> accountsCache, allAccountsCache;
    private final ExecutorService economyTasksExecutor;

    private final AccountCreateListener accountCreateListener;
    private final AccountExistsListener accountExistsListener;
    private final BalanceInfoListener balanceInfoListener;
    private final BalanceAddListener balanceAddListener;
    private final BalanceRemoveListener balanceRemoveListener;
    private final BalanceTransferListener balanceTransferListener;
    private final BalanceSetListener balanceSetListener;
    private final TransactionLogListener transactionLogListener;

    private final DecimalFormat decimalFormat;

    public ApiEconomyProvider(GlmcEconomyBungee plugin, EconomyConfig economyConfig) {
        this.plugin = plugin;
        this.economyConfig = economyConfig;

        this.accountsCache = new ConcurrentHashMap<>();
        this.allAccountsCache = new ConcurrentHashMap<>();
        this.economyTasksExecutor = Executors.newSingleThreadExecutor();

        this.accountCreateListener = new AccountCreateListener(this.plugin, this);
        this.accountExistsListener = new AccountExistsListener(this.plugin, this);
        this.balanceInfoListener = new BalanceInfoListener(this.plugin, this);
        this.balanceAddListener = new BalanceAddListener(this.plugin, this);
        this.balanceRemoveListener = new BalanceRemoveListener(this.plugin, this);
        this.balanceTransferListener = new BalanceTransferListener(this.plugin, this);
        this.balanceSetListener = new BalanceSetListener(this.plugin, this);
        this.transactionLogListener = new TransactionLogListener(this.plugin, this);

        this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.forLanguageTag("pl_PL"));
        this.decimalFormat.applyLocalizedPattern("###,###.## " + economyConfig.getCurrencySign());

        this.plugin.getLogger().info(ChatColor.GREEN + "Created Economy " + economyConfig.getName());
    }

    public void register() {
        final String economyDataTableName = "economy_" + this.economyConfig.getName() + "_data";
        final String economyLogsTableName = "economy_" + this.economyConfig.getName() + "_logs";
        final String getAllAccountsStatement = "SELECT `uuid`, `balance` FROM `" + economyDataTableName + "`";

        this.insertLogStatement =  "INSERT INTO `" + economyLogsTableName  + "` (`account_uuid`, `amount`, `action`) VALUES (?, ?, ?)";
        this.createAccountStatement = "INSERT INTO `" + economyDataTableName + "` (`uuid`, `balance`, `active`) VALUES (?, 0, ?)";
        this.getTransactionLogStatement = "SELECT * FROM `" + economyLogsTableName + "` WHERE `account_uuid` = ?";

        final String economyDataTable = "CREATE TABLE IF NOT EXISTS `" + economyDataTableName + "` ( " +
                " `uuid` char(36) NOT NULL, " +
                " `balance` decimal(15,2) NOT NULL, " +
                " `active` tinyint(1) NOT NULL, " +
                " UNIQUE KEY `uuid` (`uuid`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        this.plugin.getDatabaseProvider().updateSync(economyDataTable);

        final String economyLogsTable = "CREATE TABLE IF NOT EXISTS `" + economyLogsTableName + "` ( " +
                " `id` int(10) unsigned NOT NULL AUTO_INCREMENT, " +
                " `account_uuid` char(36) NOT NULL, " +
                " `amount` decimal(12,2) unsigned NOT NULL, " +
                " `action` tinyint(3) unsigned NOT NULL, " +
                " `created_at` timestamp NOT NULL DEFAULT current_timestamp(), " +
                " PRIMARY KEY (`id`), " +
                " KEY `account_uuid` (`account_uuid`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        this.plugin.getDatabaseProvider().updateSync(economyLogsTable);

        final String economyDataUpdateTrigger = "CREATE DEFINER=root@localhost " +
                "TRIGGER update_economy_" + this.economyConfig.getName() + "_data_update " +
                "BEFORE INSERT ON " + economyLogsTableName + " " +
                "FOR EACH ROW " +
                "BEGIN " +
                "IF (new.action = 1) THEN " +
                "UPDATE `" + economyDataTableName + "` SET `balance` = balance + new.amount WHERE uuid = new.account_uuid; " +
                "ELSEIF (new.action = 0) THEN  " +
                "UPDATE `" + economyDataTableName + "` SET `balance` = balance - new.amount WHERE uuid = new.account_uuid; " +
                "ELSE " +
                "UPDATE `" + economyDataTableName + "` SET `balance` = new.amount WHERE uuid = new.account_uuid; " +
                "END IF; " +
                "END";

        final String triggerFix = "DROP TRIGGER IF EXISTS " + "update_economy_" + this.economyConfig.getName() + "_data_update";
        this.plugin.getDatabaseProvider().updateSync(triggerFix);
        this.plugin.getDatabaseProvider().updateSync(economyDataUpdateTrigger);

        ResultSet rs = this.plugin.getDatabaseProvider().getSync(getAllAccountsStatement);

        try {
            int accounts = 0;
            while (rs.next()) {
                UUID accountUUID = UUID.fromString(rs.getString("uuid"));
                BigDecimal balance = rs.getBigDecimal("balance");

                this.allAccountsCache.put(accountUUID, balance);

                accounts++;
            }

            this.plugin.getLogger().info(ChatColor.GREEN + "Loaded " + accounts + " accounts on economy " + this.economyConfig.getName());
        } catch (SQLException e) {
            e.printStackTrace();

            this.plugin.getLogger().warning(ChatColor.RED + "Failed to load all accounts from economy " + this.economyConfig.getName());
        }

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.accountCreateListener, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.accountExistsListener, this.economyConfig.getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.balanceInfoListener, this.getEconomyConfig().getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.balanceAddListener, this.getEconomyConfig().getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.balanceRemoveListener, this.getEconomyConfig().getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.balanceTransferListener, this.getEconomyConfig().getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.balanceSetListener, this.getEconomyConfig().getName(), this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.transactionLogListener, this.getEconomyConfig().getName(), this.plugin);
    }

    public boolean insertLog(UUID accountUUID, BigDecimal amount, int action) {
        boolean success = this.plugin.getDatabaseProvider().updateSync(insertLogStatement, accountUUID.toString(), amount, action);

        if (success) {
            final BigDecimal current = this.getBalance(accountUUID).join();

            final BigDecimal update;
            if (action == 0) {
                update = current.subtract(amount).setScale(2, RoundingMode.HALF_UP);
            } else if (action == 1) {
                update = current.add(amount).setScale(2, RoundingMode.HALF_UP);
            } else if (action == 2) {
                update = amount.setScale(2, RoundingMode.HALF_UP);
            } else {
                update = current.setScale(2, RoundingMode.HALF_UP);
            }

            this.accountsCache.replace(accountUUID, update);

            this.allAccountsCache.put(accountUUID, update);

            BalanceUpdated packet = new BalanceUpdated(accountUUID, update);

            this.plugin.getGlmcApiBungee().getPacketService().sendPacket(packet, "all", this.economyConfig.getName());
        }

        return success;
    }

    public boolean insertAccount(UUID accountUUID) {
        boolean success = this.plugin.getDatabaseProvider().updateSync(createAccountStatement, accountUUID.toString(), true);

        if (success) {
            this.allAccountsCache.put(accountUUID, BigDecimal.ZERO);
        }

        return success;
    }

    public ConcurrentHashMap<UUID, BigDecimal> getRegisteredAccounts() {
        return this.allAccountsCache;
    }

    @Override
    public void cacheAccount(UUID accountUUID) {
        BigDecimal balance = this.allAccountsCache.get(accountUUID);

        if (balance != null) {
            this.accountsCache.put(accountUUID, balance);
        } else {
            this.createAccount(accountUUID)
                    .thenAccept(success -> {
                        if (success) {
                            this.accountsCache.put(accountUUID, BigDecimal.ZERO);
                        } else {
                            this.plugin.getLogger().info(ChatColor.RED + "Failed to create new account " + accountUUID + " while saving to cache");
                        }
                    });
        }
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
        CompletableFuture<Boolean> exists = new CompletableFuture<>();

        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            boolean result = this.allAccountsCache.containsKey(accountUUID);

            exists.complete(result);
        });

        return exists;
    }

    @Override
    public CompletableFuture<BigDecimal> getBalance(UUID accountUUID) {
        final CompletableFuture<BigDecimal> result = new CompletableFuture<>();

        final BigDecimal cachedBalance = this.accountsCache.get(accountUUID);

        if (cachedBalance == null) {
            this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
                final BigDecimal balance = this.allAccountsCache.get(accountUUID);

                result.complete(balance);
            });
        } else {
            result.complete(cachedBalance);
        }

        return result;
    }

    @Override
    public CompletableFuture<TransactionLog> getTransactionLog(UUID accountUUID, int limit, TransactionLog.SortingMode sortingMode, TransactionLog.OrderBy orderBy) {
        final CompletableFuture<TransactionLog> result = new CompletableFuture<>();

        this.plugin.getDatabaseProvider().getAsync((rs) -> {
            try {
                LinkedHashSet<Transaction> transactions = new LinkedHashSet<>();

                while (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal("amount");
                    int action = rs.getInt("action");
                    int id = rs.getInt("id");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    Transaction transaction = new Transaction(amount, action, id, createdAt);

                    transactions.add(transaction);
                }

                TransactionLog transactionLog = new TransactionLog(accountUUID, transactions, sortingMode, orderBy);

                result.complete(transactionLog);
            } catch (SQLException e) {
                e.printStackTrace();

                result.complete(null);
            }
        }, this.getTransactionLogStatement + " ORDER BY " + orderBy.toString().toLowerCase() + " " + sortingMode.toString() + " LIMIT ?", accountUUID.toString(), limit);

        return result;
    }

    @Override
    public CompletableFuture<Boolean> createAccount(UUID accountUUID) {
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final CreateAccountTask createAccountTask = new CreateAccountTask(result, this, accountUUID);

        this.economyTasksExecutor.submit(createAccountTask);

        return result;
    }

    @Override
    public CompletableFuture<Boolean> add(UUID accountUUID, BigDecimal amount) {
        if (amount.signum() != 1) {
            throw new IllegalArgumentException("Specified amount must be greater than zero!");
        }

        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final AddBalanceTask addBalanceTask = new AddBalanceTask(result, this, accountUUID, amount);

        this.economyTasksExecutor.submit(addBalanceTask);

        return result;
    }

    @Override
    public CompletableFuture<Boolean> remove(UUID accountUUID, BigDecimal amount) {
        if (amount.signum() != 1) {
            throw new IllegalArgumentException("Specified amount must be greater than zero!");
        }

        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final RemoveBalanceTask removeBalanceTask = new RemoveBalanceTask(result, this, accountUUID, amount);

        this.economyTasksExecutor.submit(removeBalanceTask);

        return result;
    }

    @Override
    public CompletableFuture<Boolean> set(UUID accountUUID, BigDecimal amount) {
        if (amount.signum() == -1) {
            throw new IllegalArgumentException("Specified amount must be greater than or equal zero!");
        }

        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final SetBalanceTask setBalanceTask = new SetBalanceTask(result, this, accountUUID, amount);

        this.economyTasksExecutor.submit(setBalanceTask);

        return result;
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID accountUUID, BigDecimal amount, Economy economy) {
        return this.transfer(accountUUID, accountUUID, amount, economy);
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID accountUUID, UUID targetUUID, BigDecimal amount, Economy economy) {
        if (amount.signum() != 1) {
            throw new IllegalArgumentException("Specified amount must be greater than zero!");
        }

        final CompletableFuture<Boolean> response = new CompletableFuture<>();

        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            final CompletableFuture<Boolean> removed = this.remove(accountUUID, amount);
            if (!removed.join()) {
                response.complete(false);

                return;
            }

            final CompletableFuture<Boolean> added = economy.add(targetUUID, amount);
            if (added.join()) {
                response.complete(true);
            } else {
                final CompletableFuture<Boolean> returned = this.add(accountUUID, amount);

                if (!returned.join()) {
                    throw new RuntimeException("Failed to transfer amount back after failed adding in other economy!");
                } else {
                    response.complete(true);
                }
            }
        });

        return response;
    }

    @Override
    public BigDecimal getCachedBalance(UUID accountUUID) {
        final BigDecimal balance = this.accountsCache.get(accountUUID);
        if (balance == null) {
            throw new IllegalArgumentException("This account does not exist/is unloaded!");
        }

        return balance;
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
