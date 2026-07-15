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
 * Uses the proper multi-packet handshake protocol
 */
public class PacketHandler {

    private static final String CHANNEL = "servux:litematics";
    private static final int PACKET_TYPE_TRANSMIT_START = 0;
    private static final int PACKET_TYPE_TRANSMIT_DATA = 1;
    private static final int PACKET_TYPE_TRANSMIT_END = 2;
    private static final int CHUNK_SIZE = 32768; // 32KB chunks for data transmission

    private final LitematicaPlugin plugin;

    public PacketHandler(LitematicaPlugin plugin) {
        this.plugin = plugin;
        // Register the plugin messaging channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    /**
     * Send schematic data to player via Servux protocol multi-packet handshake
     */
    public void sendSchematicData(Player player, String schematicName, byte[] fileData) {
        try {
            boolean debug = plugin.getConfig().getBoolean("debug", true);

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Starting schematic transmission to: " + player.getName());
                plugin.getLogger().info("[DEBUG PACKET] Schematic name: " + schematicName);
                plugin.getLogger().info("[DEBUG PACKET] Total data size: " + fileData.length + " bytes");
            }

            // Step 1: Send TransmitStart packet
            sendTransmitStart(player, schematicName, fileData.length, debug);

            // Step 2: Send TransmitData packets (chunked)
            int chunkCount = (int) Math.ceil((double) fileData.length / CHUNK_SIZE);
            for (int i = 0; i < chunkCount; i++) {
                int start = i * CHUNK_SIZE;
                int length = Math.min(CHUNK_SIZE, fileData.length - start);
                byte[] chunk = new byte[length];
                System.arraycopy(fileData, start, chunk, 0, length);
                sendTransmitData(player, i, chunk, debug);
            }

            // Step 3: Send TransmitEnd packet
            sendTransmitEnd(player, debug);

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Schematic transmission completed!");
            }

        } catch (IOException e) {
            plugin.getLogger().severe("Error transmitting schematic: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            plugin.getLogger().severe("Error during packet transmission: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send TransmitStart packet
     */
    private void sendTransmitStart(Player player, String schematicName, int fileSize, boolean debug) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Packet type: 0 = TransmitStart
        dos.writeByte(PACKET_TYPE_TRANSMIT_START);

        // Filename
        byte[] nameBytes = schematicName.getBytes(StandardCharsets.UTF_8);
        dos.writeShort(nameBytes.length);
        dos.write(nameBytes);

        // File size
        dos.writeLong(fileSize);

        // Chunk count
        int chunkCount = (int) Math.ceil((double) fileSize / CHUNK_SIZE);
        dos.writeInt(chunkCount);

        dos.flush();
        byte[] message = baos.toByteArray();

        if (debug) {
            plugin.getLogger().info("[DEBUG PACKET] TransmitStart packet size: " + message.length + " bytes");
        }

        player.sendPluginMessage(plugin, CHANNEL, message);
    }

    /**
     * Send TransmitData packet (chunk of schematic data)
     */
    private void sendTransmitData(Player player, int chunkIndex, byte[] chunkData, boolean debug) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Packet type: 1 = TransmitData
        dos.writeByte(PACKET_TYPE_TRANSMIT_DATA);

        // Chunk index
        dos.writeInt(chunkIndex);

        // Chunk data length
        dos.writeInt(chunkData.length);

        // Chunk data
        dos.write(chunkData);

        dos.flush();
        byte[] message = baos.toByteArray();

        if (debug) {
            plugin.getLogger().info("[DEBUG PACKET] TransmitData packet [" + chunkIndex + "] size: " + message.length + " bytes");
        }

        player.sendPluginMessage(plugin, CHANNEL, message);
    }

    /**
     * Send TransmitEnd packet
     */
    private void sendTransmitEnd(Player player, boolean debug) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Packet type: 2 = TransmitEnd
        dos.writeByte(PACKET_TYPE_TRANSMIT_END);

        dos.flush();
        byte[] message = baos.toByteArray();

        if (debug) {
            plugin.getLogger().info("[DEBUG PACKET] TransmitEnd packet size: " + message.length + " bytes");
        }

        player.sendPluginMessage(plugin, CHANNEL, message);
    }
}
