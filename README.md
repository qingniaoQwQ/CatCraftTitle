# CatCraftTitle

[中文](#中文) | [English](#english)

---

## 中文

### 简介

**CatCraftTitle** 是一款基于 **Paper / Purpur / Spigot** 的 Minecraft 玩家头衔插件，支持 **MySQL + SQLite 双数据库自动切换**，可实现跨服同步。玩家可以自由管理自己的头衔和后缀，管理员可灵活分配与修改。

### 功能特点

-  玩家可拥有多个头衔（按 ID 管理），自行切换启用
-  独立后缀系统，可单独启用/关闭显示
-  管理员直接修改头衔显示名，无需删除重建
-  MySQL / SQLite 双数据库支持，自动降级
-  与 **PlaceholderAPI** 深度整合，提供变量支持
-  启动时显示彩色服务器信息横幅
-  多版本兼容（1.16.1-1.21.11）

### 兼容服务端
- ✅ Purpur 1.16.1 ~ 1.21.11
- ✅ Spigot 1.16.1 ~ 1.21.11
- ✅ Paper 1.16.1 ~ 1.21.11
- ✅ CraftBukkit 1.16.1 ~ 1.21.11

### 🔧 依赖

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

---

### ⚙️ 玩家指令

| 指令 | 说明 |
|------|------|
| `/title list` | 查看自己拥有的头衔列表及后缀状态 |
| `/title active <ID>` | 启用指定 ID 的头衔 |
| `/title deactive` | 停用当前头衔，恢复默认 |
| `/title remove <ID>` | 移除自己拥有的某个头衔（不可移除正在激活的） |
| `/title suffixactive` | 启用后缀显示 |
| `/title suffixdeactive` | 关闭后缀显示 |

---

### 🛠️ 管理员指令

> 需要权限：`catcraft.admin`

| 指令 | 说明 |
|------|------|
| `/titleadmin give <玩家> <ID> <显示名>` | 为玩家添加一个头衔（ID 必须唯一） |
| `/titleadmin edit <玩家> <ID> <新显示名>` | 修改玩家已有头衔的显示名 |
| `/titleadmin take <玩家> <ID>` | 移除玩家的某个头衔（强制） |
| `/titleadmin list <玩家>` | 查看玩家的所有头衔和后缀状态 |
| `/titleadmin setactive <玩家> <ID>` | 强制激活玩家的某个头衔 |
| `/titleadmin suffix <玩家> <后缀>` | 设置玩家的后缀内容（保留启用状态） |
| `/titleadmin deactive <玩家>` | 停用玩家的头衔 |

---

### 📌 PlaceholderAPI 变量

| 变量 | 说明 |
|------|------|
| `%catcraft_title%` | 玩家当前激活的头衔（已着色） |
| `%catcraft_suffix%` | 玩家当前激活的后缀（若已启用，否则为空） |

---

### 🏗️ 构建
| 构建 |
|------|
|  构建产物位于 build/libs/CatCraftTitle-*.jar |

---

### 📜 许可证
| 许可证 |
|------|
|  本项目采用 MIT License，详情见 LICENSE 文件。 |

---
### 🔧 配置文件

插件启动后会自动生成 `config.yml`，主要配置项：

```yaml
mysql:
  enabled: true          # true 尝试连接 MySQL，失败自动切 SQLite
  host: localhost
  port: 3306
  db: minecraft
  user: root
  pass: "your_password"  # 请修改为实际密码

local-database:
  file: catcraft.db      # SQLite 文件名
permissions:
  admin: "catcraft.admin"



