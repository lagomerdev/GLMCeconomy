package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.config.EconomyConfig;

public class EconomyRegistrationRequest extends RequestPacket {
    private final static PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.REGISTRATION_REQUEST;

    private final EconomyConfig economyConfig;

    public EconomyRegistrationRequest(final EconomyConfig economyConfig) {
        this.economyConfig = economyConfig;
    }

    public EconomyConfig getEconomyConfig() {
        return economyConfig;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}