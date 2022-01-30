package pl.glmc.economy.bukkit.api;

import org.bukkit.ChatColor;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.bukkit.api.economy.ApiEconomyFactory;
import pl.glmc.economy.bukkit.api.economy.hook.ValutEconomy;
import pl.glmc.economy.bukkit.api.economy.local.LocalEconomy;
import pl.glmc.exchange.bukkit.GlmcExchangeBukkit;
import pl.glmc.exchange.bukkit.GlmcExchangeBukkitProvider;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyFactory;

public class GlmcExchangeProvider implements GlmcExchangeBukkit {
    private final GlmcEconomyBukkit plugin;

    private ApiEconomyFactory economyFactory;
    private LocalEconomy localEconomy;

    public GlmcExchangeProvider(GlmcEconomyBukkit plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.economyFactory = new ApiEconomyFactory(this.plugin);
        this.localEconomy = new LocalEconomy(this.plugin);

        GlmcExchangeBukkitProvider.register(this);

        ValutEconomy valutEconomy = new ValutEconomy(this.plugin);

        this.plugin.getLogger().info(ChatColor.DARK_GREEN + "Loaded API Provider");
    }

    @Override
    public EconomyFactory getEconomyFactory() {
        return this.economyFactory;
    }

    @Override
    public Economy getPlayerBankEconomy() {
        return this.localEconomy.getPlayerBankEconomy();
    }

    @Override
    public Economy getPlayerCashEconomy() {
        return this.localEconomy.getPlayerCashEconomy();
    }

}
