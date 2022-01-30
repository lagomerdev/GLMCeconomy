package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.math.BigDecimal;
import java.util.UUID;

public class BalanceSetRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.BALANCE_SET_REQUEST;

    private final UUID accountUniqueId;
    private final BigDecimal amount;

    public BalanceSetRequest(UUID accountUniqueId, BigDecimal amount) {
        this.accountUniqueId = accountUniqueId;
        this.amount = amount;
    }

    public UUID getAccountUniqueId() {
        return accountUniqueId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
