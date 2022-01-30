package pl.glmc.economy.packets;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.economy.EconomyPacketRegistry;

public class GetEconomiesRequest extends RequestPacket {
    private final static PacketInfo PACKET_INFO = EconomyPacketRegistry.ECONOMY.GET_ECONOMIES_REQUEST;

    public GetEconomiesRequest() {
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
