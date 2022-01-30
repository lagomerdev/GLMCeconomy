package pl.glmc.economy.bukkit.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.EconomyRegistered;

public class EconomyRegisteredListener extends PacketListener<EconomyRegistered> {
    private final GlmcEconomyBukkit plugin;

    public EconomyRegisteredListener(GlmcEconomyBukkit plugin) {
        super(EconomyPacketRegistry.ECONOMY.REGISTERED, EconomyRegistered.class);

        this.plugin = plugin;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(EconomyRegistered packet) {
        this.plugin.getGlmcExchangeProvider().getEconomyFactory().loadEconomy(packet.getEconomyConfig());
    }
}
