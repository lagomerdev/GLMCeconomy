package pl.glmc.economy.bukkit.api.economy.local;

import org.bukkit.entity.Player;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.bukkit.api.economy.hook.ValutEconomy;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyListener;
import pl.glmc.exchange.common.EconomyType;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.util.UUID;

public class LocalEconomy implements EconomyListener {
    private final GlmcEconomyBukkit plugin;

    private Economy playerBankEconomy, playerCashEconomy;

    public LocalEconomy(GlmcEconomyBukkit plugin) {
        this.plugin = plugin;

        this.plugin.getGlmcExchangeProvider().getEconomyFactory().registerListener(this);


        EconomyConfig playerBankConfig = new EconomyConfig("bank", "G€", EconomyType.BANK);
        this.playerBankEconomy = this.plugin.getGlmcExchangeProvider().getEconomyFactory().loadEconomy(playerBankConfig);

        EconomyConfig playerCashConfig = new EconomyConfig("cash", "G€", EconomyType.CASH);
        this.playerCashEconomy = this.plugin.getGlmcExchangeProvider().getEconomyFactory().loadEconomy(playerCashConfig);
    }

    public Economy getPlayerBankEconomy() {
        return this.playerBankEconomy;
    }

    public Economy getPlayerCashEconomy() {
        return this.playerCashEconomy;
    }

    @Override
    public void loaded(Economy economy) {
        if (economy.getEconomyConfig().getName().equals("bank")) {
            this.playerBankEconomy = economy;

            LocalEconomyCacheListener playerBankCacheListener = new LocalEconomyCacheListener(this.playerBankEconomy);
            this.plugin.getServer().getPluginManager().registerEvents(playerBankCacheListener, this.plugin);

            this.cacheAll(economy);
        } else if (economy.getEconomyConfig().getName().equals("cash")) {
            this.playerCashEconomy = economy;

            LocalEconomyCacheListener playerCashCacheListener = new LocalEconomyCacheListener(this.playerCashEconomy);
            this.plugin.getServer().getPluginManager().registerEvents(playerCashCacheListener, this.plugin);

            this.cacheAll(economy);
        }
    }

    private void cacheAll(Economy economy) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                UUID playerUUID = onlinePlayer.getUniqueId();

                economy.cacheAccount(playerUUID);
            }
        });
    }
}