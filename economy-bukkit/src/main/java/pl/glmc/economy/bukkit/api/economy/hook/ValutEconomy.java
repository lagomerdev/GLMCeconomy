package pl.glmc.economy.bukkit.api.economy.hook;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import pl.glmc.economy.bukkit.GlmcEconomyBukkit;

import java.math.BigDecimal;
import java.util.List;

public class ValutEconomy implements Economy {

    private final GlmcEconomyBukkit plugin;

    public ValutEconomy(GlmcEconomyBukkit plugin) {
        this.plugin = plugin;

        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            this.plugin.getServer().getServicesManager().register(Economy.class, this, this.plugin, ServicePriority.Highest);
            this.plugin.getLogger().warning(ChatColor.GREEN + "Hooked as Vault provider!");
        } else this.plugin.getLogger().warning("Vault not present!");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getDecimalFormat().format(v);
    }

    @Override
    public String currencyNamePlural() {
        return "GlobalCoin";
    }

    @Override
    public String currencyNameSingular() {
        return this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getEconomyConfig().getCurrencySign();
    }

    @Override
    public boolean hasAccount(String s) {
        var uuid = this.plugin.getGlmcApiBukkit().getLuckPermsHook().getLuckPerms().getUserManager().lookupUniqueId(s).join();
        if (uuid == null) return false;

        return !this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(uuid).join();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(offlinePlayer.getUniqueId()).join();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return this.hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        var uuid = this.plugin.getGlmcApiBukkit().getLuckPermsHook().getLuckPerms().getUserManager().lookupUniqueId(s).join();
        if (uuid == null) return 0D;

        return this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(offlinePlayer.getUniqueId()).join().doubleValue();
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return this.getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        var uuid = this.plugin.getGlmcApiBukkit().getLuckPermsHook().getLuckPerms().getUserManager().lookupUniqueId(s).join();
        if (uuid == null) return false;

        int result = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().compareTo(new BigDecimal(v));

        return result >= 0;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        int result = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(offlinePlayer.getUniqueId()).join().compareTo(new BigDecimal(v));

        return result >= 0;
    }

    @Override
    public boolean has(String playerName, String worldName, double v) {
        return this.has(playerName, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return this.has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        var uuid = this.plugin.getGlmcApiBukkit().getLuckPermsHook().getLuckPerms().getUserManager().lookupUniqueId(s).join();
        if (uuid == null || !this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(uuid).join()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        }
        if (v < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        boolean success = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().remove(uuid, new BigDecimal(v)).join();
        if (success) {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.FAILURE, "Balance too low");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        var uuid = offlinePlayer.getUniqueId();
        if (v < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
        if (!this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(uuid).join()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        }

        boolean success = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().remove(uuid, new BigDecimal(v)).join();
        if (success) {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.FAILURE, "Balance too low");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return this.withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        var uuid = this.plugin.getGlmcApiBukkit().getLuckPermsHook().getLuckPerms().getUserManager().lookupUniqueId(s).join();
        if (uuid == null || !this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(uuid).join()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        }
        if (v <= 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        boolean success = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().add(uuid, new BigDecimal(v)).join();
        if (success) {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.FAILURE, "Unknown error");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        var uuid = offlinePlayer.getUniqueId();
        if (!this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().accountExists(uuid).join()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        }
        if (v <= 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        boolean success = this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().add(uuid, new BigDecimal(v)).join();
        if (success) {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getBalance(uuid).join().doubleValue(), EconomyResponse.ResponseType.FAILURE, "Unknown error");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return this.depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GLMCeconomy does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
