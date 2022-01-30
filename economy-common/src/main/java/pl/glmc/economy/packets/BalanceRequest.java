package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.util.UUID;

public class BalanceRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.BALANCE_REQUEST;

    private final String economyName;
    private final UUID accountUniqueId;

    public BalanceRequest(String economyName, UUID accountUniqueId) {
        this.economyName = economyName;
        this.accountUniqueId = accountUniqueId;
    }

    public String getEconomyName() {
        return economyName;
    }

    public UUID getAccountUniqueId() {
        return accountUniqueId;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
