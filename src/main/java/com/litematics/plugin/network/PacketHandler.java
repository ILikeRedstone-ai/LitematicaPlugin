package com.litematics.plugin.network;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.litematics.plugin.LitematicaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Simplified packet handler for sending schematics
 * Uses Plugin Messaging Channel instead of direct NMS
 */
public class PacketHandler {

    private static final String CHANNEL = "servux:litematics";
    private final LitematicaPlugin plugin;

    public PacketHandler(LitematicaPlugin plugin) {
        this.plugin = plugin;
        // Register the plugin messaging channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    /**
     * Send schematic data to player via plugin messaging
     */
    public void sendSchematicData(Player player, String schematicName, byte[] fileData) {
        try {
            boolean debug = plugin.getConfig().getBoolean("debug", true);

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Sending packet to: " + player.getName());
                plugin.getLogger().info("[DEBUG PACKET] Channel: " + CHANNEL);
                plugin.getLogger().info("[DEBUG PACKET] Schematic name: " + schematicName);
                plugin.getLogger().info("[DEBUG PACKET] Data size: " + fileData.length + " bytes");
            }

            // Create message bytearray
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // Write packet type (PACKET_S2C_NBT_RESPONSE_START = 10)
            dos.writeInt(10);

            // Write transaction ID (0)
            dos.writeInt(0);

            // Write schematic name
            dos.writeUTF(schematicName);

            // Write file data length
            dos.writeInt(fileData.length);
            dos.write(fileData);

            dos.flush();
            byte[] message = baos.toByteArray();

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Message size: " + message.length + " bytes");
            }

            // Send via plugin messaging
            player.sendPluginMessage(plugin, CHANNEL, message);

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Packet sent successfully via plugin messaging!");
            }

        } catch (IOException e) {
            plugin.getLogger().severe("Error creating packet message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            plugin.getLogger().severe("Error sending packet to player: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
