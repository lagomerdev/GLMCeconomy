package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.packets.GetEconomiesRequest;
import pl.glmc.economy.packets.GetEconomiesResponse;

public class GetEconomiesListener extends PacketListener<GetEconomiesRequest> {
    private final GlmcEconomyBungee plugin;

    public GetEconomiesListener(final GlmcEconomyBungee plugin) {
        super(EconomyPacketRegistry.ECONOMY.GET_ECONOMIES_REQUEST, GetEconomiesRequest.class);

        this.plugin = plugin;
    }

    @Override
    public void received(GetEconomiesRequest packet) {
        var economies = this.plugin.getGlmcExchangeProvider().getEconomyFactory().getRegisteredConfigs();
        GetEconomiesResponse response = new GetEconomiesResponse(true, packet.getUniqueId(), economies);

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender());
    }
}
