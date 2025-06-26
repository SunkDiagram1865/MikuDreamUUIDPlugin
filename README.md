# MikuDream UUID Uploader

一个用于Leaves 1.21.5服务器的Java插件，负责将玩家UUID上传到phpMyAdmin数据库，并生成6位随机绑定码。

## 功能特性

- ✅ 自动在玩家加入时上传UUID到MySQL数据库
- ✅ 为每个玩家生成唯一的6位随机绑定码（数字和字母组合）
- ✅ 记录玩家首次加入和最后在线时间
- ✅ 支持手动上传指定玩家的UUID
- ✅ 支持查看玩家绑定码
- ✅ 异步数据库操作，不影响服务器性能
- ✅ 可配置的上传冷却时间
- ✅ 完整的权限系统
- ✅ 支持配置重载
- ✅ 调试模式支持

## 系统要求

- **服务器版本**: Leaves 1.21.5
- **Java版本**: Java 17+
- **数据库**: MySQL 5.7+ 或 MariaDB 10.2+
- **phpMyAdmin**: 用于管理数据库

## 安装步骤

### 1. 编译插件

```bash
cd MikuDreamUUIDPlugin
mvn clean package
```

编译完成后，JAR文件将位于 `target/uuid-uploader-1.0.4.jar`

编译成功后，将在 `target/` 目录下生成：
- `uuid-uploader-1.0.4.jar` - 可部署的插件JAR文件
- `uuid-uploader-1.0.4-sources.jar` - 源代码JAR文件

### 2. 安装插件

1. 将编译好的JAR文件放入服务器的 `plugins` 文件夹
2. 重启服务器或重载插件
3. 插件将自动生成配置文件

### 3. 配置数据库

1. 在phpMyAdmin中创建数据库（例如：`cysunk`）
2. 创建数据库用户并授予权限
3. 编辑 `plugins/MikuDreamUUIDUploader/config.yml` 文件

### 4. 配置示例

```yaml
# 数据库配置
database:
  host: "server.memsyslizi.cn"
  port: 887
  database: "cysunk"
  username: "cysunk"
  password: "cysunk"
  table: "player_uuids"
  
# 插件设置
settings:
  auto_upload_on_join: true
  record_last_seen: true
  upload_cooldown: 300
  debug: false
```

## 数据库表结构

插件会自动创建以下表结构：

```sql
CREATE TABLE player_uuids (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(16) NOT NULL,
    player_uuid VARCHAR(36) NOT NULL UNIQUE,
    pairing_code VARCHAR(6) NOT NULL UNIQUE,
    first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_uuid (player_uuid),
    INDEX idx_name (player_name),
    INDEX idx_pairing_code (pairing_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 命令

### `/bind [玩家名]`
- **权限**: `mikudream.uuidupload.bind`
- **描述**: 查看玩家绑定码
- **用法**: 
  - `/bind` - 查看自己的绑定码
  - `/bind <玩家名>` - 查看指定玩家的绑定码（需要管理员权限）

### `/uuidupload [玩家名]`
- **权限**: `mikudream.uuidupload.admin`
- **描述**: 手动上传指定玩家的UUID
- **用法**: 
  - `/uuidupload` - 上传自己的UUID（仅限玩家）
  - `/uuidupload <玩家名>` - 上传指定玩家的UUID

### `/uuidreload`
- **权限**: `mikudream.uuidupload.admin`
- **描述**: 重新加载插件配置
- **用法**: `/uuidreload`

## 权限

- `mikudream.uuidupload.admin` - 管理员权限，可以使用所有命令
- `mikudream.uuidupload.upload` - 上传权限（默认所有玩家都有）
- `mikudream.uuidupload.bind` - 绑定码查看权限（默认所有玩家都有）

## 绑定码功能

### 绑定码生成
- 每个玩家首次加入服务器时自动生成6位随机绑定码
- 绑定码由大写字母和数字组成（A-Z, 0-9）
- 每个绑定码都是唯一的，不会重复

### 绑定码查看
- 玩家可以使用 `/bind` 命令查看自己的绑定码
- 管理员可以使用 `/bind <玩家名>` 查看其他玩家的绑定码
- 支持查询离线玩家的绑定码

### 绑定码示例
- `ABC123`
- `DEF456`
- `XYZ789`

## 配置选项

### 数据库配置
- `host`: 数据库主机地址
- `port`: 数据库端口
- `database`: 数据库名称
- `username`: 数据库用户名
- `password`: 数据库密码
- `table`: 数据表名称

### 插件设置
- `auto_upload_on_join`: 是否在玩家加入时自动上传UUID
- `record_last_seen`: 是否记录玩家最后在线时间
- `upload_cooldown`: 上传冷却时间（秒）
- `debug`: 是否启用调试模式

### 消息配置
- `prefix`: 消息前缀
- `upload_success`: 上传成功消息
- `upload_failed`: 上传失败消息
- `reload_success`: 重载成功消息
- `no_permission`: 无权限消息
- `player_not_found`: 玩家未找到消息
- `database_error`: 数据库错误消息
- `pairing_code_own`: 自己的绑定码消息
- `pairing_code_show`: 显示其他玩家绑定码消息
- `pairing_code_generated`: 生成绑定码消息
- `pairing_code_error`: 绑定码错误消息

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库配置是否正确
   - 确保数据库服务正在运行
   - 检查防火墙设置
   - 验证SSL连接配置

2. **插件无法启动**
   - 检查Java版本是否为17+
   - 查看服务器日志获取详细错误信息
   - 确保所有依赖都已正确安装

3. **UUID上传失败**
   - 检查数据库权限
   - 确保表结构正确
   - 查看调试日志

4. **绑定码生成失败**
   - 检查数据库连接
   - 确保pairing_code字段存在
   - 查看调试日志

### 调试模式

启用调试模式以获取详细的日志信息：

```yaml
settings:
  debug: true
```

## 更新日志

### v1.0.0
- 初始版本发布
- 支持自动UUID上传
- 支持手动上传命令
- 支持6位随机绑定码生成
- 支持绑定码查看命令
- 完整的配置系统
- 异步数据库操作

## 许可证

© 2025 MikuDream 保留所有权利。

## 支持

如果您遇到任何问题或有建议，请联系开发团队。