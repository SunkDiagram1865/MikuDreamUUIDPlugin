# 项目结构说明

```
MikuDreamUUIDPlugin/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mikudream/
│       │           └── uuiduploader/
│       │               ├── MikuDreamUUIDPlugin.java          # 主插件类
│       │               ├── commands/                         # 命令包
│       │               │   ├── UUIDUploadCommand.java        # UUID上传命令
│       │               │   └── UUIDReloadCommand.java        # 重载配置命令
│       │               ├── database/                         # 数据库包
│       │               │   └── DatabaseManager.java          # 数据库管理器
│       │               ├── listeners/                        # 监听器包
│       │               │   └── PlayerListener.java           # 玩家事件监听器
│       │               └── utils/                            # 工具包
│       │                   └── MessageUtils.java             # 消息工具类
│       └── resources/
│           ├── plugin.yml                                    # 插件配置文件
│           └── config.yml                                    # 默认配置文件
├── pom.xml                                                   # Maven项目配置
├── README.md                                                 # 项目说明文档
├── PROJECT_STRUCTURE.md                                      # 项目结构说明
├── database_init.sql                                         # 数据库初始化脚本
├── build.bat                                                 # Windows编译脚本
└── build.sh                                                  # Linux/Mac编译脚本
```

## 文件说明

### 核心文件
- **MikuDreamUUIDPlugin.java**: 插件主类，负责初始化和管理插件生命周期
- **DatabaseManager.java**: 数据库管理器，处理所有MySQL数据库操作
- **PlayerListener.java**: 玩家事件监听器，处理玩家加入和退出事件

### 命令文件
- **UUIDUploadCommand.java**: 处理 `/uuidupload` 命令，支持手动上传UUID
- **UUIDReloadCommand.java**: 处理 `/uuidreload` 命令，重新加载配置

### 工具文件
- **MessageUtils.java**: 消息工具类，处理颜色代码和消息格式化

### 配置文件
- **plugin.yml**: 插件元数据，定义插件信息、命令和权限
- **config.yml**: 插件配置文件，包含数据库连接信息和插件设置

### 构建文件
- **pom.xml**: Maven项目配置文件，定义依赖和构建过程
- **build.bat**: Windows批处理编译脚本
- **build.sh**: Linux/Mac shell编译脚本

### 数据库文件
- **database_init.sql**: 数据库初始化脚本，创建必要的表和索引

## 编译输出

编译成功后，将在 `target/` 目录下生成：
- `uuid-uploader-1.0.0.jar` - 可部署的插件JAR文件
- `uuid-uploader-1.0.0-sources.jar` - 源代码JAR文件

## 部署说明

1. 将 `target/uuid-uploader-1.0.0.jar` 复制到服务器的 `plugins/` 文件夹
2. 重启服务器或使用插件管理器重载
3. 编辑生成的配置文件 `plugins/MikuDreamUUIDUploader/config.yml`
4. 配置数据库连接信息
5. 重启服务器或重载插件

## 开发环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **IDE**: 推荐使用 IntelliJ IDEA 或 Eclipse
- **数据库**: MySQL 5.7+ 或 MariaDB 10.2+ 