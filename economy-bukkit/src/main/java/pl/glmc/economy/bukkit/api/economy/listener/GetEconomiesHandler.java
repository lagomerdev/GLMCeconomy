package pl.glmc.economy.bukkit.api.economy.listener;


import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.GetEconomiesResponse;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.util.HashSet;

public class GetEconomiesHandler extends ResponseHandlerListener<GetEconomiesResponse, HashSet<EconomyConfig>> {
    public GetEconomiesHandler() {
        super(EconomyPacketRegistry.ECONOMY.GET_ECONOMIES_RESPONSE, GetEconomiesResponse.class);
    }

    @Override
    public void received(GetEconomiesResponse packet) {
        this.complete(packet.getOriginUniqueId(), packet.getRegisteredEconomies());
    }
}
