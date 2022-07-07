package pl.glmc.economy.bungee.cmd.economy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.lang3.StringUtils;
import pl.glmc.economy.bungee.GlmcEconomyBungee;
import pl.glmc.economy.bungee.api.economy.ApiEconomyProvider;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BaltopCommand extends Command implements TabExecutor {
    private static final int PER_PAGE = 10;
    private static final DateTimeFormatter LAST_REFRESHED_FORMATTER = DateTimeFormatter.ofPattern("d/M/uu H:mm:ss");
    private static final List<String> PARAMS = List.of("--force");

    private final GlmcEconomyBungee plugin;

    private LocalDateTime lastRefresh;
    private LinkedHashMap<String, BigDecimal> topBalance;
    private int pages;

    public BaltopCommand(final GlmcEconomyBungee plugin) {
        super("baltop", "economy.baltop", "balancetop");

        this.plugin = plugin;

        this.calculateBalanceTop()
                .thenAccept(success -> {
                    if (success) {
                        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, this);
                    } else {
                        this.plugin.getLogger().warning(ChatColor.RED + "Failed to calculate baltop...");
                    }
                });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("economy.baltop.force")
                && Arrays.stream(args).noneMatch(arg -> arg.equalsIgnoreCase("--force"))) {
            return PARAMS;
        }

        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        boolean refresh = lastRefresh.plusMinutes(2).isBefore(currentTimestamp.toLocalDateTime());

        if (args.length > 0 && sender.hasPermission("economy.baltop.force")
                && Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--force"))) {
            refresh = true;
        }

        if (refresh) {
            this.calculateBalanceTop()
                    .thenAccept(success -> {
                        if (success) {
                            this.sendBaltop(sender, args);
                        } else {
                            TextComponent errorResponse = new TextComponent();
                            errorResponse.addExtra(ChatColor.RED + "Wystąpił błąd podczas wczytywania listy najbogatszych graczy!");
                        }
                    });
        } else {
            this.sendBaltop(sender, args);
        }
    }

    private void sendBaltop(CommandSender sender, String[] args) {
        int page;
        if (args.length > 0 && StringUtils.isNumeric(args[0]) && sender.hasPermission("economy.baltop.all")) {
            page = Integer.parseInt(args[0]) - 1;

            if (page < 0) {
                TextComponent errorResponse = new TextComponent();
                errorResponse.addExtra(ChatColor.RED + "Numer strony nie może być mniejszy niż 1!");

                sender.sendMessage(errorResponse);

                return;
            } else if (page >= this.pages) {
                TextComponent errorResponse = new TextComponent();
                errorResponse.addExtra(ChatColor.RED + "Numer strony nie może być większy niż " + this.pages + "!");

                sender.sendMessage(errorResponse);

                return;
            }
        } else {
            page = 0;
        }

        String lastRefreshTime = lastRefresh.format(LAST_REFRESHED_FORMATTER);

        TextComponent baltopResponse = new TextComponent();
        baltopResponse.addExtra(ChatColor.GOLD + "Najbogatsi gracze na serwerze:");
        baltopResponse.addExtra(ChatColor.DARK_GRAY  + " | " + ChatColor.YELLOW + (page + 1) + ChatColor.GRAY  + "/" + ChatColor.YELLOW + pages);
        baltopResponse.addExtra(ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "(" + lastRefreshTime + ")");

        int position = 0;
        for (Map.Entry<String, BigDecimal> sortedPosition : topBalance.entrySet()) {
            position++;

            if (position <= PER_PAGE * page) continue;
            else if (position > PER_PAGE * page + PER_PAGE) break;

            baltopResponse.addExtra("\n" + ChatColor.GRAY + position + ". " + ChatColor.WHITE + sortedPosition.getKey() + ": " + ChatColor.YELLOW
                    + this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy().getDecimalFormat().format(sortedPosition.getValue()));
        }
        if (sender.hasPermission("economy.baltop.all") && (page+1) != this.pages) {
            baltopResponse.addExtra("\n" + ChatColor.GRAY + "Aby przejść na kolejną stornę użyj komendy /baltop " + (page+2));
        }

        sender.sendMessage(baltopResponse);
    }

    private CompletableFuture<Boolean> calculateBalanceTop() {
        CompletableFuture<Boolean> callback = new CompletableFuture<>();

        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            try {
                ApiEconomyProvider bankEconomyProvider = (ApiEconomyProvider) this.plugin.getGlmcExchangeProvider().getPlayerBankEconomy();

                HashMap<UUID, BigDecimal> combined = new HashMap<>(bankEconomyProvider.getRegisteredAccounts());

                this.topBalance = combined.entrySet().stream()
                        .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                        .limit(1000)
                        .collect(Collectors.toMap(key -> this.plugin.getGlmcApiBungee().getUserManager().getUsername(key.getKey(), key.getKey().toString()), Map.Entry::getValue, (key, value) -> key, LinkedHashMap::new));

                this.lastRefresh = new Timestamp(new Date().getTime()).toLocalDateTime();

                int entries = topBalance.size();

                this.pages = entries % PER_PAGE == 0 ? entries / PER_PAGE : (entries - entries % PER_PAGE) / PER_PAGE + 1;

                callback.complete(true);
            } catch (Exception e) {
                callback.complete(false);
            }
        });

        return callback;
    }
}
