package com.mikudream.uuiduploader;

import com.mikudream.uuiduploader.commands.UUIDUploadCommand;
import com.mikudream.uuiduploader.commands.UUIDReloadCommand;
import com.mikudream.uuiduploader.commands.BindCommand;
import com.mikudream.uuiduploader.listeners.PlayerListener;
import com.mikudream.uuiduploader.database.DatabaseManager;
import com.mikudream.uuiduploader.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class MikuDreamUUIDPlugin extends JavaPlugin {
    
    private static MikuDreamUUIDPlugin instance;
    private DatabaseManager databaseManager;
    private MessageUtils messageUtils;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认配置
        saveDefaultConfig();
        
        // 初始化消息工具
        messageUtils = new MessageUtils(this);
        
        // 初始化数据库管理器
        databaseManager = new DatabaseManager(this);
        
        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // 注册命令
        getCommand("uuidupload").setExecutor(new UUIDUploadCommand(this));
        getCommand("uuidreload").setExecutor(new UUIDReloadCommand(this));
        getCommand("bind").setExecutor(new BindCommand(this));
        
        // 测试数据库连接
        if (databaseManager.testConnection()) {
            getLogger().info("数据库连接成功！");
        } else {
            getLogger().severe("数据库连接失败！请检查配置文件。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getLogger().info("MikuDream UUID Uploader 插件已启用！");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().info("MikuDream UUID Uploader 插件已禁用！");
    }
    
    public static MikuDreamUUIDPlugin getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public MessageUtils getMessageUtils() {
        return messageUtils;
    }
} 