---

## 🎯 插件作用

CatCraftTitle 是一个**轻量级、功能完善的 Minecraft 头衔与后缀管理插件**，支持 MySQL 与 SQLite 双数据库，帮助服务器轻松实现玩家头衔系统，并支持跨服同步。

---
![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

## ✨ 功能特点

- ✅ **多级头衔体系**：每个玩家可拥有多个头衔，按 ID 独立管理，自由切换启用  
- ✅ **独立后缀系统**：头衔与后缀分离设计，玩家可单独控制后缀启用/关闭  
- ✅ **跨服数据同步**：支持 MySQL，多服务器共享玩家数据  
- ✅ **自动降级存储**：MySQL 失败自动切换 SQLite  
- ✅ **无需重启修改**：管理员可直接修改头衔显示名  
- ✅ **玩家自助管理**：玩家可通过指令管理自己的头衔  
- ✅ **默认头衔系统**：无头衔玩家自动显示“萌新喵”  
- ✅ **自定义玩家颜色**：支持修改玩家ID颜色与聊天颜色  
- ✅ **PlaceholderAPI 支持**  
- ✅ **Minecraft 传统颜色代码支持（&e 等）**  
- ✅ **支持中文**

--- 
## ⚠️ 注意事项
- ❌ 插件目前不支持英文，但是后续会做中英双译 
- ⚠️ 必须使用 PlaceholderAPI 作为前置
--- 
## 头衔以及后缀统一管理 
![CatCraft_1](https://cdn.modrinth.com/data/cached_images/d17878d4406a7ebbf6df97e41bd3ac3633d82fb1.png) 
--- 
## 插件支持自定义玩家ID以及发出的文字颜色 
![CatCraft_2](https://cdn.modrinth.com/data/cached_images/bb9241bde91d7ee56e7b0a5bb4866ac41c121602.png) 
---

## 📦 兼容服务端

- ✅ Paper 1.16.x ~ 1.21.11  
- ✅ Purpur 1.16.x ~ 1.21.11  
- ✅ Spigot 1.16.x ~ 1.21.11  
- ✅ Folia 1.16.x ~ 1.21.11  
- ❌ Bukkit（不推荐）  
- ❌ Sponge / BungeeCord / Velocity（不支持）

---

## 🔧 必要前置

| 前置插件 | 必需性 | 说明 |
|----------|--------|------|
| PlaceholderAPI | ⚠️ 必须 | 变量支持 |
| MySQL | 可选 | 跨服数据库 |

---

## ⚙️ 玩家指令

- `/title list`  查看头衔列表  
- `/title active <ID>`  启用头衔  
- `/title deactive`  关闭头衔  
- `/title remove <ID>`  删除头衔  
- `/title suffixactive`  启用后缀  
- `/title suffixdeactive`  关闭后缀  

---

## 🛠️ 管理员指令

- `/titleadmin give <玩家> <ID> <显示名>`  添加头衔  
- `/titleadmin edit <玩家> <ID> <新名称>`  修改头衔  
- `/titleadmin take <玩家> <ID>`  删除头衔  
- `/titleadmin list <玩家>`  查看头衔  
- `/titleadmin setactive <玩家> <ID>`  强制启用  
- `/titleadmin suffix <玩家> <后缀>`  设置后缀  
- `/titleadmin deactive <玩家>`  关闭头衔  

---

## 🔐 权限节点

| 权限 | 说明 |
|------|------|
| catcraft.admin | 管理员权限 |
| catcraft.title.set | 兼容权限 |

---

## 📌 PlaceholderAPI 变量

- `%catcraft_title%` 👉 当前头衔  
- `%catcraft_suffix%` 👉 当前后缀  

---

## ⚙️ 配置文件（自动生成）

```yaml
mysql:
  enabled: true
  host: localhost
  port: 3306
  db: minecraft
  user: root
  pass: "your_password"

local-database:
  file: catcraft.db

permissions:
  admin: "catcraft.admin"
```

---

## 📊 数据库结构

### player_titles

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(36) | 玩家UUID |
| title_id | INT | 头衔ID |
| title_display | VARCHAR(128) | 显示名称 |
| is_active | BOOLEAN | 是否启用 |

---

### catcraft_titles

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(36) | 玩家UUID |
| suffix | VARCHAR(64) | 后缀 |
| is_active | BOOLEAN | 是否启用 |

---

## 🚀 快速上手

### 👑 管理员

```bash
/titleadmin give [PlayerID] 1 &6[VIP]
/titleadmin edit [PlayerID] 1 &6[超级VIP]
/titleadmin suffix [PlayerID] &7★
/titleadmin list [PlayerID]
```

---

### 🎮 玩家

```bash
/title list
/title active 1
/title suffixactive
/title suffixdeactive
```

---

## ⚠️ 常见问题

**Q：没有头衔显示什么？**  
A：默认显示“萌新喵”

**Q：MySQL失败怎么办？**  
A：自动切换 SQLite，无需处理

**Q：支持RGB吗？**  
A：支持 MiniMessage 格式

---
