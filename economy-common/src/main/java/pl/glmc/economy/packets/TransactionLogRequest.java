package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.TransactionLog;

import java.util.UUID;

public class TransactionLogRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.TRANSACTION_LOG_REQUEST;

    private final UUID accountUniqueId;
    private final int limit;
    private final TransactionLog.SortingMode sortingMode;
    private final TransactionLog.OrderBy orderBy;

    public TransactionLogRequest(UUID accountUniqueId, int limit, TransactionLog.SortingMode sortingMode, TransactionLog.OrderBy orderBy) {
        this.accountUniqueId = accountUniqueId;
        this.limit = limit;
        this.sortingMode = sortingMode;
        this.orderBy = orderBy;
    }

    public UUID getAccountUniqueId() {
        return accountUniqueId;
    }

    public int getLimit() {
        return limit;
    }

    public TransactionLog.SortingMode getSortingMode() {
        return sortingMode;
    }

    public TransactionLog.OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
