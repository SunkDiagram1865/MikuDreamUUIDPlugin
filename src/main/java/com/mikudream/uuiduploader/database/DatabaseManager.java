package com.mikudream.uuiduploader.database;

import com.mikudream.uuiduploader.MikuDreamUUIDPlugin;
import com.mikudream.uuiduploader.utils.BindCodeGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    
    private final MikuDreamUUIDPlugin plugin;
    private Connection connection;
    private final String host, database, username, password, table;
    private final int port;
    
    public DatabaseManager(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfig().getString("database.host", "localhost");
        this.port = plugin.getConfig().getInt("database.port", 887);
        this.database = plugin.getConfig().getString("database.database", "cysunk");
        this.username = plugin.getConfig().getString("database.username", "cysunk");
        this.password = plugin.getConfig().getString("database.password", "cysunk");
        this.table = plugin.getConfig().getString("database.table", "playermc");
        
        // 不再自动建表
        // initializeTable();
    }
    
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + 
                        "?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&verifyServerCertificate=false";
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }
    
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            plugin.getLogger().severe("数据库连接测试失败: " + e.getMessage());
            return false;
        }
    }
    
    public CompletableFuture<Boolean> uploadPlayerUUID(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO " + table + " (name, uuid, bind) VALUES (?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE name = VALUES(name)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, player.getName());
                    pstmt.setString(2, player.getUniqueId().toString());
                    pstmt.setString(3, BindCodeGenerator.generateBindCode());
                    int result = pstmt.executeUpdate();
                    if (plugin.getConfig().getBoolean("settings.debug", false)) {
                        plugin.getLogger().info("玩家UUID上传结果: " + player.getName() + " (" + player.getUniqueId() + ") - " + result);
                    }
                    return result > 0;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("上传玩家UUID失败: " + e.getMessage());
                return false;
            }
        });
    }
    
    public CompletableFuture<Boolean> uploadPlayerUUID(String playerName, UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO " + table + " (name, uuid, bind) VALUES (?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE name = VALUES(name)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, playerName);
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.setString(3, BindCodeGenerator.generateBindCode());
                    int result = pstmt.executeUpdate();
                    if (plugin.getConfig().getBoolean("settings.debug", false)) {
                        plugin.getLogger().info("玩家UUID上传结果: " + playerName + " (" + playerUUID + ") - " + result);
                    }
                    return result > 0;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("上传玩家UUID失败: " + e.getMessage());
                return false;
            }
        });
    }
    
    public CompletableFuture<String> getPlayerBindCode(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                String sql = "SELECT bind FROM " + table + " WHERE uuid = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, player.getUniqueId().toString());
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("bind");
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("查询玩家配对码失败: " + e.getMessage());
            }
            return null;
        });
    }
    
    public CompletableFuture<String> getPlayerBindCode(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                String sql = "SELECT bind FROM " + table + " WHERE name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, playerName);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("bind");
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("查询玩家配对码失败: " + e.getMessage());
            }
            return null;
        });
    }
    
    public CompletableFuture<String> getPlayerUUID(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                String sql = "SELECT uuid FROM " + table + " WHERE name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, playerName);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("uuid");
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("查询玩家UUID失败: " + e.getMessage());
            }
            return null;
        });
    }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("关闭数据库连接失败: " + e.getMessage());
            }
        }
    }
} 