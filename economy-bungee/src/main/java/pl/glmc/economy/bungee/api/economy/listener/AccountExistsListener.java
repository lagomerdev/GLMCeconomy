package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.AccountExistsRequest;
import pl.glmc.economy.packets.AccountExistsResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class AccountExistsListener extends PacketListener<AccountExistsRequest> {
    private final GlmcEconomyBungee plugin;
    private final ApiEconomyProvider economyProvider;

    public AccountExistsListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.ACCOUNT_EXISTS_REQUEST, AccountExistsRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(AccountExistsRequest packet) {
        this.economyProvider.accountExists(packet.getAccountUniqueId())
                .thenAccept(success -> {
                    AccountExistsResponse response = new AccountExistsResponse(success, packet.getUniqueId());

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), this.economyProvider.getEconomyConfig().getName());
                });
    }
}
