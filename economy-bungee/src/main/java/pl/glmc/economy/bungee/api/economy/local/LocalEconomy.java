package pl.glmc.economy.bungee.api.economy.local;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyType;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.util.UUID;

public class LocalEconomy {
    private final GlmcEconomyBungee plugin;

    private final Economy playerBankEconomy;

    public LocalEconomy(GlmcEconomyBungee plugin) {
        this.plugin = plugin;

        EconomyConfig playerBankConfig = new EconomyConfig("bank", "G€", EconomyType.BANK);
        this.playerBankEconomy = this.plugin.getGlmcExchangeProvider().getEconomyFactory().loadEconomy(playerBankConfig);

        LocalEconomyCacheListener playerBankCacheListener = new LocalEconomyCacheListener(this.playerBankEconomy);
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, playerBankCacheListener);


        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            for (ProxiedPlayer onlinePlayer : this.plugin.getProxy().getPlayers()) {
                UUID playerUUID = onlinePlayer.getUniqueId();

                this.playerBankEconomy.cacheAccount(playerUUID);
            }
        });
    }

    public Economy getPlayerBankEconomy() {
        return playerBankEconomy;
    }
}