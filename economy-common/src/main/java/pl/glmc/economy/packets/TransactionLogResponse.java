package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.TransactionLog;

import java.util.UUID;

public class TransactionLogResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.TRANSACTION_LOG_RESPONSE;

    private final TransactionLog transactionLog;

    public TransactionLogResponse(boolean success, UUID originUniqueId, TransactionLog transactionLog) {
        super(success, originUniqueId);

        this.transactionLog = transactionLog;
    }

    public TransactionLog getTransactionLog() {
        return transactionLog;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
