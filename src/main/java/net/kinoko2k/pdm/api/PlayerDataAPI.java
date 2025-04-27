package net.kinoko2k.pdm.api;

import net.kinoko2k.pdm.PlayerDataManager;
import net.kinoko2k.pdm.data.PlayerData;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataAPI {
    private static PlayerDataManager plugin;

    public static void setPlugin(PlayerDataManager plugin) {
        PlayerDataAPI.plugin = plugin;
    }

    /**
     * UUIDからプレイヤーデータを取得
     * @param uuid プレイヤーのUUID
     * @return PlayerDataオブジェクト、存在しない場合はnull
     * @throws SQLException データベースエラーが発生した場合
     */
    public static PlayerData getPlayerData(UUID uuid) throws SQLException {
        return plugin.getDatabaseManager().getPlayerData(uuid);
    }

    /**
     * MCIDからプレイヤーデータを取得
     * @param mcid プレイヤーのMCID
     * @return PlayerDataオブジェクト、存在しない場合はnull
     * @throws SQLException データベースエラーが発生した場合
     */
    public static PlayerData getPlayerDataByMCID(String mcid) throws SQLException {
        return plugin.getDatabaseManager().getPlayerDataByMCID(mcid);
    }

    /**
     * プレイヤーデータを保存
     * @param data 保存するPlayerDataオブジェクト
     * @throws SQLException データベースエラーが発生した場合
     */
    public static void savePlayerData(PlayerData data) throws SQLException {
        plugin.getDatabaseManager().savePlayerData(data);
    }
}