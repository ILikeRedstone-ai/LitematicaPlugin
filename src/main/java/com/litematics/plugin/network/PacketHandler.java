package com.litematics.plugin.network;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.litematics.plugin.LitematicaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servux protocol packet handler for sending schematics to Litematica
 * Implements proper Servux packet structure for MC 1.21+
 */
public class PacketHandler {

    private static final String CHANNEL = "servux:litematics";
    private static final byte PACKET_S2C_SCHEMATIC_DOWNLOAD = 1;
    private final LitematicaPlugin plugin;

    public PacketHandler(LitematicaPlugin plugin) {
        this.plugin = plugin;
        // Register the plugin messaging channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    /**
     * Send schematic data to player via Servux protocol
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

            // Create message bytearray using proper Servux protocol
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // Write packet type (PACKET_S2C_SCHEMATIC_DOWNLOAD = 1)
            dos.writeByte(PACKET_S2C_SCHEMATIC_DOWNLOAD);

            // Write schematic name length and string
            byte[] nameBytes = schematicName.getBytes(StandardCharsets.UTF_8);
            writeVarInt(dos, nameBytes.length);
            dos.write(nameBytes);

            // Write file data length and data
            writeVarInt(dos, fileData.length);
            dos.write(fileData);

            dos.flush();
            byte[] message = baos.toByteArray();

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Message size: " + message.length + " bytes");
                plugin.getLogger().info("[DEBUG PACKET] Packet type: SCHEMATIC_DOWNLOAD (1)");
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

    /**
     * Write a variable-length integer (VarInt) to the DataOutputStream
     * This is the standard Minecraft protocol format for variable-length integers
     */
    private void writeVarInt(DataOutputStream dos, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            dos.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        dos.writeByte(value & 0x7F);
    }
}
