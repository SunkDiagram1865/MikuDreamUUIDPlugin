package com.mikudream.uuiduploader.listeners;

import com.mikudream.uuiduploader.MikuDreamUUIDPlugin;
import com.mikudream.uuiduploader.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    
    private final MikuDreamUUIDPlugin plugin;
    private final MessageUtils messageUtils;
    private final Map<UUID, Long> lastUploadTime;
    
    public PlayerListener(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
        this.messageUtils = plugin.getMessageUtils();
        this.lastUploadTime = new HashMap<>();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // 检查是否启用自动上传
        if (!plugin.getConfig().getBoolean("settings.auto_upload_on_join", true)) {
            return;
        }
        
        // 检查上传冷却时间
        long currentTime = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("settings.upload_cooldown", 300) * 1000;
        
        if (lastUploadTime.containsKey(playerUUID)) {
            long timeSinceLastUpload = currentTime - lastUploadTime.get(playerUUID);
            if (timeSinceLastUpload < cooldown) {
                if (plugin.getConfig().getBoolean("settings.debug", false)) {
                    plugin.getLogger().info("玩家 " + player.getName() + " 的上传冷却中，跳过上传");
                }
                return;
            }
        }
        
        // 异步上传UUID
        plugin.getDatabaseManager().uploadPlayerUUID(player).thenAccept(success -> {
            if (success) {
                lastUploadTime.put(playerUUID, currentTime);
                
                // 在主线程中发送消息
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    String message = plugin.getConfig().getString("messages.upload_success", "&a玩家UUID上传成功: {player} ({uuid})")
                            .replace("{player}", player.getName())
                            .replace("{uuid}", playerUUID.toString());
                    
                    if (plugin.getConfig().getBoolean("settings.debug", false)) {
                        plugin.getLogger().info(messageUtils.colorize(message));
                    }
                });
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    String message = plugin.getConfig().getString("messages.upload_failed", "&c玩家UUID上传失败: {player}")
                            .replace("{player}", player.getName());
                    
                    plugin.getLogger().warning(messageUtils.colorize(message));
                });
            }
        });
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 检查是否启用记录最后在线时间
        // if (plugin.getConfig().getBoolean("settings.record_last_seen", true)) {
        //     plugin.getDatabaseManager().updateLastSeen(player.getUniqueId()).thenAccept(success -> {
        //         if (plugin.getConfig().getBoolean("settings.debug", false)) {
        //             if (success) {
        //                 plugin.getLogger().info("玩家 " + player.getName() + " 的最后在线时间已更新");
        //             } else {
        //                 plugin.getLogger().warning("更新玩家 " + player.getName() + " 的最后在线时间失败");
        //             }
        //         }
        //     });
        // }
        
        // 清理冷却时间记录
        lastUploadTime.remove(player.getUniqueId());
    }
} 