package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.BalanceRemoveRequest;
import pl.glmc.economy.packets.BalanceRemoveResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class BalanceRemoveListener extends PacketListener<BalanceRemoveRequest> {
    private final GlmcEconomyBungee plugin;
    private final ApiEconomyProvider economyProvider;

    public BalanceRemoveListener(GlmcEconomyBungee plugin, ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_REMOVE_REQUEST, BalanceRemoveRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(BalanceRemoveRequest packet) {
        this.economyProvider.remove(packet.getAccountUniqueId(), packet.getAmount())
                .thenAccept(success -> {
                    BalanceRemoveResponse response = new BalanceRemoveResponse(success, packet.getUniqueId());

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), this.economyProvider.getEconomyConfig().getName());
                });
    }
}
