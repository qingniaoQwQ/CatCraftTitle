# CatCraftTitle

[English](README_EN.md) | [中文](README.md)

## Introduction

**CatCraftTitle** is a Minecraft player title plugin built for **Paper / Purpur / Spigot**, supporting **MySQL + SQLite dual database auto-switching** for cross-server synchronization. Players can manage their own titles and suffixes, and administrators can assign/modify them flexibly.

## Features

- ✅ Multiple titles per player (managed by ID), freely switch active one
- ✅ Independent suffix system with toggle on/off
- ✅ Admin can edit title display name directly, no need to delete & re-add
- ✅ MySQL / SQLite dual database support with automatic fallback
- ✅ PlaceholderAPI integration
- ✅ Colorful server info banner on startup
- ✅ Multi-version support (1.16.1-1.21.11)

## Supported Server Software

- ✅ Purpur 1.16.1 ~ 1.21.11
- ✅ Spigot 1.16.1 ~ 1.21.11
- ✅ Paper 1.16.1 ~ 1.21.11
- ✅ CraftBukkit 1.16.1 ~ 1.21.11

## Dependencies

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 

---

## Player Commands

| Command | Description |
|---------|-------------|
| `/title list` | View your owned titles and suffix status |
| `/title active <ID>` | Activate a title by ID |
| `/title deactive` | Deactivate current title, revert to default |
| `/title remove <ID>` | Remove a title you own (cannot remove active one) |
| `/title suffixactive` | Enable suffix display |
| `/title suffixdeactive` | Disable suffix display |

---

## Admin Commands

> Requires permission: `catcraft.admin`

| Command | Description |
|---------|-------------|
| `/titleadmin give <player> <ID> <display>` | Give a title to a player (ID must be unique) |
| `/titleadmin edit <player> <ID> <newDisplay>` | Edit an existing title display name |
| `/titleadmin take <player> <ID>` | Force remove a title from a player |
| `/titleadmin list <player>` | List all titles and suffix status of a player |
| `/titleadmin setactive <player> <ID>` | Force activate a title for a player |
| `/titleadmin suffix <player> <suffix>` | Set suffix content for a player (keeps active status) |
| `/titleadmin deactive <player>` | Deactivate a player's title |

---

## PlaceholderAPI Variables

| Variable | Description |
|----------|-------------|
| `%catcraft_title%` | Player's currently active title (colored) |
| `%catcraft_suffix%` | Player's currently active suffix (if enabled, else empty) |

---
## Build

| Build |
|----------|
| Output JAR is placed in build/libs/CatCraftTitle-*.jar|

---
## Build

| License |
|----------|
| This project is licensed under the MIT License – see the LICENSE file for details. |

---
## Configuration

Plugin generates `config.yml` on first startup. Key options:

```yaml
mysql:
  enabled: true          # true tries MySQL, falls back to SQLite on failure
  host: localhost
  port: 3306
  db: minecraft
  user: root
  pass: "your_password"  # Replace with actual password

local-database:
  file: catcraft.db      # SQLite file name

permissions:
  admin: "catcraft.admin"
