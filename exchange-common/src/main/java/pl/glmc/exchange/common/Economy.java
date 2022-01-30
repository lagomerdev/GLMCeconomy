package pl.glmc.exchange.common;

import pl.glmc.exchange.common.config.EconomyConfig;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Economy
 */
public interface Economy {

    /**
     * Caches given account and automatically refreshes
     * it's balance when receives update notification
     *
     * @param accountUUID account's unique identifier
     */
    void cacheAccount(UUID accountUUID);

    /**
     * Removes given account from cache and stops refreshing
     * it's balance
     *
     * @param accountUUID account's unique identifier
     */
    void removeFromCache(UUID accountUUID);

    /**
     * Checks if given account is currently cached
     *
     * @param accountUUID account's unique identifier
     * @return true if account is caches, false if it is not
     */
    boolean isCached(UUID accountUUID);

    /**
     *
     * @param accountUUID
     * @return
     */
    CompletableFuture<Boolean> accountExists(UUID accountUUID);

    /**
     * Gets account's balance
     *
     * @param accountUUID account's unique identifier
     * @return account's balance
     */
    CompletableFuture<BigDecimal> getBalance(UUID accountUUID);

    /**
     *
     * @param accountUUID
     * @param sortingMode
     * @param orderBy
     * @return
     */
    CompletableFuture<TransactionLog> getTransactionLog(UUID accountUUID, int limit, TransactionLog.SortingMode sortingMode, TransactionLog.OrderBy orderBy);

    /**
     *
     * @param accountUUID
     * @return
     */
    CompletableFuture<Boolean> createAccount(UUID accountUUID);

    /**
     * Adds given amount to player's balance
     *
     * @param accountUUID account's unique identifier
     * @param amount amount to be added
     * @return transaction status
     */
    CompletableFuture<Boolean> add(UUID accountUUID, BigDecimal amount);

    /**
     * Removes given amount from player's balance
     *
     * @param accountUUID account's unique identifier
     * @param amount amount to be removed
     * @return transaction status
     */
    CompletableFuture<Boolean> remove(UUID accountUUID, BigDecimal amount);

    /**
     *
     * @param accountUUID
     * @param amount
     * @return
     */
    CompletableFuture<Boolean> set(UUID accountUUID, BigDecimal amount);

    /**
     * Transfers given amount from current economy to other
     *
     * @param accountUUID account's unique identifier
     * @param amount amount to be transferred
     * @param economy other economy provider
     * @return transaction status
     */
    CompletableFuture<Boolean> transfer(UUID accountUUID, BigDecimal amount, Economy economy);

    /**
     * Transfers given amount from current economy to other
     *
     * @param accountUUID account's unique identifier
     * @param targetUUID target's account unique identifier
     * @param amount amount to be transferred
     * @param economy other economy provider
     * @return transaction status
     */
    CompletableFuture<Boolean> transfer(UUID accountUUID, UUID targetUUID, BigDecimal amount, Economy economy);

    /**
     * Gets account's balance
     * Might be null if account is not cached
     *
     * @param accountUUID account's unique identifier
     * @return current player balance
     */
    BigDecimal getCachedBalance(UUID accountUUID);

    /**
     * Gets current economy config
     *
     * @return economy config
     */
    EconomyConfig getEconomyConfig();

    /**
     * Gets EconomyType
     *
     * @return current economy type
     */
    EconomyType getEconomyType();

    /**
     *
     * @return
     */
    DecimalFormat getDecimalFormat();
}
