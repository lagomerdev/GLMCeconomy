package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.InfoPacket;
import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.config.EconomyConfig;

public class EconomyRegistered extends InfoPacket {
    private final static PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.REGISTERED;
    private final EconomyConfig economyConfig;

    public EconomyRegistered(final EconomyConfig economyConfig) {
        this.economyConfig = economyConfig;
    }

    public EconomyConfig getEconomyConfig() {
        return this.economyConfig;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
