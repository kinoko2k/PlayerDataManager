package net.kinoko2k.pdm.listeners;

import net.kinoko2k.pdm.PlayerDataManager;
import net.kinoko2k.pdm.data.PlayerData;
import net.kinoko2k.pdm.database.DatabaseManager;
import net.kinoko2k.pdm.utils.DiscordWebhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final PlayerDataManager plugin;
    private final DatabaseManager database;
    private final Map<UUID, Long> loginTimes;
    private final DiscordWebhook discord;

    public PlayerListener(PlayerDataManager plugin, DatabaseManager database) {
        this.plugin = plugin;
        this.database = database;
        this.loginTimes = new HashMap<>();
        this.discord = new DiscordWebhook(plugin.getConfig().getString("discord.webhook-url"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getName();
        String ipAddress = event.getPlayer().getAddress().getAddress().getHostAddress();
        loginTimes.put(playerUUID, System.currentTimeMillis() / 1000);

        try {
            PlayerData existingData = database.getPlayerData(playerUUID);
            
            if (existingData == null) {
                PlayerData newPlayerData = new PlayerData(playerName, playerUUID);
                newPlayerData.setLastIpAddress(ipAddress);
                database.savePlayerData(newPlayerData);
                plugin.getLogger().info(playerName + "の新規データを作成しました。");

                String message = String.format("""
                    %sのデータを新規に作成しました。
                    以下がそのデータです：
                    
                    MCID: %s
                    UUID: %s
                    初回ログイン: %s
                    IPアドレス: %s
                    """,
                    playerName,
                    playerName,
                    playerUUID.toString(),
                    newPlayerData.getFirstLoginDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")),
                    ipAddress
                );
                
                discord.sendMessage(message);
            } else {
                existingData.setLastIpAddress(ipAddress);
                database.savePlayerData(existingData);
                plugin.getLogger().info(playerName + "の既存データを読み込みました。累計プレイ時間: " + 
                    existingData.getPlaytime() + "秒");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("プレイヤーデータの処理中にエラーが発生しました: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (loginTimes.containsKey(playerUUID)) {
            long loginTime = loginTimes.get(playerUUID);
            long currentTime = System.currentTimeMillis() / 1000;
            long sessionTime = currentTime - loginTime;

            try {
                PlayerData playerData = new PlayerData(event.getPlayer().getName(), playerUUID);
                playerData.setPlaytime(sessionTime);
                playerData.setLastIpAddress(event.getPlayer().getAddress().getAddress().getHostAddress());
                database.savePlayerData(playerData);
                plugin.getLogger().info(event.getPlayer().getName() + "のプレイ時間を更新しました。今回のセッション: "
                    + sessionTime + "秒");
            } catch (SQLException e) {
                plugin.getLogger().severe("プレイヤーデータの保存に失敗しました: " + e.getMessage());
            }

            loginTimes.remove(playerUUID);
        }
    }
}