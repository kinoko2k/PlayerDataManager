package net.kinoko2k.pdm.database;

import net.kinoko2k.pdm.data.PlayerData;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

public class DatabaseManager {
    private Connection connection;
    private final String url;
    private final String username;
    private final String password;
    private final Logger logger;

    public DatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.logger = Logger.getLogger("PlayerDataManager");
    }

    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQLドライバーを読み込みました。");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQLドライバーが見つかりません: " + e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
            logger.info("データベースに接続しました。");
        } catch (SQLException e) {
            logger.severe("データベース接続エラー: " + e.getMessage());
            throw e;
        }
    }

    public void createTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("データベース接続が確立されていません。");
        }

        String sql = "CREATE TABLE IF NOT EXISTS player_data ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "mcid VARCHAR(16) NOT NULL,"
                + "uuid VARCHAR(36) NOT NULL UNIQUE,"
                + "first_login_date DATETIME NOT NULL,"
                + "playtime BIGINT NOT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("テーブルの作成に成功しました。");
        } catch (SQLException e) {
            logger.severe("テーブル作成エラー: " + e.getMessage());
            throw e;
        }
    }

    public PlayerData getPlayerData(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                PlayerData data = new PlayerData(
                    rs.getString("mcid"),
                    UUID.fromString(rs.getString("uuid"))
                );
                data.setId(rs.getInt("id"));
                data.setFirstLoginDate(rs.getTimestamp("first_login_date").toLocalDateTime());
                data.setPlaytime(rs.getLong("playtime"));
                return data;
            }
            return null;
        }
    }

    public void savePlayerData(PlayerData data) throws SQLException {
        PlayerData existingData = getPlayerData(data.getUuid());
        
        if (existingData == null) {
            String sql = "INSERT INTO player_data (mcid, uuid, first_login_date, playtime) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, data.getMcid());
                pstmt.setString(2, data.getUuid().toString());
                pstmt.setTimestamp(3, Timestamp.valueOf(data.getFirstLoginDate()));
                pstmt.setLong(4, data.getPlaytime());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE player_data SET mcid = ?, playtime = ? WHERE uuid = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, data.getMcid());
                pstmt.setLong(2, existingData.getPlaytime() + data.getPlaytime()); // 累計プレイ時間を更新
                pstmt.setString(3, data.getUuid().toString());
                pstmt.executeUpdate();
            }
        }
    }
}