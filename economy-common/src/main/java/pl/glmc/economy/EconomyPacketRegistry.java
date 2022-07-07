package pl.glmc.economy;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.economy.packets.*;

public class EconomyPacketRegistry {

    public static final class ECONOMY {
        public static final PacketInfo REGISTRATION_REQUEST = PacketInfo.make("economy.economy.registration_request", EconomyRegistrationRequest.class);
        public static final PacketInfo REGISTRATION_RESPONSE = PacketInfo.make("economy.registration_response", EconomyRegistrationResponse.class);
        public static final PacketInfo REGISTERED = PacketInfo.make("economy.registered", EconomyRegistered.class);
        public static final PacketInfo BALANCE_UPDATED = PacketInfo.make("economy.packet_updated", BalanceUpdated.class);
        public static final PacketInfo BALANCE_REQUEST = PacketInfo.make("economy.balance_request", BalanceRequest.class);
        public static final PacketInfo BALANCE_RESPONSE = PacketInfo.make("economy.balance_response", BalanceResponse.class);
        public static final PacketInfo BALANCE_SET_REQUEST = PacketInfo.make("economy.balance_set_request", BalanceSetRequest.class);
        public static final PacketInfo BALANCE_SET_RESPONSE = PacketInfo.make("economy.balance_set_response", BalanceSetResponse.class);
        public static final PacketInfo BALANCE_ADD_REQUEST = PacketInfo.make("economy.balance_add_request", BalanceAddRequest.class);
        public static final PacketInfo BALANCE_ADD_RESPONSE = PacketInfo.make("economy.balance_add_response", BalanceAddResponse.class);
        public static final PacketInfo BALANCE_REMOVE_REQUEST = PacketInfo.make("economy.balance_remove_request", BalanceRemoveRequest.class);
        public static final PacketInfo BALANCE_REMOVE_RESPONSE = PacketInfo.make("economy.balance_remove_response", BalanceRemoveResponse.class);
        public static final PacketInfo BALANCE_TRANSFER_REQUEST = PacketInfo.make("economy.balance_transfer_request", BalanceTransferRequest.class);
        public static final PacketInfo BALANCE_TRANSFER_RESPONSE = PacketInfo.make("economy.balance_transfer_response", BalanceTransferResponse.class);
        public static final PacketInfo ACCOUNT_CREATE_REQUEST = PacketInfo.make("economy.account_create_request", AccountCreateRequest.class);
        public static final PacketInfo ACCOUNT_CREATE_RESPONSE = PacketInfo.make("economy.account_create_response", AccountCreateResponse.class);
        public static final PacketInfo ACCOUNT_EXISTS_REQUEST = PacketInfo.make("economy.account_exists_request", AccountExistsRequest.class);
        public static final PacketInfo ACCOUNT_EXISTS_RESPONSE = PacketInfo.make("economy.account_exists_response", AccountExistsResponse.class);
        public static final PacketInfo TRANSACTION_LOG_REQUEST = PacketInfo.make("economy.transaction_log_request", TransactionLogRequest.class);
        public static final PacketInfo TRANSACTION_LOG_RESPONSE = PacketInfo.make("economy.transaction_log_response", TransactionLogResponse.class);
        public static final PacketInfo GET_ECONOMIES_REQUEST = PacketInfo.make("economy.get_economies_request", GetEconomiesRequest.class);
        public static final PacketInfo GET_ECONOMIES_RESPONSE = PacketInfo.make("economy.get_economies_response", GetEconomiesResponse.class);

    }

}
