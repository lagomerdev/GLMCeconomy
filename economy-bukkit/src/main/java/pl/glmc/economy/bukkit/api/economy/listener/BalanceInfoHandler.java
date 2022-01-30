package pl.glmc.economy.bukkit.api.economy.listener;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.economy.packets.BalanceResponse;
import pl.glmc.economy.EconomyPacketRegistry;

import java.math.BigDecimal;

public class BalanceInfoHandler extends ResponseHandlerListener<BalanceResponse, BigDecimal> {
    public BalanceInfoHandler() {
        super(EconomyPacketRegistry.ECONOMY.BALANCE_RESPONSE, BalanceResponse.class);
    }

    @Override
    public void received(BalanceResponse packet) {
        this.complete(packet.getOriginUniqueId(), packet.getBalance());
    }
}
