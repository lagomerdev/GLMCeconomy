package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.BalanceTransferRequest;
import pl.glmc.economy.packets.BalanceTransferResponse;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.exchange.common.Economy;

public class BalanceTransferListener extends PacketListener<BalanceTransferRequest> {
    private final GlmcEconomyBungee plugin;
    private final ApiEconomyProvider economyProvider;

    public BalanceTransferListener(GlmcEconomyBungee plugin, ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_TRANSFER_REQUEST, BalanceTransferRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(BalanceTransferRequest packet) {
        try {
            Economy targetEconomy = this.plugin.getGlmcExchangeProvider().getEconomyFactory().getEconomy(packet.getTargetEconomyName());

            this.economyProvider.transfer(packet.getFromAccountUniqueId(), packet.getToAccountUniqueId(), packet.getAmount(), targetEconomy)
                    .thenAccept(success -> {
                        BalanceTransferResponse response = new BalanceTransferResponse(success, packet.getUniqueId());

                        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), economyProvider.getEconomyConfig().getName());
                    });
        } catch (NullPointerException e) {
            BalanceTransferResponse response = new BalanceTransferResponse(false, packet.getUniqueId());

            this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), economyProvider.getEconomyConfig().getName());
        }
    }
}
