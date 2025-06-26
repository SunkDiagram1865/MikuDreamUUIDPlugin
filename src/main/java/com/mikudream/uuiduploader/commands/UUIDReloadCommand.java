package com.mikudream.uuiduploader.commands;

import com.mikudream.uuiduploader.MikuDreamUUIDPlugin;
import com.mikudream.uuiduploader.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UUIDReloadCommand implements CommandExecutor {
    
    private final MikuDreamUUIDPlugin plugin;
    private final MessageUtils messageUtils;
    
    public UUIDReloadCommand(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
        this.messageUtils = plugin.getMessageUtils();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mikudream.uuidupload.admin")) {
            sender.sendMessage(messageUtils.colorize(plugin.getConfig().getString("messages.no_permission", "&c您没有权限使用此命令")));
            return true;
        }
        
        try {
            // 重新加载配置
            plugin.reloadConfig();
            
            // 重新初始化数据库管理器
            plugin.getDatabaseManager().closeConnection();
            plugin.getDatabaseManager().testConnection();
            
            String message = plugin.getConfig().getString("messages.reload_success", "&a配置重新加载成功");
            sender.sendMessage(messageUtils.colorize(message));
            
            plugin.getLogger().info("配置已重新加载");
            
        } catch (Exception e) {
            String errorMessage = "&c重新加载配置失败: " + e.getMessage();
            sender.sendMessage(messageUtils.colorize(errorMessage));
            plugin.getLogger().severe("重新加载配置失败: " + e.getMessage());
        }
        
        return true;
    }
} 