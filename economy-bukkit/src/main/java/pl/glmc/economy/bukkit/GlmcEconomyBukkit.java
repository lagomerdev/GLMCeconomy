package pl.glmc.economy.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.glmc.api.bukkit.GlmcApiBukkit;
import pl.glmc.api.bukkit.GlmcApiBukkitProvider;
import pl.glmc.economy.bukkit.api.GlmcExchangeProvider;
import pl.glmc.economy.bukkit.config.ConfigProvider;

import java.io.File;

public class GlmcEconomyBukkit extends JavaPlugin {
    private GlmcExchangeProvider glmcExchangeProvider;
    private GlmcApiBukkit glmcApiBukkit;

    private ConfigProvider configProvider;

    @Override
    public void onLoad() {
        this.loadFiles();

        this.configProvider = new ConfigProvider(this);
    }

    @Override
    public void onEnable() {
        try {
            this.glmcApiBukkit = GlmcApiBukkitProvider.get();
        } catch (NullPointerException e) {
            this.getLogger().warning(ChatColor.RED + "Failed to load GLMCapi!");
        }

        this.glmcExchangeProvider = new GlmcExchangeProvider(this);
        this.glmcExchangeProvider.load();
    }

    @Override
    public void onDisable() {
        this.getServer().getServicesManager().unregisterAll(this);
        this.glmcApiBukkit.unload(this);
    }

    private void loadFiles() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public GlmcExchangeProvider getGlmcExchangeProvider() {
        return glmcExchangeProvider;
    }

    public GlmcApiBukkit getGlmcApiBukkit() {
        return glmcApiBukkit;
    }
}
