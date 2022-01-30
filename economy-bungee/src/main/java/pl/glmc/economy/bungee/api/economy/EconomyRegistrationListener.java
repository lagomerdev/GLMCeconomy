package pl.glmc.economy.bungee.api.economy;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.packets.EconomyRegistrationRequest;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.EconomyRegistrationResponse;

public class EconomyRegistrationListener extends PacketListener<EconomyRegistrationRequest> {
    private final GlmcEconomyBungee plugin;

    public EconomyRegistrationListener(final GlmcEconomyBungee plugin) {
        super(EconomyPacketRegistry.ECONOMY.REGISTRATION_REQUEST, EconomyRegistrationRequest.class);

        this.plugin = plugin;

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(EconomyRegistrationRequest packet) {
        boolean success;
        try {
            this.plugin.getGlmcExchangeProvider().getEconomyFactory().registerEconomy(packet.getEconomyConfig());

            success = true;
        } catch (Exception e) {
            success = false;
        }

        EconomyRegistrationResponse response = new EconomyRegistrationResponse(success, packet.getUniqueId());
        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender());
    }
}
