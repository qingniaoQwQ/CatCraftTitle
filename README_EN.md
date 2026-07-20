# CatCraftTitle

[中文](README.md) | [English](README_EN.md)

---

## Plugin Description

CatCraftTitle is a **lightweight, GUI‑managed, bilingual (Chinese/English) Minecraft title and suffix management plugin** with full support for MySQL, PostgreSQL, and SQLite. It helps servers easily implement player title systems with cross‑server data synchronization.  
**Built‑in shop system** allows players to purchase titles with coins, and daily sign‑in rewards are also supported.  
The brand‑new **1.2.5 version** introduces an **Admin GUI Control Panel**, a **PlaceholderAPI‑based automatic title system** (attribute plugin support), **real‑time language switching**, and **independent feature toggles**, making administration more efficient and the user experience smoother.

![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

---

## Features

- ✅ **Multi‑level title system** – Each player can own multiple titles, managed independently by ID, and switch activation freely.
- ✅ **Independent suffix system** – Titles and suffixes are separated; players can enable/disable each suffix individually.
- ✅ **Graphical GUI management** – Players can open `/catcraft gui` to manage all their titles through an intuitive interface.
- ✅ **Admin GUI Control Panel** (new in 1.2.5) – Use `/titleadmin panel` to view server status, switch languages, toggle features, and reload configurations with one click – no manual commands needed.
- ✅ **Automatic title system (attribute plugin support)** (new in 1.2.5) – Based on PlaceholderAPI placeholders, automatically grants or removes titles according to any player attribute (level, money, kills, etc.). Supports operators `>=`, `<=`, `==`, `!=`, `>`, `<` for flexible rules.
- ✅ **Real‑time language switching** (new in 1.2.5) – Admins can switch between Chinese and English instantly via the GUI panel, no server restart required.
- ✅ **Independent feature toggles** (new in 1.2.5) – Independently enable/disable RGB gradients, built‑in shop, and attribute plugin support; all are disabled by default and can be turned on as needed.
- ✅ **Cross‑server data synchronization** – Supports MySQL and PostgreSQL for sharing player data across multiple servers.
- ✅ **Local storage fallback** – Automatically falls back to SQLite if MySQL/PostgreSQL fails, so no data is lost.
- ✅ **No restart required for modifications** – Admins can change title display names instantly.
- ✅ **Player self‑service** – Players can manage their titles and suffixes via commands or GUI.
- ✅ **Default title system** – Players without a title automatically see a customisable default title.
- ✅ **RGB gradient support** – Enable colourful gradient titles/suffixes with customisable colours.
- ✅ **Fully internationalised** – One‑click switch between Chinese and English; command help and GUI are fully translated.
- ✅ **PlaceholderAPI support** – Provides `%catcraft_title%`, `%catcraft_suffix%`, and `%catcraft_balance%` placeholders.
- ✅ **Minecraft legacy colour codes** – Supports `&e` format and similar.
- ✅ **Built‑in shop and coin system** – Admins can add items (titles/suffixes); players buy them with coins; daily sign‑in grants coins.
- ✅ **Startup info dashboard** – Console shows server version, database status, and PAPI status.

---
## ⚠️ Important Notes
- ⚠️ **PlaceholderAPI is required as a dependency.**
- ⚠️ If using PostgreSQL, ensure the server has the appropriate driver installed (the plugin includes it).
- ⚠️ **When upgrading from 1.2.3 to 1.2.4 or 1.2.5, it is strongly recommended to delete the `config.yml` file located in `.\plugins\CatCraftTitle\`** so that the plugin regenerates a clean configuration with all new settings. Failing to do so may prevent new features from working correctly or cause unexpected bugs. Player databases are unaffected – no need to delete or migrate them.

---
## Unified Title & Suffix Management – GUI
![GUI](https://cdn.modrinth.com/data/cached_images/3832c52973eff160de8f7d650a80b2ff3b54ab3a.png)

---
## Admin GUI Control Panel (new in 1.2.5)
![admingui](https://cdn.modrinth.com/data/cached_images/49a4414654563c7e2cd23306b8d2025e261e1497.png)  
*(Screenshot is for illustration; actual interface may vary.)*

---
## Customise Player Display Name & Chat Colour
![test](https://cdn.modrinth.com/data/cached_images/67849a0bc9584f0ace606eb79d459997210c60eb.png)

---

## 📦 Supported Server Software

- ✅ Paper 1.16.x – 26.2
- ✅ Purpur 1.16.x – 26.2
- ✅ Spigot 1.16.x – 26.2
- ✅ Folia 1.16.x – 26.2
- ❌ Bukkit (not recommended)
- ❌ Sponge / BungeeCord / Velocity (not supported)

---
## 📦 Supported Databases

- ✅ MySQL
- ✅ PostgreSQL
- ✅ SQLite (built‑in)

---

## Required Dependencies

| Dependency        | Required | Description                                                      |
|-------------------|----------|------------------------------------------------------------------|
| PlaceholderAPI    | ⚠️ Required | Provides placeholder support and automatic title condition parsing |
| MySQL / PostgreSQL | Optional | For cross‑server databases (if not used, SQLite is used automatically) |

---

## Player Commands

| Command | Description |
|---------|-------------|
| `/catcraft gui` | Opens the graphical title management interface |
| `/catcraft shop` | Opens the shop home page (browse titles/suffixes by category) |
| `/catcraft list` | Lists all owned titles and suffixes with their activation status |
| `/catcraft active <ID>` | Activates the title with the given ID |
| `/catcraft deactive` | Deactivates the currently active title and reverts to default |
| `/catcraft suffixactive <ID>` | Activates the suffix with the given ID |
| `/catcraft suffixdeactive` | Deactivates the currently active suffix |
| `/catcraft remove <ID>` | Removes an owned title or suffix (cannot remove the currently active one) |

---

## Admin Commands (require `catcraft.admin` permission)

### Control Panel (new in 1.2.4)
| Command | Description |
|---------|-------------|
| `/titleadmin panel` | Opens the Admin GUI Control Panel to view server status, switch language, toggle features, and reload config |

### Title Management
| Command | Description |
|---------|-------------|
| `/titleadmin give <player> <ID> <display>` | Gives a title to a player (ID must be globally unique) |
| `/titleadmin edit <player> <ID> <new display>` | Changes the display name of a player's title |
| `/titleadmin take <player> <ID>` | Removes a title from a player (cannot remove the active one) |
| `/titleadmin list <player>` | Lists all titles owned by a player with their activation status |
| `/titleadmin setactive <player> <ID>` | Activates a specific title for a player |
| `/titleadmin deactive <player>` | Deactivates the player's currently active title |

### Suffix Management
| Command | Description |
|---------|-------------|
| `/titleadmin suffixgive <player> <ID> <display>` | Gives a suffix to a player (ID must be globally unique) |
| `/titleadmin suffixedit <player> <ID> <new display>` | Changes the display name of a player's suffix |
| `/titleadmin suffixtake <player> <ID>` | Removes a suffix from a player (cannot remove the active one) |
| `/titleadmin suffixlist <player>` | Lists all suffixes owned by a player with their activation status |
| `/titleadmin suffixsetactive <player> <ID>` | Activates a specific suffix for a player |
| `/titleadmin suffixdeactive <player>` | Deactivates the player's currently active suffix |

### Shop Management
| Command | Description |
|---------|-------------|
| `/titleadmin shop add <ID> <type(0/1)> <price> <display>` | Adds an item to the title shop (0) or suffix shop (1) |
| `/titleadmin shop remove <ID> <type>` | Removes an item from the shop |
| `/titleadmin shop setprice <ID> <type> <new price>` | Changes the price of an item |
| `/titleadmin shop list` | Lists all shop items |
| `/titleadmin shop givebalance <player> <amount>` | Adds coins to a player |
| `/titleadmin shop setbalance <player> <amount>` | Sets a player's coin balance to a specific value |
| `/titleadmin shop toggle` | Shows a hint to enable/disable the shop via config.yml |

> Type: `0` = title, `1` = suffix.

---

## Permission Nodes

| Permission | Description |
|------------|-------------|
| `catcraft.admin` | Admin permission – allows use of all `/titleadmin` commands (including `/titleadmin panel`) |
| `catcraft.title.set` | Legacy permission (currently unused) |

> Note: Player commands do not require any extra permission; all players can use them.

---

## PlaceholderAPI Placeholders

| Placeholder | Description |
|-------------|-------------|
| `%catcraft_title%` | The player's currently active title display name (with colour/RGB) |
| `%catcraft_suffix%` | The player's currently active suffix display name (with colour/RGB) |
| `%catcraft_balance%` | The player's current coin balance (integer) |

---

## Configuration (auto‑generated, example)

```yaml
# Language setting (zh / en)
language: zh

# Database configuration
database:
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
  enable-rgb: false          # Enable RGB gradient (default: false)
  attribute-support: false   # Enable automatic title system (default: false)
  check-update: true         # Check for updates

# Shop system configuration
shop:
  enabled: false             # Enable shop (default: false)
  default-balance: 0         # Default coins for new players
  signin-reward: 30          # Daily sign-in reward coins

# Auto‑title rules (only active when attribute-support is true)
auto-rules:
  - id: 1
    type: 0                    # 0 = title, 1 = suffix
    display: "&6[&eVeteran&6]" # Display text of the title/suffix
    condition: "%player_level% >= 30"
  - id: 2
    type: 1
    display: " &8[&7Master&8]"
    condition: "%vault_eco_balance% > 1000"
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
