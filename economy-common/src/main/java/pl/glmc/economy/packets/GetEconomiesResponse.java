package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.util.HashSet;
import java.util.UUID;

public class GetEconomiesResponse extends ResponsePacket {
    private final static PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.GET_ECONOMIES_RESPONSE;

    private final HashSet<EconomyConfig> registeredEconomies;

    public GetEconomiesResponse(boolean success, UUID originUniqueId, HashSet<EconomyConfig> registeredEconomies) {
        super(success, originUniqueId);

        this.registeredEconomies = registeredEconomies;
    }

    public HashSet<EconomyConfig> getRegisteredEconomies() {
        return registeredEconomies;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
