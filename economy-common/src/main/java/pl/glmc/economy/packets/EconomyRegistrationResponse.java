package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.economy.EconomyPacketRegistry;

import java.util.UUID;

public class EconomyRegistrationResponse extends ResponsePacket {
    private final static PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.REGISTRATION_RESPONSE;

    public EconomyRegistrationResponse(final boolean success, final UUID originUniqueId) {
        super(success, originUniqueId);
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}