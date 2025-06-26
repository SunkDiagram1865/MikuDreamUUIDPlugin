package com.mikudream.uuiduploader.commands;

import com.mikudream.uuiduploader.MikuDreamUUIDPlugin;
import com.mikudream.uuiduploader.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDUploadCommand implements CommandExecutor, TabCompleter {
    
    private final MikuDreamUUIDPlugin plugin;
    private final MessageUtils messageUtils;
    
    public UUIDUploadCommand(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
        this.messageUtils = plugin.getMessageUtils();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mikudream.uuidupload.admin")) {
            sender.sendMessage(messageUtils.colorize(plugin.getConfig().getString("messages.no_permission", "&c您没有权限使用此命令")));
            return true;
        }
        
        if (args.length == 0) {
            // 上传所有在线玩家
            if (sender instanceof Player) {
                Player player = (Player) sender;
                uploadPlayerUUID(player, sender);
            } else {
                sender.sendMessage(messageUtils.colorize("&c控制台无法使用此命令上传自己的UUID"));
            }
            return true;
        }
        
        if (args.length == 1) {
            String targetPlayerName = args[0];
            
            // 尝试查找在线玩家
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer != null) {
                uploadPlayerUUID(targetPlayer, sender);
                return true;
            }
            
            // 如果在线玩家不存在，尝试通过Mojang API获取UUID
            sender.sendMessage(messageUtils.colorize("&e正在通过Mojang API查找玩家: " + targetPlayerName));
            
            // 这里可以添加Mojang API调用逻辑
            // 由于需要额外的HTTP请求库，这里简化处理
            sender.sendMessage(messageUtils.colorize("&c玩家 " + targetPlayerName + " 不在线，请确保玩家在线后再尝试"));
            return true;
        }
        
        sender.sendMessage(messageUtils.colorize("&c用法: /" + label + " [玩家名]"));
        return true;
    }
    
    private void uploadPlayerUUID(Player player, CommandSender sender) {
        plugin.getDatabaseManager().uploadPlayerUUID(player).thenAccept(success -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    String message = plugin.getConfig().getString("messages.upload_success", "&a玩家UUID上传成功: {player} ({uuid})")
                            .replace("{player}", player.getName())
                            .replace("{uuid}", player.getUniqueId().toString());
                    sender.sendMessage(messageUtils.colorize(message));
                } else {
                    String message = plugin.getConfig().getString("messages.upload_failed", "&c玩家UUID上传失败: {player}")
                            .replace("{player}", player.getName());
                    sender.sendMessage(messageUtils.colorize(message));
                }
            });
        });
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mikudream.uuidupload.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
} 