package net.kinoko2k.pdm.commands;

import net.kinoko2k.pdm.PlayerDataManager;
import net.kinoko2k.pdm.data.PlayerData;
import net.kinoko2k.pdm.database.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LookupCommand implements CommandExecutor, TabCompleter {
    private final PlayerDataManager plugin;
    private final DatabaseManager database;

    public LookupCommand(PlayerDataManager plugin, DatabaseManager database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("pdm.lookup")) {
            sender.sendMessage(ChatColor.RED + "このコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "使用方法: /pdm lookup <mcid/uuid> <値>");
            return true;
        }

        try {
            PlayerData data;
            switch (args[0].toLowerCase()) {
                case "mcid":
                    data = database.getPlayerDataByMCID(args[1]);
                    break;
                case "uuid":
                    try {
                        UUID uuid = UUID.fromString(args[1]);
                        data = database.getPlayerData(uuid);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + "無効なUUID形式です。");
                        return true;
                    }
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "検索タイプは 'mcid' または 'uuid' を指定してください。");
                    return true;
            }

            if (data == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーデータが見つかりませんでした。");
                return true;
            }

            long totalSeconds = data.getPlaytime();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            sender.sendMessage(ChatColor.GREEN + "=== プレイヤーデータ ===");
            sender.sendMessage(ChatColor.YELLOW + "MCID: " + ChatColor.WHITE + data.getMcid());
            sender.sendMessage(ChatColor.YELLOW + "UUID: " + ChatColor.WHITE + data.getUuid());
            sender.sendMessage(ChatColor.YELLOW + "最終IP: " + ChatColor.WHITE + data.getLastIpAddress());
            sender.sendMessage(ChatColor.YELLOW + "初回ログイン: " + ChatColor.WHITE +
                    data.getFirstLoginDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            sender.sendMessage(ChatColor.YELLOW + "総プレイ時間: " + ChatColor.WHITE +
                    String.format("%02d時間%02d分%02d秒", hours, minutes, seconds));
            sender.sendMessage(ChatColor.GREEN + "==================");

        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "データベースエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("mcid");
            completions.add("uuid");
        }
        
        return completions;
    }
}