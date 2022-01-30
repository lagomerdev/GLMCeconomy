package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.math.BigDecimal;
import java.util.UUID;

public class BalanceTransferRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.BALANCE_TRANSFER_REQUEST;

    private final UUID fromAccountUniqueId, toAccountUniqueId;
    private final BigDecimal amount;
    private final String targetEconomyName;

    public BalanceTransferRequest(UUID fromAccountUniqueId, UUID toAccountUniqueId, BigDecimal amount, String targetEconomyName) {
        this.fromAccountUniqueId = fromAccountUniqueId;
        this.toAccountUniqueId = toAccountUniqueId;
        this.amount = amount;
        this.targetEconomyName = targetEconomyName;
    }


    public UUID getFromAccountUniqueId() {
        return fromAccountUniqueId;
    }

    public UUID getToAccountUniqueId() {
        return toAccountUniqueId;
    }

    public String getTargetEconomyName() {
        return targetEconomyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
