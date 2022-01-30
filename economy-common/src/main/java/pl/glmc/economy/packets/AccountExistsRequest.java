package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.util.UUID;

public class AccountExistsRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.ACCOUNT_EXISTS_REQUEST;

    private final UUID accountUniqueId;

    public AccountExistsRequest(UUID accountUniqueId) {
        this.accountUniqueId = accountUniqueId;
    }

    public UUID getAccountUniqueId() {
        return accountUniqueId;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
