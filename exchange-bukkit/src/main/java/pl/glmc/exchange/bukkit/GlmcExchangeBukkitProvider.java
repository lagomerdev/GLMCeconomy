package pl.glmc.exchange.bukkit;

public final class GlmcExchangeBukkitProvider {
    private static GlmcExchangeBukkit instance = null;

    public static GlmcExchangeBukkit get() {
        if (instance == null) {
            throw new NullPointerException("GLMCeconomy is not loaded!");
        }

        return instance;
    }

    public static void register(GlmcExchangeBukkit instance) {
        GlmcExchangeBukkitProvider.instance = instance;
    }

    public static void unregister() {
        GlmcExchangeBukkitProvider.instance = null;
    }
}
