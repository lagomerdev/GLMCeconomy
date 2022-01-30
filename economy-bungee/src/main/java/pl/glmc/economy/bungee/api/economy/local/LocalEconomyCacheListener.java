package pl.glmc.economy.bungee.api.economy.local;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.glmc.exchange.common.Economy;

import java.util.UUID;

public class LocalEconomyCacheListener implements Listener {

    private final Economy apiEconomyProvider;

    public LocalEconomyCacheListener(Economy economy) {
        this.apiEconomyProvider = economy;
    }

    @EventHandler
    public void onJoin(PostLoginEvent postLoginEvent) {
        UUID playerUUID = postLoginEvent.getPlayer().getUniqueId();
        if (!this.apiEconomyProvider.isCached(playerUUID)) {
            this.apiEconomyProvider.cacheAccount(playerUUID);
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent disconnectEvent) {
        UUID playerUUID = disconnectEvent.getPlayer().getUniqueId();
        if (this.apiEconomyProvider.isCached(playerUUID)) {
            this.apiEconomyProvider.removeFromCache(playerUUID);
        }
    }
}