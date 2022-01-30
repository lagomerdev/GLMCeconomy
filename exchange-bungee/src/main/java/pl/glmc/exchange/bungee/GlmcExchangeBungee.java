package pl.glmc.exchange.bungee;

import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.EconomyFactory;

public interface GlmcExchangeBungee {

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

    /**
     * Gets default player cash economy
     *
     * @return player cash economy
     */
    Economy getPlayerCashEconomy();
}
