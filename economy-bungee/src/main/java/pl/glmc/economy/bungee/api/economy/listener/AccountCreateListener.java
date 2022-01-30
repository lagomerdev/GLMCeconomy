package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.packets.AccountCreateRequest;
import pl.glmc.economy.packets.AccountCreateResponse;
import pl.glmc.economy.EconomyPacketRegistry;

public class AccountCreateListener extends PacketListener<AccountCreateRequest> {
    private final GlmcEconomyBungee plugin;
    private final ApiEconomyProvider economyProvider;

    public AccountCreateListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.ACCOUNT_CREATE_REQUEST, AccountCreateRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(AccountCreateRequest packet) {
        this.economyProvider.createAccount(packet.getAccountUniqueId())
                .thenAccept(success -> {
                    AccountCreateResponse response = new AccountCreateResponse(success, packet.getUniqueId());

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket( response, packet.getSender(), this.economyProvider.getEconomyConfig().getName());
                });

    }
}
