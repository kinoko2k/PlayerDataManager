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
     * MCIDからプレイ時間を取得
     * @param mcid プレイヤーのMCID
     * @return プレイ時間（秒）、プレイヤーが存在しない場合は-1
     * @throws SQLException データベースエラーが発生した場合
     */
    public static long getPlaytimeByMCID(String mcid) throws SQLException {
        PlayerData data = plugin.getDatabaseManager().getPlayerDataByMCID(mcid);
        return data != null ? data.getPlaytime() : -1;
    }

    /**
     * MCIDからフォーマットされたプレイ時間文字列を取得
     * @param mcid プレイヤーのMCID
     * @return "XX時間XX分XX秒" 形式のプレイ時間、プレイヤーが存在しない場合はnull
     * @throws SQLException データベースエラーが発生した場合
     */
    public static String getFormattedPlaytimeByMCID(String mcid) throws SQLException {
        long playtime = getPlaytimeByMCID(mcid);
        if (playtime == -1) {
            return null;
        }
        
        long hours = playtime / 3600;
        long minutes = (playtime % 3600) / 60;
        long seconds = playtime % 60;
        
        return String.format("%02d時間%02d分%02d秒", hours, minutes, seconds);
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