package pl.glmc.economy.bungee.cmd.economy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.lang3.StringUtils;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;
import pl.glmc.exchange.common.Economy;
import pl.glmc.exchange.common.Transaction;
import pl.glmc.exchange.common.TransactionLog;
import pl.glmc.exchange.common.config.EconomyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EconomyCommand extends Command implements TabExecutor {
    private final static List<String> SUBCOMMANDS_COMPLETION_LIST = List.of("balance", "list", "give", "reset", "set", "subcommands", "take", "transactions");
    private final static List<String> ECONOMY_COMMANDS = List.of("balance", "give", "reset", "set", "take", "transactions");
    private final static List<String> BALANCE_COMMANDS = List.of("give", "reset", "set", "take");
    private final static List<String> AMOUNT_SAMPLES = List.of("10.00", "50.00", "100.00", "250.00", "500.00", "1000.00", "5000.00", "10000.00");
    private final static List<String> TRANSACTION_SAMPLES = List.of("5", "10", "15", "20", "25", "50");

    private final GlmcEconomyBungee plugin;

    private final Pattern uuidPattern;
    private final Pattern completionUuidPattern;

    public EconomyCommand(final GlmcEconomyBungee plugin) {
        super("economy", "economy.admin", "eco");

        this.plugin = plugin;
        this.uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
        this.completionUuidPattern = Pattern.compile("^[-a-fA-F0-9]+");

        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, this);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("economy.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 0) {
            return SUBCOMMANDS_COMPLETION_LIST;
        } else if (args.length == 1) {
            String arg = args[0];

            return SUBCOMMANDS_COMPLETION_LIST.stream().filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                    .collect(Collectors.toSet());
        } else if (args.length == 2 && ECONOMY_COMMANDS.contains(args[0].toLowerCase())) {
            String arg = args[1];

            return this.plugin.getGlmcExchangeProvider().getEconomyFactory().getRegisteredConfigs().stream().map(EconomyConfig::getName)
                    .filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                    .collect(Collectors.toSet());
        } else if (args.length == 3 && ECONOMY_COMMANDS.contains(args[0].toLowerCase())) {
            String arg = args[2];

            ApiEconomyProvider economyProvider;
            try {
                economyProvider = (ApiEconomyProvider) this.plugin.getGlmcExchangeProvider().getEconomyFactory().getEconomy(args[1]);
            } catch (NullPointerException | ClassCastException e) {
                return new ArrayList<>();
            }
            
            boolean isBasicEconomy = economyProvider.getEconomyConfig().getName().equals("bank");

            Matcher matcher = completionUuidPattern.matcher(arg);
            if (isBasicEconomy) {
                return this.plugin.getGlmcApiBungee().getUserManager().getRegisteredUsernames().stream()
                        .filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                        .limit(25)
                        .collect(Collectors.toSet());
            } else if (matcher.matches()) {
                return economyProvider.getRegisteredAccounts().keySet().stream().map(UUID::toString)
                        .filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                        .limit(25)
                        .collect(Collectors.toSet());
            } else return new ArrayList<>();
        } else if (args.length == 4 && BALANCE_COMMANDS.contains(args[0].toLowerCase())) {
            String arg = args[3];

            return AMOUNT_SAMPLES.stream().filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                    .collect(Collectors.toSet());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("transactions")) {
            String arg = args[3];

            return TRANSACTION_SAMPLES.stream().filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                    .collect(Collectors.toSet());
        }

        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.listEconomies(sender);
            this.listSubcommands(sender);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                this.listEconomies(sender);
            } else if (args[0].equalsIgnoreCase("subcommands")) {
                this.listSubcommands(sender);
            } else {
                this.sendUsage(sender);
            }
        } else if (args.length == 3 || args.length == 4) {
            ApiEconomyProvider economy;
            try {
                economy = (ApiEconomyProvider) this.plugin.getGlmcExchangeProvider().getEconomyFactory().getEconomy(args[1]);
            } catch (NullPointerException | ClassCastException e) {
                TextComponent errorResponse = new TextComponent();
                errorResponse.addExtra(ChatColor.RED + "Podany parametr <economy> nie odpowiada żadnej istniejącej ekonomii!");
                errorResponse.addExtra(ChatColor.RED + "\n" + "Użyj polecenia /economy list, aby sprawdzić wszystkie dostępne ekonomie...");

                sender.sendMessage(errorResponse);

                return;
            }
            UUID accountUniqueId;

            String uniqueId = args[2];
            Matcher matcher = uuidPattern.matcher(uniqueId);
            if (matcher.matches()) {
                accountUniqueId = UUID.fromString(uniqueId);
            } else {
                accountUniqueId = this.plugin.getGlmcApiBungee().getUserManager().getUniqueId(uniqueId);

                if (accountUniqueId == null) {
                    TextComponent errorResponse = new TextComponent();
                    errorResponse.addExtra(ChatColor.RED + "Podany parametr <account> nie odpowiada żadnemu istniejącemu kontu!");
                    errorResponse.addExtra(ChatColor.RED + "\n" + "Najprawdopodobniej podano błędne UUID lub nick Gracza, który jest offline...");

                    sender.sendMessage(errorResponse);

                    return;
                }
            }

            if (!economy.getRegisteredAccounts().containsKey(accountUniqueId)) {
                TextComponent errorResponse = new TextComponent();
                errorResponse.addExtra(ChatColor.RED + "Błąd! (konto o podanym parametrze <account> nie istnieje)");

                sender.sendMessage(errorResponse);

                return;
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("balance")) {
                    this.getBalance(sender, economy, accountUniqueId);
                } else if (args[0].equalsIgnoreCase("reset")) {
                    this.resetBalance(sender, economy, accountUniqueId);
                } else if (args[0].equalsIgnoreCase("transactions")) {
                    this.transactionLog(sender, economy, accountUniqueId, 10);
                } else {
                    this.sendUsage(sender);
                }
            } else {
                BigDecimal amount;
                try {
                    amount = new BigDecimal(args[3]).setScale(2, RoundingMode.HALF_UP);

                    if (amount.signum() == -1) {
                        TextComponent errorResponse = new TextComponent();
                        errorResponse.addExtra(ChatColor.RED + "Podany parametr <amount> nie może być mniejszy niż zero!");

                        sender.sendMessage(errorResponse);

                        return;
                    }
                } catch (NumberFormatException e) {
                    TextComponent errorResponse = new TextComponent();
                    errorResponse.addExtra(ChatColor.RED + "Podany parametr <amount> nie jest prawidłową liczbą!");

                    sender.sendMessage(errorResponse);

                    return;
                }

                if (args[0].equalsIgnoreCase("give")) {
                    this.addBalance(sender, economy, accountUniqueId, amount);
                } else if (args[0].equalsIgnoreCase("take")) {
                    this.removeBalance(sender, economy, accountUniqueId, amount);
                } else if (args[0].equalsIgnoreCase("set")) {
                    this.setBalance(sender, economy, accountUniqueId, amount);
                } else if (args[0].equalsIgnoreCase("transactions")) {
                    this.transactionLog(sender, economy, accountUniqueId, amount.intValue());
                } else {
                    this.sendUsage(sender);
                }
            }
        } else {
            this.sendUsage(sender);
        }
    }

    private void getBalance(CommandSender sender, Economy economy, UUID accountUniqueId) {
        economy.getBalance(accountUniqueId)
                .thenAccept(balance -> {
                    TextComponent balanceResponse = new TextComponent();
                    if (balance != null) {
                        balanceResponse.addExtra(ChatColor.GREEN + "Konto o identyfikatorze " + accountUniqueId.toString() + " na ekonomii " + economy.getEconomyConfig().getName()
                                + " ma balans: " + ChatColor.DARK_GREEN + balance);
                    } else {
                        balanceResponse.addExtra(ChatColor.RED + "Błąd! (sprawdź poprawność podanych argumentów)");
                    }

                    sender.sendMessage(balanceResponse);
                });
    }

    private void resetBalance(CommandSender sender, Economy economy, UUID accountUniqueId) {
        economy.set(accountUniqueId, BigDecimal.ZERO)
                .thenAccept(success -> {
                    TextComponent setResponse = new TextComponent();
                    if (success) {
                        setResponse.addExtra(ChatColor.GREEN + "Pomyślnie zresetowano balans konta!");
                    } else {
                        setResponse.addExtra(ChatColor.RED + "Błąd! (sprawdź poprawność podanych argumentów)");
                    }

                    sender.sendMessage(setResponse);
                });
    }

    private void addBalance(CommandSender sender, Economy economy, UUID accountUniqueId, BigDecimal amount) {
        economy.add(accountUniqueId, amount)
                .thenAccept(success -> {
                    TextComponent addResponse = new TextComponent();
                    if (success) {
                        addResponse.addExtra(ChatColor.GREEN + "Pomyślnie dodano " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " do balansu konta!" );
                    } else {
                        addResponse.addExtra(ChatColor.RED + "Błąd! (sprawdź poprawność podanych argumentów)");
                    }

                    sender.sendMessage(addResponse);
                });
    }

    private void removeBalance(CommandSender sender, Economy economy, UUID accountUniqueId, BigDecimal amount) {
        economy.remove(accountUniqueId, amount)
                .thenAccept(success -> {
                    TextComponent removeResponse = new TextComponent();
                    if (success) {
                        removeResponse.addExtra(ChatColor.GREEN + "Pomyślnie zabrano " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " 2z balansu konta!");

                        sender.sendMessage(removeResponse);
                    } else {
                        economy.getBalance(accountUniqueId)
                                .thenAccept(balance -> {
                                    if (balance.subtract(amount).signum() != 1) {
                                        removeResponse.addExtra(ChatColor.RED + "Błąd! (balans konta byłby ujemy, jeżeli zabrano by sume podaną w parametrze <amount>)");
                                    } else {
                                        removeResponse.addExtra(ChatColor.RED + "Błąd! (sprawdź poprawność podanych argumentów)");
                                    }

                                    sender.sendMessage(removeResponse);
                                });
                    }
                });
    }

    private void setBalance(CommandSender sender, Economy economy, UUID accountUniqueId, BigDecimal amount) {
        economy.set(accountUniqueId, amount)
                .thenAccept(success -> {
                    TextComponent setResponse = new TextComponent();
                    if (success) {
                        setResponse.addExtra(ChatColor.GREEN + "Pomyślnie ustawiono balans konta na " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + "!");
                    } else {
                        setResponse.addExtra(ChatColor.RED + "Błąd! (sprawdź poprawność podanych argumentów)");
                    }

                    sender.sendMessage(setResponse);
                });
    }

    private void transactionLog(CommandSender sender, Economy economy, UUID accountUniqueId, int amount) {
        if (amount > 100) {
            TextComponent error = new TextComponent();
            error.addExtra(ChatColor.RED + "Nie można wczytać więcej niż 100 ostatnich transakcji!");

            sender.sendMessage(error);
            return;
        }

        economy.getTransactionLog(accountUniqueId, amount, TransactionLog.SortingMode.DESC, TransactionLog.OrderBy.CREATED_AT)
                .thenAccept(transactionLog -> {
                    TextComponent transactions = new TextComponent();
                    if (transactionLog.getAmount() != 0) {
                        transactions.addExtra(ChatColor.GREEN + "Lista ostatnich transakcji danego konta (" + transactionLog.getAmount() + ")");
                        for (Transaction transaction : transactionLog.getTransactions()) {
                            String action = "";
                            if (transaction.getAction() == 0) {
                                action = ChatColor.RED + "odjęto " + ChatColor.DARK_RED;
                            } else if (transaction.getAction() == 1) {
                                action = ChatColor.GREEN + "dodano " + ChatColor.DARK_GREEN;
                            } else if (transaction.getAction() == 2) {
                                action = ChatColor.YELLOW + "zresetowano na " + ChatColor.GOLD;
                            }

                            LocalDateTime localDateTime = transaction.getTimestamp().toLocalDateTime();
                            String time = localDateTime.toLocalDate() + " " + localDateTime.toLocalTime();

                            transactions.addExtra("\n" + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + time + ChatColor.DARK_GRAY + " | ");
                            transactions.addExtra(action + transaction.getAmount());
                        }
                    } else {
                        transactions.addExtra(ChatColor.RED + "Lista transakcji konta podanego w parametrze <account> jest pusta!");
                    }

                    sender.sendMessage(transactions);
                });
    }

    private void sendUsage(CommandSender sender) {
        TextComponent usage = new TextComponent();
        usage.addExtra(ChatColor.RED + "Nie odnaleziono takiego polecenia! Użyj /economy, aby sprawdzić listę dostępnych poleceń...");

        sender.sendMessage(usage);
    }

    private void listEconomies(CommandSender sender) {
        TextComponent economies = new TextComponent();
        economies.addExtra(ChatColor.GOLD + "" + ChatColor.BOLD + "Dostępne ekonomie: ");
        for (EconomyConfig registeredConfig : this.plugin.getGlmcExchangeProvider().getEconomyFactory().getRegisteredConfigs()) {
            economies.addExtra("\n" + ChatColor.YELLOW + "- " + registeredConfig.getName() + " (" + registeredConfig.getEconomyType() + ")");
        }

        sender.sendMessage(economies);
    }

    private void listSubcommands(CommandSender sender) {
        TextComponent subcommands = new TextComponent();
        subcommands.addExtra(ChatColor.GOLD + "" + ChatColor.BOLD + "Dostępne komendy: ");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy list - " + ChatColor.GOLD + "zwraca listę wszystkich dostępnych ekonomii");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy subcommands - " + ChatColor.GOLD + "zwraca listę wszystkich dostępnych subkomend");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy balance <economy> <account> - " + ChatColor.GOLD + "zwraca balans konta z danej ekonomii");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy reset <economy> <account> - " + ChatColor.GOLD + "resetuje balans konta na danej ekonomii");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy transactions <economy> <account> [amount] - " + ChatColor.GOLD + "zwraca ostatnio wykonane transackcje");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy give <economy> <account> <balance> - " + ChatColor.GOLD + "dodaje podana sumę do balansu konta na danej ekonomii");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy take <economy> <account> <balance> - " + ChatColor.GOLD + "zabiera podana sumę z balansu konta na danej ekonomii");
        subcommands.addExtra("\n" + ChatColor.YELLOW + "/economy set <economy> <account> <balance> - " + ChatColor.GOLD + "ustawia dana sumę jako balans konta na danej ekonomii");

        sender.sendMessage(subcommands);
    }
}
