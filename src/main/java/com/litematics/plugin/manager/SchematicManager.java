package com.litematics.plugin.manager;

import org.bukkit.entity.Player;
import com.litematics.plugin.LitematicaPlugin;
import com.litematics.plugin.network.PacketHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicManager {

    private final LitematicaPlugin plugin;
    private final PacketHandler packetHandler;

    public SchematicManager(LitematicaPlugin plugin) {
        this.plugin = plugin;
        this.packetHandler = new PacketHandler(plugin);
    }

    public void sendSchematicToPlayer(Player player, String fileName, String prefix) {
        // Get file path
        File schematicsFolder = new File(plugin.getDataFolder(), "schematics");
        File schematicFile = new File(schematicsFolder, fileName);

        boolean debug = plugin.getConfig().getBoolean("debug", true);

        // Check if file exists
        if (!schematicFile.exists()) {
            player.sendMessage("§cError: Schematic file not found!");
            plugin.getLogger().warning("Schematic file not found: " + schematicFile.getAbsolutePath());
            return;
        }

        if (debug) {
            plugin.getLogger().info("[DEBUG] File found: " + schematicFile.getAbsolutePath());
            plugin.getLogger().info("[DEBUG] File size: " + schematicFile.length() + " bytes");
        }

        try {
            // Read file into byte array
            byte[] fileData = readFileToByteArray(schematicFile);

            if (debug) {
                plugin.getLogger().info("[DEBUG] Successfully read file, size: " + fileData.length + " bytes");
            }

            // Send to player
            sendToClient(player, fileName, prefix, fileData);

            player.sendMessage("§aSchematic sent! Check your Litematica schematics folder.");

        } catch (IOException e) {
            player.sendMessage("§cError reading schematic file!");
            plugin.getLogger().severe("Error reading schematic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
    }

    private void sendToClient(Player player, String fileName, String prefix, byte[] fileData) {
        // Create prefixed name
        String prefixedName = prefix + fileName;

        if (plugin.getConfig().getBoolean("debug", true)) {
            plugin.getLogger().info("[DEBUG] Sending to player: " + player.getName());
            plugin.getLogger().info("[DEBUG] Original name: " + fileName);
            plugin.getLogger().info("[DEBUG] Prefixed name: " + prefixedName);
            plugin.getLogger().info("[DEBUG] File data size: " + fileData.length + " bytes");
        }

        // Send via packet handler
        packetHandler.sendSchematicData(player, prefixedName, fileData);
    }
}
