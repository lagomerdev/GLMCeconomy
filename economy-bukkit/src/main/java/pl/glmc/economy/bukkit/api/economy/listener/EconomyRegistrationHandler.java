package pl.glmc.economy.bukkit.api.economy.listener;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.EconomyRegistrationResponse;

public class EconomyRegistrationHandler extends ResponseHandlerListener<EconomyRegistrationResponse, Boolean> {
    private final GlmcEconomyBukkit plugin;

    public EconomyRegistrationHandler(final GlmcEconomyBukkit plugin) {
        super(EconomyPacketRegistry.ECONOMY.REGISTRATION_RESPONSE, EconomyRegistrationResponse.class);

        this.plugin = plugin;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(EconomyRegistrationResponse packet) {
        this.complete(packet.getOriginUniqueId(), packet.isSuccess());
    }
}
