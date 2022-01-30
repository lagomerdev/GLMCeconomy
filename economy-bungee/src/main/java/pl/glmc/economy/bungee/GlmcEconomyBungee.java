package pl.glmc.economy.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import pl.glmc.api.bungee.GlmcApiBungee;
import pl.glmc.api.bungee.GlmcApiBungeeProvider;
import pl.glmc.api.bungee.database.DatabaseProvider;
import pl.glmc.economy.bungee.api.GlmcExchangeProvider;
import pl.glmc.economy.bungee.cmd.economy.BalanceCommand;
import pl.glmc.economy.bungee.cmd.economy.BaltopCommand;
import pl.glmc.economy.bungee.cmd.economy.EconomyCommand;
import pl.glmc.economy.bungee.config.ConfigProvider;

public class GlmcEconomyBungee extends Plugin {

    private GlmcExchangeProvider glmcExchangeProvider;
    private GlmcApiBungee glmcApiBungee;

    private ConfigProvider configProvider;
    private DatabaseProvider databaseProvider;

    @Override
    public void onLoad() {
        this.configProvider = new ConfigProvider(this);
        this.databaseProvider = new DatabaseProvider(this, this.configProvider.getDatabaseConfig());
    }

    @Override
    public void onEnable() {
        try {
            this.glmcApiBungee = GlmcApiBungeeProvider.get();
        } catch (NullPointerException e) {
            e.printStackTrace();
            this.getLogger().warning(ChatColor.RED + "Failed to load GLMCapi!");
        }

        this.glmcExchangeProvider = new GlmcExchangeProvider(this);
        this.glmcExchangeProvider.load();

        BalanceCommand balanceCommand = new BalanceCommand(this);
        BaltopCommand baltopCommand = new BaltopCommand(this);
        EconomyCommand economyCommand = new EconomyCommand(this);
    }

    @Override
    public void onDisable() {
        this.databaseProvider.unload();
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public GlmcApiBungee getGlmcApiBungee() {
        return glmcApiBungee;
    }

    public GlmcExchangeProvider getGlmcExchangeProvider() {
        return glmcExchangeProvider;
    }
}
