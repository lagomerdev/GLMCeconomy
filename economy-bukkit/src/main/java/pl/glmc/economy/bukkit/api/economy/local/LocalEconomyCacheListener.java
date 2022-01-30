package pl.glmc.economy.bukkit.api.economy.local;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.glmc.exchange.common.Economy;

import java.util.UUID;

public class LocalEconomyCacheListener implements Listener {
    private final Economy apiEconomyProvider;

    public LocalEconomyCacheListener(Economy economy) {
        this.apiEconomyProvider = economy;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        UUID playerUUID = joinEvent.getPlayer().getUniqueId();
        if (!this.apiEconomyProvider.isCached(playerUUID)) {
            this.apiEconomyProvider.cacheAccount(playerUUID);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        UUID playerUUID = quitEvent.getPlayer().getUniqueId();
        if (this.apiEconomyProvider.isCached(playerUUID)) {
            this.apiEconomyProvider.removeFromCache(playerUUID);
        }
    }
}