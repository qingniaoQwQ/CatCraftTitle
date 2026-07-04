# CatCraftTitle

[中文](README.md) | [English](README_EN.md)

---

## 🎯 插件作用

CatCraftTitle 是一个**轻量级、支持GUI管理、中英双语、功能完善的 Minecraft 头衔与后缀管理插件**，支持 MySQL / PostgreSQL / SQLite 三种数据库，帮助服务器轻松实现玩家头衔系统，并支持跨服同步。

![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

## ✨ 功能特点

- ✅ **多级头衔体系**：每个玩家可拥有多个头衔，按 ID 独立管理，自由切换启用
- ✅ **独立后缀系统**：头衔与后缀分离设计，玩家可单独控制每个后缀的启用/关闭
- ✅ **图形化 GUI 管理**：玩家可通过 `/title gui` 打开直观的界面管理所有称号
- ✅ **跨服数据同步**：支持 MySQL / PostgreSQL，多服务器共享玩家数据
- ✅ **本地存储备选**：MySQL/PostgreSQL 失败自动切换 SQLite，数据不丢失
- ✅ **无需重启修改**：管理员可直接修改头衔显示名，即时生效
- ✅ **玩家自助管理**：玩家可通过指令或 GUI 管理自己的头衔和后缀
- ✅ **默认头衔系统**：无头衔玩家自动显示自定义默认头衔（可配置）
- ✅ **RGB 渐变色支持**：可启用炫彩渐变头衔/后缀，颜色自定义
- ✅ **完全国际化**：支持中文/英文一键切换，命令帮助和 GUI 界面全翻译
- ✅ **PlaceholderAPI 支持**：提供 `%catcraft_title%` 和 `%catcraft_suffix%` 变量
- ✅ **Minecraft 传统颜色代码支持**：`&e` 等格式
- ✅ **启动信息看板**：控制台显示服务器版本、数据库状态、PAPI 状态

---
## ⚠️ 注意事项
- ⚠️ **必须使用 PlaceholderAPI 作为前置**
- ⚠️ 若使用 PostgreSQL，请确保服务端已安装对应驱动（插件已内置）
---
## 头衔以及后缀统一管理 - GUI界面
![GUI](https://cdn.modrinth.com/data/cached_images/3832c52973eff160de8f7d650a80b2ff3b54ab3a.png)
---
## 插件支持自定义玩家ID以及发出的文字颜色 
![test](https://cdn.modrinth.com/data/cached_images/67849a0bc9584f0ace606eb79d459997210c60eb.png)
---

## 📦 兼容服务端

- ✅ Paper 1.16.x ~ 1.21.11
- ✅ Purpur 1.16.x ~ 1.21.11
- ✅ Spigot 1.16.x ~ 1.21.11
- ✅ Folia 1.16.x ~ 1.21.11
- ❌ Bukkit（不推荐）
- ❌ Sponge / BungeeCord / Velocity（不支持）

---
## 📦 兼容数据库

- ✅ MySQL
- ✅ PostgreSQL

---

## 🔧 必要前置

| 前置插件 | 必需性 | 说明 |
|----------|--------|------|
| PlaceholderAPI | ⚠️ 必须 | 变量支持 |
| MySQL / PostgreSQL | 可选 | 跨服数据库 |

---

## ⚙️ 玩家指令

| 指令 | 说明 |
|------|------|
| `/title gui` | 打开图形化称号管理界面 |
| `/title list` | 查看自己拥有的所有头衔和后缀及其激活状态 |
| `/title active <ID>` | 激活指定ID的头衔 |
| `/title deactive` | 停用当前激活的头衔，恢复默认 |
| `/title suffixactive <ID>` | 激活指定ID的后缀 |
| `/title suffixdeactive` | 停用当前激活的后缀 |
| `/title remove <ID>` | 移除自己拥有的某个称号（头衔或后缀），不能移除当前激活的 |

---

## 🛠️ 管理员指令（需 `catcraft.admin` 权限）

### 头衔管理
| 指令 | 说明 |
|------|------|
| `/titleadmin give <玩家> <ID> <显示名>` | 为玩家添加一个头衔（ID必须全局唯一） |
| `/titleadmin edit <玩家> <ID> <新显示名>` | 修改玩家某个头衔的显示名 |
| `/titleadmin take <玩家> <ID>` | 移除玩家的某个头衔（不能移除激活的） |
| `/titleadmin list <玩家>` | 查看玩家拥有的所有头衔及其激活状态 |
| `/titleadmin setactive <玩家> <ID>` | 激活玩家的某个头衔 |
| `/titleadmin deactive <玩家>` | 停用玩家当前激活的头衔 |

### 后缀管理
| 指令 | 说明 |
|------|------|
| `/titleadmin suffixgive <玩家> <ID> <显示名>` | 为玩家添加一个后缀（ID必须全局唯一） |
| `/titleadmin suffixedit <玩家> <ID> <新显示名>` | 修改玩家某个后缀的显示名 |
| `/titleadmin suffixtake <玩家> <ID>` | 移除玩家的某个后缀（不能移除激活的） |
| `/titleadmin suffixlist <玩家>` | 查看玩家拥有的所有后缀及其激活状态 |
| `/titleadmin suffixsetactive <玩家> <ID>` | 激活玩家的某个后缀 |
| `/titleadmin suffixdeactive <玩家>` | 停用玩家当前激活的后缀 |

---

## 🔐 权限节点

| 权限 | 说明 |
|------|------|
| `catcraft.admin` | 管理员权限，允许使用所有 `/titleadmin` 命令 |
| `catcraft.title.set` | 兼容旧版权限（暂未使用） |

> 注：玩家指令无需额外权限，任何玩家均可使用。

---

## 📌 PlaceholderAPI 变量

| 变量 | 说明 |
|------|------|
| `%catcraft_title%` | 玩家当前激活的头衔显示名（含颜色/RGB） |
| `%catcraft_suffix%` | 玩家当前激活的后缀显示名（含颜色/RGB） |

---

## ⚙️ 配置文件（自动生成）

```yaml
# 语言设置 (zh / en)
language: zh

# 数据库配置
database:
  # 支持类型: mysql, postgresql, sqlite
  type: sqlite
  mysql:
    host: localhost
    port: 3306
    db: minecraft
    user: root
    pass: "your_password"
  postgresql:
    host: localhost
    port: 5432
    db: minecraft
    user: root
    pass: "your_password"
  sqlite:
    file: catcraft.db

permissions:
  admin: "catcraft.admin"

settings:
  debug: true
  enable-rgb: false          # 是否启用RGB渐变
  check-update: true         # 是否检查更新
  default-title: "&d[&d萌新喵]&7"   # 默认头衔
  rgb-colors:                # 自定义RGB颜色数组
    - "&x&F&F&0&0&0&0"
    - "&x&F&F&4&0&F&F"
    - "&x&8&0&4&0&F&F"

# GUI 标题自定义
gui:
  home-title: "§6CatCraft称号管理 · 主页"
  title-page: "§6头衔管理"
  suffix-page: "§6后缀管理"

# 所有消息（中英双语）
messages:
  zh: { ... }
  en: { ... }
```

---

## 📊 数据库结构（统一表 `player_titles`）

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(36) | 玩家UUID |
| title_id | INT | 称号ID（全局唯一） |
| title_display | VARCHAR(128) | 显示名称 |
| type | INT | 0=头衔, 1=后缀 |
| is_active | BOOLEAN | 是否启用 |

> 头衔与后缀存储在同一张表中，通过 `type` 字段区分，确保 ID 全局唯一。

---

## 🚀 快速上手

### 👑 管理员

```bash
# 添加头衔
/titleadmin give QingNiao 1 &6[VIP]

# 修改头衔
/titleadmin edit QingNiao 1 &6[超级VIP]

# 添加后缀
/titleadmin suffixgive QingNiao 100 &7★

# 激活后缀
/titleadmin suffixsetactive QingNiao 100

# 查看玩家所有称号
/titleadmin list QingNiao
```

---

### 🎮 玩家

```bash
# 打开GUI
/title gui

# 查看所有称号
/title list

# 激活头衔ID 1
/title active 1

# 激活后缀ID 100
/title suffixactive 100

# 停用后缀
/title suffixdeactive
```

---

## ⚠️ 常见问题

**Q：没有头衔显示什么？**  
A：默认显示配置的 `default-title`，默认为 `&d[&d萌新喵]&7`

**Q：MySQL/PostgreSQL 连接失败怎么办？**  
A：自动切换 SQLite，无需手动处理

**Q：支持 RGB 颜色吗？**  
A：支持。将 `settings.enable-rgb` 设为 `true`，并可自定义 `rgb-colors` 数组

**Q：如何切换语言？**  
A：修改 `language` 为 `zh` 或 `en`，重启服务器即可

**Q：更新检测报 SSL 错误？**  
A：请升级 Java 版本至 11 或以上；或手动导入 GitHub SSL 证书

---

## 📜 开源协议

MIT License — 允许自由使用、修改、分发，需保留原作者声明。

---

## 👨‍💻 作者

QingNiaoQaQ (CatCraft Team)  
GitHub: https://github.com/qingniaoQwQ

---

## 🙏 鸣谢

感谢所有使用和贡献本插件的玩家与开发者！

---
