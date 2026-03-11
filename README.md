# hytale-plugin-template-inventory

## Overview

Inventory-related state transitions and command-driven interaction checks. This repository is a practical starting point for a Hytale plugin.

## Main entrypoint

- Main class from manifest.json: net.hytaledepot.templates.plugin.inventory.InventoryPluginTemplate
- Includes asset pack: false

## Source layout

- Java sources: src/main/java
- Manifest: src/main/resources/manifest.json
- Runtime jar output: build/libs/hytale-plugin-template-inventory-1.0.0.jar

## Key classes

- InventoryDemoCommand
- InventoryDemoService
- InventoryPluginLifecycle
- InventoryPluginState
- InventoryPluginTemplate
- InventoryStatusCommand

## Commands

- /hdinventorydemo
- /hdinventorystatus

## Build

1. Ensure the server jar is available in one of these locations:
   - HYTALE_SERVER_JAR
   - HYTALE_HOME/install/$patchline/package/game/latest/Server/HytaleServer.jar
   - workspace root HytaleServer.jar
   - libs/HytaleServer.jar
2. Run: ./gradlew clean build
3. Copy build/libs/hytale-plugin-template-inventory-1.0.0.jar into your server mods/ folder.

## License

MIT
