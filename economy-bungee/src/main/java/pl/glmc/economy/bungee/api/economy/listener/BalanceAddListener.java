package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.BalanceAddRequest;
import pl.glmc.economy.packets.BalanceAddResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class BalanceAddListener extends PacketListener<BalanceAddRequest> {
    private final ApiEconomyProvider economyProvider;
    private final GlmcEconomyBungee plugin;

    public BalanceAddListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_ADD_REQUEST, BalanceAddRequest.class);

        this.economyProvider = economyProvider;
        this.plugin = plugin;
    }

    @Override
    public void received(BalanceAddRequest packet) {
        this.economyProvider.add(packet.getAccountUniqueId(), packet.getAmount())
                .thenAccept(success -> {
                    BalanceAddResponse response = new BalanceAddResponse(success, packet.getUniqueId());

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), this.economyProvider.getEconomyConfig().getName());
                });
    }
}
