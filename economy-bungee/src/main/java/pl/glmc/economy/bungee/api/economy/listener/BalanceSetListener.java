package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.BalanceSetRequest;
import pl.glmc.economy.packets.BalanceSetResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class BalanceSetListener extends PacketListener<BalanceSetRequest> {
    private final ApiEconomyProvider economyProvider;
    private final GlmcEconomyBungee plugin;

    public BalanceSetListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_SET_REQUEST, BalanceSetRequest.class);

        this.economyProvider = economyProvider;
        this.plugin = plugin;
    }

    @Override
    public void received(BalanceSetRequest packet) {
        this.economyProvider.set(packet.getAccountUniqueId(), packet.getAmount())
                .thenAccept(success -> {
                    BalanceSetResponse response = new BalanceSetResponse(success, packet.getUniqueId());

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), this.economyProvider.getEconomyConfig().getName());
                });
    }
}
