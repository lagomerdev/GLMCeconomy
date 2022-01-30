package pl.glmc.economy.bukkit.api.economy;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.packets.BalanceUpdated;
import pl.glmc.economy.EconomyPacketRegistry;

public class ApiEconomyListener extends PacketListener<BalanceUpdated> {
    private final GlmcEconomyBukkit plugin;
    private final ApiEconomyProvider apiEconomyProvider;

    public ApiEconomyListener(final GlmcEconomyBukkit plugin, final ApiEconomyProvider apiEconomyProvider) {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_UPDATED, BalanceUpdated.class);

        this.apiEconomyProvider = apiEconomyProvider;
        this.plugin = plugin;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, apiEconomyProvider.getEconomyConfig().getName(), this.plugin);
    }

    @Override
    public void received(BalanceUpdated packet) { //todo add registering uuid cache
        if (this.apiEconomyProvider.isCached(packet.getAccountUniqueId())) {
            this.apiEconomyProvider.updateBalance(packet.getAccountUniqueId(), packet.getBalance());
        }
    }
}
