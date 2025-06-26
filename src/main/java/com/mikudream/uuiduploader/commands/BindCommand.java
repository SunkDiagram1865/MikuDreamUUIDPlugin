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
import java.util.stream.Collectors;

public class BindCommand implements CommandExecutor, TabCompleter {
    
    private final MikuDreamUUIDPlugin plugin;
    private final MessageUtils messageUtils;
    
    public BindCommand(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
        this.messageUtils = plugin.getMessageUtils();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // 查看自己的配对码
            if (!(sender instanceof Player)) {
                sender.sendMessage(messageUtils.colorize("&c此命令只能由玩家使用"));
                return true;
            }
            
            Player player = (Player) sender;
            showBindCode(player, player);
            return true;
        }
        
        if (args.length == 1) {
            // 管理员查看其他玩家的配对码
            if (!sender.hasPermission("mikudream.uuidupload.admin")) {
                sender.sendMessage(messageUtils.colorize(plugin.getConfig().getString("messages.no_permission", "&c您没有权限使用此命令")));
                return true;
            }
            
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            
            if (targetPlayer != null) {
                showBindCode(targetPlayer, sender);
            } else {
                // 查询离线玩家的配对码
                plugin.getDatabaseManager().getPlayerBindCode(targetPlayerName).thenAccept(bindCode -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (bindCode != null) {
                            String message = plugin.getConfig().getString("messages.bind_code_show", "&a玩家 {player} 的配对码: &e{bind_code}")
                                    .replace("{player}", targetPlayerName)
                                    .replace("{bind_code}", bindCode);
                            sender.sendMessage(messageUtils.colorize(message));
                        } else {
                            String message = plugin.getConfig().getString("messages.player_not_found", "&c玩家 {player} 未找到")
                                    .replace("{player}", targetPlayerName);
                            sender.sendMessage(messageUtils.colorize(message));
                        }
                    });
                });
            }
            return true;
        }
        
        sender.sendMessage(messageUtils.colorize("&c用法: /" + label + " [玩家名]"));
        return true;
    }
    
    private void showBindCode(Player player, CommandSender sender) {
        plugin.getDatabaseManager().getPlayerBindCode(player).thenAccept(bindCode -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (bindCode != null) {
                    String message;
                    if (sender.equals(player)) {
                        message = plugin.getConfig().getString("messages.bind_code_own", "&a您的配对码: &e{bind_code}")
                                .replace("{bind_code}", bindCode);
                    } else {
                        message = plugin.getConfig().getString("messages.bind_code_show", "&a玩家 {player} 的配对码: &e{bind_code}")
                                .replace("{player}", player.getName())
                                .replace("{bind_code}", bindCode);
                    }
                    sender.sendMessage(messageUtils.colorize(message));
                } else {
                    // 如果玩家没有配对码，先上传UUID生成配对码
                    plugin.getDatabaseManager().uploadPlayerUUID(player).thenAccept(success -> {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (success) {
                                // 重新查询配对码
                                plugin.getDatabaseManager().getPlayerBindCode(player).thenAccept(newBindCode -> {
                                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                                        if (newBindCode != null) {
                                            String message = plugin.getConfig().getString("messages.bind_code_generated", "&a已为您生成配对码: &e{bind_code}")
                                                    .replace("{bind_code}", newBindCode);
                                            sender.sendMessage(messageUtils.colorize(message));
                                        } else {
                                            String message = plugin.getConfig().getString("messages.bind_code_error", "&c获取配对码失败");
                                            sender.sendMessage(messageUtils.colorize(message));
                                        }
                                    });
                                });
                            } else {
                                String message = plugin.getConfig().getString("messages.bind_code_error", "&c生成配对码失败");
                                sender.sendMessage(messageUtils.colorize(message));
                            }
                        });
                    });
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