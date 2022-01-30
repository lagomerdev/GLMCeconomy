package pl.glmc.exchange.common.config;

import pl.glmc.exchange.common.EconomyType;

public class EconomyConfig {

    private final String name, currencySign;
    private final EconomyType economyType;

    public EconomyConfig(String name, String currencySign, EconomyType economyType) {
        this.name = name;
        this.currencySign = currencySign;
        this.economyType = economyType;
    }

    public String getName() {
        return name;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public EconomyType getEconomyType() {
        return economyType;
    }
}
