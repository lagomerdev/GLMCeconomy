package pl.glmc.economy.bukkit.api.economy.listener;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.api.common.packet.listener.ResponseHandlerListener;

public class EconomyListener<T extends ResponsePacket> extends ResponseHandlerListener<T, Boolean> {
    public EconomyListener(PacketInfo packetInfo, Class<T> packetClass) {
        super(packetInfo, packetClass);
    }

    @Override
    public void received(T packet) {
        this.complete(packet.getOriginUniqueId(), packet.isSuccess());
    }
}
