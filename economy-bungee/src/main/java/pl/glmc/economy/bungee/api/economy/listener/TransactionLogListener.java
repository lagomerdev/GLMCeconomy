package pl.glmc.economy.bungee.api.economy.listener;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.economy.EconomyPacketRegistry;
import pl.glmc.economy.packets.TransactionLogRequest;
import pl.glmc.economy.packets.TransactionLogResponse;

public class TransactionLogListener extends PacketListener<TransactionLogRequest> {
    private final GlmcEconomyBungee plugin;
    private final ApiEconomyProvider economyProvider;

    public TransactionLogListener(final GlmcEconomyBungee plugin, final ApiEconomyProvider economyProvider) {
        super(EconomyPacketRegistry.ECONOMY.TRANSACTION_LOG_REQUEST, TransactionLogRequest.class);

        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }

    @Override
    public void received(TransactionLogRequest packet) {
        this.economyProvider.getTransactionLog(packet.getAccountUniqueId(), packet.getLimit(), packet.getSortingMode(), packet.getOrderBy())
                .thenAccept(transactionLog -> {
                    boolean success = transactionLog.getAmount() != 0;

                    TransactionLogResponse response = new TransactionLogResponse(success, packet.getUniqueId(), transactionLog);

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(response, packet.getSender(), economyProvider.getEconomyConfig().getName());
                });
    }
}
