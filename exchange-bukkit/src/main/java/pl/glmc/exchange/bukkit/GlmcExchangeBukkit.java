package pl.glmc.exchange.bukkit;

import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyFactory;

public interface GlmcExchangeBukkit {
    /**
     * Gets EconomyFactory
     *
     * @return EconomyFactory
     */
    EconomyFactory getEconomyFactory();

    /**
     * Gets default player bank economy
     *
     * @return player bank economy
     */
    Economy getPlayerBankEconomy();
}
