package pl.glmc.economy.bukkit.api.economy.listener;


import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.TransactionLogResponse;
import pl.glmc.exchange.common.TransactionLog;

public class TransactionLogHandler extends ResponseHandlerListener<TransactionLogResponse, TransactionLog> {
    public TransactionLogHandler() {
        super(EconomyPacketRegistry.ECONOMY.TRANSACTION_LOG_RESPONSE, TransactionLogResponse.class);
    }

    @Override
    public void received(TransactionLogResponse packet) {
        this.complete(packet.getOriginUniqueId(), packet.getTransactionLog());
    }
}
