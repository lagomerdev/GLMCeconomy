package pl.glmc.economy.bungee.api;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyFactory;
import pl.glmc.economy.bungee.api.economy.local.LocalEconomy;
import pl.glmc.exchange.bungee.GlmcExchangeBungee;
import pl.glmc.exchange.bungee.GlmcExchangeBungeeProvider;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GlmcExchangeProvider implements GlmcExchangeBungee {
    private final GlmcEconomyBungee plugin;

    private ApiEconomyFactory economyFactory;
    private LocalEconomy localEconomy;

    public GlmcExchangeProvider(GlmcEconomyBungee plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.economyFactory = new ApiEconomyFactory(this.plugin);
        this.localEconomy = new LocalEconomy(this.plugin);

        GlmcExchangeBungeeProvider.register(this);

        this.plugin.getLogger().info(ChatColor.DARK_GREEN + "Loaded Bungee API Provider");

        this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
            this.localEconomy.getPlayerBankEconomy().add(UUID.fromString("5d689771-1ffd-4513-82b8-b58d3f8540da"), new BigDecimal("1000.69"));
        }, 10, TimeUnit.SECONDS);
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
