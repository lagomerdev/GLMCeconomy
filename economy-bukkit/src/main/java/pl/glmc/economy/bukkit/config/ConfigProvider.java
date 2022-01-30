package pl.glmc.economy.bukkit.config;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;

public class ConfigProvider {
    private final GlmcEconomyBukkit plugin;

    private ConfigData configData;

    public ConfigProvider(GlmcEconomyBukkit plugin) {
        this.plugin = plugin;

        try {
            this.loadConfig();
        } catch (NullPointerException exception) {
            exception.printStackTrace();

            this.plugin.getLogger().warning(ChatColor.RED + "Failed to load config!");
        }
    }

    private void loadConfig() {
        this.configData = new ConfigData();
    }

    public ConfigData getConfigData() {
        return configData;
    }
}
