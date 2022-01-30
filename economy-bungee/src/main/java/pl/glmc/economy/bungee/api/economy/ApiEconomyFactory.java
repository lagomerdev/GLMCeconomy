package pl.glmc.economy.bungee.api.economy;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.listener.GetEconomiesListener;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyFactory;
import pl.glmc.exchange.common.EconomyListener;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ApiEconomyFactory implements EconomyFactory {
    private final GlmcEconomyBungee plugin;
    private final ConcurrentHashMap<String, Economy> registeredEconomies;
    private final HashSet<EconomyConfig> registeredConfigs;
    private final HashSet<EconomyListener> registeredListeners;

    public ApiEconomyFactory(final GlmcEconomyBungee plugin) {
        this.plugin = plugin;

        this.registeredEconomies = new ConcurrentHashMap<>();
        this.registeredConfigs = new HashSet<>();
        this.registeredListeners = new HashSet<>();

        var getEconomiesListener = new GetEconomiesListener(this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(getEconomiesListener, this.plugin);

        EconomyRegistrationListener registrationListener = new EconomyRegistrationListener(this.plugin);
    }

    @Override
    public Economy loadEconomy(EconomyConfig economyConfig) {
        boolean registered = this.registeredEconomies.containsKey(economyConfig.getName());

        if (!registered) {
            ApiEconomyProvider apiEconomyProvider = new ApiEconomyProvider(this.plugin, economyConfig);
            apiEconomyProvider.register();

            this.registeredEconomies.put(economyConfig.getName(), apiEconomyProvider);
            this.registeredConfigs.add(economyConfig);

            for (EconomyListener registeredListener : this.registeredListeners) {
                registeredListener.loaded(apiEconomyProvider);
            }

            this.plugin.getLogger().info(ChatColor.GREEN + "Loaded economy " + ChatColor.DARK_GREEN + economyConfig.getName());

            return apiEconomyProvider;
        } else {
            throw new IllegalArgumentException("Given Economy name is already registered!");
        }
    }

    @Override
    public void registerListener(EconomyListener economyListener) {
        if (this.registeredListeners.contains(economyListener)) {
            throw new IllegalArgumentException("This listener has been already registered!");
        }

        this.plugin.getLogger().info(ChatColor.GREEN + "Registered new economy listener with " + economyListener.getClass().getName());

        this.registeredListeners.add(economyListener);
    }

    //small workaround
    @Override
    public void registerEconomy(EconomyConfig economyConfig) {
        boolean registered = this.registeredEconomies.containsKey(economyConfig.getName());

        if (!registered) {
            this.loadEconomy(economyConfig);
        } else {
            throw new IllegalArgumentException("Given Economy name is already registered!");
        }
    }

    /*@Override
    public void unregisterEconomy(String economyName) {
        Economy economy = this.registeredEconomies.remove(economyName);

        if (economy != null) {
            //todo cleanup stuff
        }
    }*/

    @Override
    public ConcurrentHashMap<String, Economy> getRegisteredEconomies() {
        return this.registeredEconomies;
    }

    @Override
    public HashSet<EconomyConfig> getRegisteredConfigs() {
        return this.registeredConfigs;
    }

    @Override
    public Economy getEconomy(String name) {
        Economy economy = this.registeredEconomies.getOrDefault(name, null);

        if (economy == null) {
            throw new NullPointerException("Economy with given is not registered!");
        }

        return economy;
    }
}
