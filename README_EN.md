# CatCraftTitle

[English](README_EN.md) | [中文](README.md)

---

## 🎯 Plugin Overview

CatCraftTitle is a **lightweight, fully-featured Minecraft title and suffix management plugin** with GUI support, full bilingual support (Chinese/English), and multiple database backends (MySQL, PostgreSQL, SQLite). It enables servers to easily implement a player title system with cross-server synchronization.

![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

## ✨ Features

- ✅ **Multi-title system**: Each player can own multiple titles, managed by unique IDs, freely switchable
- ✅ **Independent suffix system**: Titles and suffixes are separated; players can enable/disable each suffix individually
- ✅ **Graphical GUI**: Players can use `/title gui` to manage all titles intuitively
- ✅ **Cross-server sync**: Supports MySQL / PostgreSQL for shared data across multiple servers
- ✅ **Automatic failover**: Falls back to SQLite if MySQL/PostgreSQL fails, no data loss
- ✅ **No restart required**: Admins can edit title display names on the fly
- ✅ **Player self-service**: Players manage their own titles and suffixes via commands or GUI
- ✅ **Customizable default title**: Players without a title display a configurable fallback
- ✅ **RGB gradient support**: Enable colorful gradient titles/suffixes with custom colors
- ✅ **Full localization**: One-click switch between Chinese and English, including command help and GUI
- ✅ **PlaceholderAPI support**: Provides `%catcraft_title%` and `%catcraft_suffix%` placeholders
- ✅ **Legacy color codes support**: `&e` format etc.
- ✅ **Startup info banner**: Console displays server version, database status, PAPI status

---
## ⚠️ Notes
- ⚠️ **PlaceholderAPI is required** as a prerequisite
- ⚠️ If using PostgreSQL, ensure the server has the driver (already bundled)
---
## Unified Management of Titles and Suffixes - GUI Interface
![guien](https://cdn.modrinth.com/data/cached_images/81b354ecabfd1952b60e249445f25473005830af.png)
---
## Supports Customizing Player Names and Chat Colors
![test](https://cdn.modrinth.com/data/cached_images/67849a0bc9584f0ace606eb79d459997210c60eb.png)
---

## 📦 Supported Servers

- ✅ Paper 1.16.x ~ 1.21.11
- ✅ Purpur 1.16.x ~ 1.21.11
- ✅ Spigot 1.16.x ~ 1.21.11
- ✅ Folia 1.16.x ~ 1.21.11
- ❌ Bukkit (not recommended)
- ❌ Sponge / BungeeCord / Velocity (not supported)

---
## 📦 Supported Databases

- ✅ MySQL
- ✅ PostgreSQL

---

## 🔧 Requirements

| Dependency | Required | Description |
|------------|----------|-------------|
| PlaceholderAPI | ⚠️ Required | Variable support |
| MySQL / PostgreSQL | Optional | For cross-server sync |

---

## ⚙️ Player Commands

| Command | Description |
|---------|-------------|
| `/title gui` | Open GUI management interface |
| `/title list` | List all owned titles and suffixes with status |
| `/title active <ID>` | Activate a title by ID |
| `/title deactive` | Deactivate current title, revert to default |
| `/title suffixactive <ID>` | Activate a suffix by ID |
| `/title suffixdeactive` | Deactivate current suffix |
| `/title remove <ID>` | Remove a title or suffix (cannot remove active one) |

---

## 🛠️ Admin Commands (require `catcraft.admin`)

### Title Management
| Command | Description |
|---------|-------------|
| `/titleadmin give <player> <ID> <display>` | Add a title to a player (ID must be globally unique) |
| `/titleadmin edit <player> <ID> <new display>` | Edit a title's display name |
| `/titleadmin take <player> <ID>` | Remove a title from a player (cannot remove active one) |
| `/titleadmin list <player>` | List all titles of a player with status |
| `/titleadmin setactive <player> <ID>` | Force activate a title for a player |
| `/titleadmin deactive <player>` | Deactivate player's current title |

### Suffix Management
| Command | Description |
|---------|-------------|
| `/titleadmin suffixgive <player> <ID> <display>` | Add a suffix to a player (ID must be globally unique) |
| `/titleadmin suffixedit <player> <ID> <new display>` | Edit a suffix's display name |
| `/titleadmin suffixtake <player> <ID>` | Remove a suffix from a player (cannot remove active one) |
| `/titleadmin suffixlist <player>` | List all suffixes of a player with status |
| `/titleadmin suffixsetactive <player> <ID>` | Force activate a suffix for a player |
| `/titleadmin suffixdeactive <player>` | Deactivate player's current suffix |

---

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `catcraft.admin` | Admin access, allows all `/titleadmin` commands |
| `catcraft.title.set` | Legacy compatibility (not used) |

> Note: Player commands do not require any permission.

---

## 📌 PlaceholderAPI Variables

| Variable | Description |
|----------|-------------|
| `%catcraft_title%` | Current active title (colored/RGB) |
| `%catcraft_suffix%` | Current active suffix (colored/RGB) |

---

## ⚙️ Config File (auto-generated)

```yaml
# Language setting (zh / en)
language: zh

# Database configuration
database:
  # Supported types: mysql, postgresql, sqlite
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
  enable-rgb: false          # Enable RGB gradient
  check-update: true         # Enable update check
  default-title: "&d[&dNewbie Meow]&7"   # Default fallback title
  rgb-colors:                # Custom RGB color array
    - "&x&F&F&0&0&0&0"
    - "&x&F&F&4&0&F&F"
    - "&x&8&0&4&0&F&F"

# GUI custom titles
gui:
  home-title: "§6CatCraft Title Manager · Home"
  title-page: "§6Title Management"
  suffix-page: "§6Suffix Management"

# All messages (bilingual)
messages:
  zh: { ... }
  en: { ... }
```

---

## 📊 Database Structure (unified table `player_titles`)

| Column | Type | Description |
|--------|------|-------------|
| uuid | VARCHAR(36) | Player UUID |
| title_id | INT | Title ID (globally unique) |
| title_display | VARCHAR(128) | Display name |
| type | INT | 0=title, 1=suffix |
| is_active | BOOLEAN | Whether active |

> Titles and suffixes are stored in the same table, distinguished by `type`, ensuring global ID uniqueness per player.

---

## 🚀 Quick Start

### 👑 Admin

```bash
# Add a title
/titleadmin give QingNiao 1 &6[VIP]

# Edit title
/titleadmin edit QingNiao 1 &6[Super VIP]

# Add a suffix
/titleadmin suffixgive QingNiao 100 &7★

# Activate suffix
/titleadmin suffixsetactive QingNiao 100

# List all titles/suffixes of a player
/titleadmin list QingNiao
```

---

### 🎮 Player

```bash
# Open GUI
/title gui

# List all titles/suffixes
/title list

# Activate title ID 1
/title active 1

# Activate suffix ID 100
/title suffixactive 100

# Deactivate suffix
/title suffixdeactive
```

---

## ⚠️ FAQ

**Q: What if a player has no title?**  
A: The plugin displays the configured `default-title` (default: `&d[&dNewbie Meow]&7`)

**Q: What if MySQL/PostgreSQL connection fails?**  
A: The plugin automatically falls back to SQLite – no manual action needed.

**Q: Does it support RGB colors?**  
A: Yes. Set `settings.enable-rgb` to `true` and customize `rgb-colors` array.

**Q: How to switch language?**  
A: Change `language` to `zh` or `en` and restart the server.

**Q: Update check gives SSL error?**  
A: Please upgrade Java to version 11 or higher; or manually import GitHub SSL certificate.

---

## 📜 License

MIT License — free to use, modify, and distribute with attribution.

---

## 👨‍💻 Author

QingNiaoQaQ (CatCraft Team)  
GitHub: https://github.com/qingniaoQwQ

---

## 🙏 Credits

Thanks to all users and contributors!
