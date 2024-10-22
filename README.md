# FinalChunkLoader

This is a plugin for Crucible MC1.7.10 servers that allows players to make chunk loaders.

This plugins is a remake of [BetterChunkLoader](https://github.com/EverNife/BetterChunkLoader).

It is fully customizable, all messages can be changed in the config file, and it has a GUI (editable) to create and edit chunk loaders.

It has an online-only (work only when the player is online), and an always-on chunk loader (works even if the player is offline).

The amount of chunks that can be loaded per player can be set manually with ingame commands and with permissions.

It provides ingame-commands for admins so they can remove all chunk loaders for a player, and chunk loaders can be disabled by disabling the plugin.

This plugin requires BCLForgeLib, a server-side Forge mod library, so it can be added to any modpack without requiring the mod on client-side.

#### Installation
- Download [FinalChunkLoader](https://github.com/EverNife/FinalChunkLoader/releases) and place it on the plugins folder
- Download [EverNifeCore](https://github.com/EverNife/EverNifeCore) and place it on the plugins folder.
- Download BCLForgeLib and place it on the mods folder.

#### Commands
- /fcl <info|list|chunks|delete|purge|reload|enable|disable> - main commands
- /fcl info - shows generic info about the plugin
- /fcl list (own|PlayerName|all) [page] - list your own, the player name, or all chunk loaders
- /fcl chunks (get|add|set) (PlayerName) (alwayson|onlineonly) (amount) - show, add, or set the amount of chunks that can be loaded by the specified player
- /fcl delete (PlayerName) - delete all the chunk loaders for the specified player
- /fcl purge - purge all the invalid chunk loaders. Useful if you reset the world
- /fcl <enable|disable> - enable or disable the plugin

#### Permissions
- finalchunkloader.onlineonly - Permission to create new onlineonly chunk loaders
- finalchunkloader.alwayson - Permission to create new alwayson chunk loaders
- finalchunkloader.list.own - List own chunk loaders
- finalchunkloader.unlimitedchunks - Override chunks amount limits
- finalchunkloader.list.others - List others chunk loaders
- finalchunkloader.edit - Edit others chunk loaders
- finalchunkloader.adminloader - Create admin chunk loaders
- finalchunkloader.info - Show general statistics
- finalchunkloader.delete - Delete player chunk loaders
- finalchunkloader.chunks - Manage players chunks amount
- finalchunkloader.purge - Allows the use of the purge command
- finalchunkloader.reload - Reload the plugin
- finalchunkloader.enable - Enable the plugin
- finalchunkloader.disable - Disable the plugin

#### How to use
- Place a iron block or a diamond block, then right click with a blaze rod. 
- A GUI will appear. Click the range. 
- If you have the adminloader permission, you can create admin chunk loaders by shift+right clicking the diamond block with a blaze rod. 
- Left-click a chunk loader to show basic information about the chunk loader. 
- Right click a chunk loader while holding the ROD to edit its range or disable it.

