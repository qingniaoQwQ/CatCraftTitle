# CatCraftTitle

[English](README_EN.md) | [中文](README.md)

---

A lightweight and fully-featured Minecraft title plugin that supports both MySQL and SQLite databases, making it easy to manage player titles and suffixes with cross-server synchronization.

---
![CatCraft](https://cdn.modrinth.com/data/cached_images/9996397931ad9af01a23f81aa956ae9dfcf9a80b.png)

## 🎯 Plugin Overview

CatCraftTitle is a player title and suffix management system designed to solve the following needs:

- Multi-level title system: Each player can have multiple titles managed by unique IDs, freely switchable  
- Independent suffix system: Titles and suffixes are separated; players can enable or disable suffixes independently  
- Cross-server synchronization: Supports MySQL for shared data across multiple servers  
- No restart required for changes: Admins can directly edit title display names without recreating entries  
- Player self-management: Players can view, enable, and disable their own titles via commands  
- Default title fallback: Players without a title will display the default “Newbie Meow”  
- Custom player name color support: Allows customization of player name color and chat color  
- Chinese supported: Fully supports Chinese  
- Minecraft legacy color codes supported (e.g. &e for yellow)

---
## ⚠️ Notes
- ❌ The plugin currently does not support English, but Chinese-English bilingual support will be added in the future.
- ⚠️ PlaceholderAPI is required as a prerequisite.
---
## Unified Management of Titles and Suffixes
![CatCraft_1](https://cdn.modrinth.com/data/cached_images/d17878d4406a7ebbf6df97e41bd3ac3633d82fb1.png) 
--- 
## Supports Customizing Player Names and Chat Colors 
![CatCraft_2](https://cdn.modrinth.com/data/cached_images/bb9241bde91d7ee56e7b0a5bb4866ac41c121602.png) 
---

## ✨ Features

| Feature | Description |
|--------|-------------|
| Multi-title management | Each player can own multiple titles identified by numeric IDs |
| Title switching | Players can switch active titles anytime |
| Independent suffix control | Suffix can be enabled/disabled separately |
| Admin quick editing | Modify title display name without deleting |
| Dual database support | MySQL (cross-server) / SQLite (local fallback) |
| PlaceholderAPI support | Provides %catcraft_title% and %catcraft_suffix% placeholders |
| Startup banner | Displays colored server status and database info |
| Multi-version support | Supports Minecraft 1.16.x ~ 1.21.11 |

---

## 📦 Supported Servers

- Paper 1.16.x ~ 1.21.11  
- Purpur 1.16.x ~ 1.21.11  
- Spigot 1.16.x ~ 1.21.11  
- Folia 1.16.x ~ 1.21.11  
- Bukkit (not recommended, missing Paper API support)  
- Sponge / BungeeCord / Velocity (not supported)

---

## 🔧 Requirements

| Dependency | Required | Description |
|------------|----------|-------------|
| PlaceholderAPI | Required | Essential dependency |
| MySQL | Optional | Required for cross-server sync |

If MySQL is not available, the plugin automatically falls back to SQLite local storage.

---

## ⚙️ Player Commands

| Command | Description | Permission |
|--------|-------------|-----------|
| /title list | View your title list and suffix status | Everyone |
| /title active <ID> | Activate a specific title | Everyone |
| /title deactive | Disable current title | Everyone |
| /title remove <ID> | Remove one of your titles (permanent) | Everyone |
| /title suffixactive | Enable suffix display | Everyone |
| /title suffixdeactive | Disable suffix display | Everyone |

---

## 🛠️ Admin Commands

| Command | Description | Permission |
|--------|-------------|-----------|
| /titleadmin give <player> <ID> <display> | Give a title to a player | catcraft.admin |
| /titleadmin edit <player> <ID> <new display> | Edit title display name | catcraft.admin |
| /titleadmin take <player> <ID> | Remove a player's title | catcraft.admin |
| /titleadmin list <player> | View player's titles and suffix | catcraft.admin |
| /titleadmin setactive <player> <ID> | Force activate a title | catcraft.admin |
| /titleadmin suffix <player> <suffix> | Set player suffix | catcraft.admin |
| /titleadmin deactive <player> | Disable player's title | catcraft.admin |

---

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| catcraft.admin | Full admin access |
| catcraft.title.set | Legacy compatibility permission |

By default, players can use all /title commands without extra permissions.

---

## 📌 PlaceholderAPI Variables

| Placeholder | Description | Example |
|-------------|-------------|---------|
| %catcraft_title% | Current active title (colored) | &d[Newbie Meow]&7 |
| %catcraft_suffix% | Active suffix (if enabled) | &7★ |

---

## ⚙️ Config File (Auto-generated)

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

---

## 📊 Database Structure

### player_titles

| Column | Type | Description |
|--------|------|-------------|
| uuid | VARCHAR(36) | Player UUID |
| title_id | INT | Title ID |
| title_display | VARCHAR(128) | Display name |
| is_active | BOOLEAN | Whether active |

---

### catcraft_titles

| Column | Type | Description |
|--------|------|-------------|
| uuid | VARCHAR(36) | Player UUID |
| suffix | VARCHAR(64) | Suffix content |
| is_active | BOOLEAN | Whether enabled |

---

## 🚀 Quick Start

### Admin Workflow

1. Give title  
/titleadmin give [PlayerID] 1 &6[VIP]

2. Edit title  
/titleadmin edit [PlayerID] 1 &6[Super VIP]

3. Set suffix  
/titleadmin suffix [PlayerID] &7★

4. List titles  
/titleadmin list [PlayerID]

---

### Player Workflow

1. View titles  
/title list

2. Activate title  
/title active 1

3. Enable suffix  
/title suffixactive

4. Disable suffix  
/title suffixdeactive

---

## ⚠️ FAQ

Q: What if a player has no title?  
A: Default display is “Newbie Meow”, configurable in TitleManager.DEFAULT_TITLE.

Q: Can title IDs be duplicated?  
A: No. Each player cannot have duplicate IDs.

Q: What if MySQL fails?  
A: The plugin automatically switches to SQLite.

Q: How to show titles in chat?  
A: Use built-in ChatListener or %catcraft_title% placeholder.

Q: Does it support RGB colors?  
A: Yes, MiniMessage format is supported.

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
