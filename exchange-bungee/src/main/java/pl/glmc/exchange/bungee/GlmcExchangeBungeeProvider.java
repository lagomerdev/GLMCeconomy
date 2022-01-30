package pl.glmc.exchange.bungee;

public class GlmcExchangeBungeeProvider {
    private static GlmcExchangeBungee instance = null;

    public static GlmcExchangeBungee get() {
        if (instance == null) {
            throw new NullPointerException("GLMCeconomy is not loaded!");
        }        return instance;
    }

    public static void register(GlmcExchangeBungee instance) {
        GlmcExchangeBungeeProvider.instance = instance;
    }

    public static void unregister() {
        GlmcExchangeBungeeProvider.instance = null;
    }
}
