package net.kinoko2k.pdm;

import net.kinoko2k.pdm.commands.LookupCommand;
import net.kinoko2k.pdm.database.DatabaseManager;
import net.kinoko2k.pdm.listeners.PlayerListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;

public final class PlayerDataManager extends JavaPlugin {
    private DatabaseManager databaseManager;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        String url = config.getString("database.url");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        if (url == null || username == null || password == null) {
            getLogger().severe("データベース設定が不完全です。config.ymlを確認してください。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        databaseManager = new DatabaseManager(url, username, password);

        try {
            getLogger().info("データベースへの接続を試みています...");
            databaseManager.connect();
            getLogger().info("テーブルの作成を試みています...");
            databaseManager.createTable();
            getLogger().info("データベースのセットアップが完了しました。");
        } catch (SQLException e) {
            getLogger().severe("データベースエラー: " + e.getMessage());
            getLogger().severe("スタックトレース:");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LookupCommand lookupCommand = new LookupCommand(this, databaseManager);
        getCommand("pdm").setExecutor(lookupCommand);
        getCommand("pdm").setTabCompleter(lookupCommand);

        getServer().getPluginManager().registerEvents(new PlayerListener(this, databaseManager), this);
        getLogger().info("PlayerDataManager が有効になりました！");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerDataManager が無効になりました！");
    }
}