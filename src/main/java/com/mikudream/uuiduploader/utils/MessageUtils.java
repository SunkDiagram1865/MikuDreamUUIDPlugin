package com.mikudream.uuiduploader.utils;

import com.mikudream.uuiduploader.MikuDreamUUIDPlugin;
import org.bukkit.ChatColor;

public class MessageUtils {
    
    private final MikuDreamUUIDPlugin plugin;
    
    public MessageUtils(MikuDreamUUIDPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 将颜色代码转换为Bukkit颜色
     * @param message 包含颜色代码的消息
     * @return 转换后的消息
     */
    public String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * 获取带前缀的消息
     * @param message 消息内容
     * @return 带前缀的消息
     */
    public String getPrefixedMessage(String message) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&bMikuDream&8] &r");
        return colorize(prefix + message);
    }
    
    /**
     * 格式化消息，替换占位符
     * @param message 消息模板
     * @param replacements 替换参数
     * @return 格式化后的消息
     */
    public String formatMessage(String message, String... replacements) {
        if (message == null) {
            return "";
        }
        
        String formatted = message;
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = "{" + replacements[i] + "}";
                String value = replacements[i + 1];
                formatted = formatted.replace(placeholder, value);
            }
        }
        
        return colorize(formatted);
    }
} 