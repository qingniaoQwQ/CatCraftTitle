# CatCraftTitle

[English](README_EN.md) | [中文](README.md)

---

##  Plugin Overview

CatCraftTitle is a **lightweight, fully-featured Minecraft title and suffix management plugin** with GUI support, full bilingual support (Chinese/English), and multiple database backends (MySQL, PostgreSQL, SQLite). It enables servers to easily implement a player title system with cross-server synchronization.  
**Built-in shop system** allows players to purchase titles with coins, plus a daily sign-in feature.

![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

##  Features

- ✅ **Multi-title system**: Each player can own multiple titles, managed by unique IDs, freely switchable
- ✅ **Independent suffix system**: Titles and suffixes are separated; players can enable/disable each suffix individually
- ✅ **Graphical GUI**: Players can use `/catcraft gui` to manage all titles intuitively
- ✅ **Cross-server sync**: Supports MySQL / PostgreSQL for shared data across multiple servers
- ✅ **Automatic failover**: Falls back to SQLite if MySQL/PostgreSQL fails, no data loss
- ✅ **No restart required**: Admins can edit title display names on the fly
- ✅ **Player self-service**: Players manage their own titles and suffixes via commands or GUI
- ✅ **Customizable default title**: Players without a title display a configurable fallback
- ✅ **RGB gradient support**: Enable colorful gradient titles/suffixes with custom colors
- ✅ **Full localization**: One-click switch between Chinese and English, including command help and GUI
- ✅ **PlaceholderAPI support**: Provides `%catcraft_title%`, `%catcraft_suffix%` and `%catcraft_balance%` placeholders
- ✅ **Legacy color codes support**: `&e` format etc.
- ✅ **Built-in shop & coin system**: Admins can add items (titles/suffixes) to the shop; players purchase with coins; daily sign-in rewards coins
- ✅ **Startup info banner**: Console displays server version, database status, PAPI status

---
## ⚠️ Notes
- ⚠️ **PlaceholderAPI is required** as a prerequisite
- ⚠️ If using PostgreSQL, ensure the server has the driver (already bundled)
- ⚠️ **When upgrading from 1.2.2 to 1.2.3, please delete `.\plugins\CatCraftTitle\config.yml`** and let the plugin regenerate it, otherwise new feature messages will not appear. Player database is unaffected.

---
## Unified Management of Titles and Suffixes - GUI Interface
![guien](https://cdn.modrinth.com/data/cached_images/81b354ecabfd1952b60e249445f25473005830af.png)
---
## Supports Customizing Player Names and Chat Colors
![test](https://cdn.modrinth.com/data/cached_images/67849a0bc9584f0ace606eb79d459997210c60eb.png)
---

## 📦 Supported Servers

- ✅ Paper 1.16.x ~ 26.2
- ✅ Purpur 1.16.x ~ 26.2
- ✅ Spigot 1.16.x ~ 26.2
- ✅ Folia 1.16.x ~ 26.2
- ❌ Bukkit (not recommended)
- ❌ Sponge / BungeeCord / Velocity (not supported)

---
## 📦 Supported Databases

- ✅ MySQL
- ✅ PostgreSQL
- ✅ SQLite (built-in)

---

##  Requirements

| Dependency | Required | Description |
|------------|----------|-------------|
| PlaceholderAPI | ⚠️ Required | Variable support |
| MySQL / PostgreSQL | Optional | For cross-server sync (if not used, SQLite is used automatically) |

---

##  Player Commands

| Command | Description |
|---------|-------------|
| `/catcraft gui` | Open GUI management interface |
| `/catcraft shop` | Open shop home (title/suffix categories) |
| `/catcraft list` | List all owned titles and suffixes with status |
| `/catcraft active <ID>` | Activate a title by ID |
| `/catcraft deactive` | Deactivate current title, revert to default |
| `/catcraft suffixactive <ID>` | Activate a suffix by ID |
| `/catcraft suffixdeactive` | Deactivate current suffix |
| `/catcraft remove <ID>` | Remove a title or suffix (cannot remove active one) |

---

##  Admin Commands (require `catcraft.admin`)

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

### Shop Management (new)
| Command | Description |
|---------|-------------|
| `/titleadmin shop add <ID> <type(0/1)> <price> <display>` | Add an item to title or suffix shop |
| `/titleadmin shop remove <ID> <type>` | Remove an item from the shop |
| `/titleadmin shop setprice <ID> <type> <new price>` | Change item price |
| `/titleadmin shop list` | List all shop items |
| `/titleadmin shop givebalance <player> <amount>` | Add coins to a player |
| `/titleadmin shop setbalance <player> <amount>` | Set a player's coins to a specific value |
| `/titleadmin shop toggle` | Hint to toggle shop via config file |

> `type`: `0` for title, `1` for suffix.

---

##  Permissions

| Permission | Description |
|------------|-------------|
| `catcraft.admin` | Admin access, allows all `/titleadmin` commands |
| `catcraft.title.set` | Legacy compatibility (not used) |

> Note: Player commands do not require any permission.

---

##  PlaceholderAPI Variables

| Variable | Description |
|----------|-------------|
| `%catcraft_title%` | Current active title (colored/RGB) |
| `%catcraft_suffix%` | Current active suffix (colored/RGB) |
| `%catcraft_balance%` | Player's current coin balance (integer) |

---

##  Config File (auto-generated, example)

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

# Shop system configuration (new)
shop:
  enabled: true              # Enable shop (disables all shop features if false)
  default-balance: 0         # Default coins for new players
  signin-reward: 30          # Daily sign-in reward amount

# GUI custom titles (existing)
gui:
  home-title: "§6CatCraft Title Manager · Home"
  title-page: "§6Title Management"
  suffix-page: "§6Suffix Management"

# All messages (bilingual, full key list in actual config)
messages:
  zh: { ... }
  en: { ... }
```

---

## Database Structure (unified table `player_titles`)

| Column | Type | Description |
|--------|------|-------------|
| uuid | VARCHAR(36) | Player UUID |
| title_id | INT | Title ID (globally unique) |
| title_display | VARCHAR(128) | Display name |
| type | INT | 0=title, 1=suffix |
| is_active | BOOLEAN | Whether active |

> Titles and suffixes are stored in the same table, distinguished by `type`, ensuring global ID uniqueness per player.

---


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

**Q: After updating, shop messages don't appear?**  
A: Delete `config.yml` and restart to regenerate the file with the new message keys.

---

##  License

MIT License — free to use, modify, and distribute with attribution.

---

##  Author

QingNiaoQaQ (CatCraft Team)  
GitHub: https://github.com/qingniaoQwQ

---

## 🙏 Credits

Thanks to all users and contributors!
