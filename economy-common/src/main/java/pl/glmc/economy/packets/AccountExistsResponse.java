package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.util.UUID;

public class AccountExistsResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.ACCOUNT_EXISTS_RESPONSE;

    public AccountExistsResponse(boolean success, UUID originUniqueId) {
        super(success, originUniqueId);
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
