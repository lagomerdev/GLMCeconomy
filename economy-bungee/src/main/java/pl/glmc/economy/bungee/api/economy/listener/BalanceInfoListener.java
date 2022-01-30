package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.BalanceRequest;
import pl.glmc.economy.packets.BalanceResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class BalanceInfoListener extends PacketListener<BalanceRequest> {
    private final ApiEconomyProvider economyProvider;
    private final GlmcEconomyBungee plugin;

    public BalanceInfoListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_REQUEST, BalanceRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(BalanceRequest packet) {
        this.economyProvider.getBalance(packet.getAccountUniqueId())
                .thenAccept(balance -> {
                    boolean success = balance != null;

                    BalanceResponse response = new BalanceResponse(success, packet.getUniqueId(), balance);

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), economyProvider.getEconomyConfig().getName());
                });
    }
}
