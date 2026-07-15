package com.litematics.plugin.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import com.litematics.plugin.LitematicaPlugin;

import java.nio.charset.StandardCharsets;

/**
 * Handles sending custom payload packets to Litematica clients
 * Using the same channel as Servux: "servux:litematics"
 */
public class PacketHandler {

    private static final String CHANNEL_ID = "servux:litematics";
    private final LitematicaPlugin plugin;

    public PacketHandler(LitematicaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Send schematic data to player via custom payload
     */
    public void sendSchematicData(Player player, String schematicName, byte[] fileData) {
        try {
            boolean debug = plugin.getConfig().getBoolean("debug", true);

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Sending packet to: " + player.getName());
                plugin.getLogger().info("[DEBUG PACKET] Channel: " + CHANNEL_ID);
                plugin.getLogger().info("[DEBUG PACKET] Schematic name: " + schematicName);
                plugin.getLogger().info("[DEBUG PACKET] Data size: " + fileData.length + " bytes");
            }

            // Create buffer with packet data
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

            // Write packet type (PACKET_S2C_NBT_RESPONSE_START = 10)
            buffer.writeVarInt(10);

            // Write transaction ID (0 for this implementation)
            buffer.writeVarInt(0);

            // Write schematic name as UTF string
            byte[] nameBytes = schematicName.getBytes(StandardCharsets.UTF_8);
            buffer.writeVarInt(nameBytes.length);
            buffer.writeBytes(nameBytes);

            // Write schematic data
            buffer.writeBytes(fileData);

            // Get CraftBukkit player and send packet
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().connection.send(new LitematicaCustomPayload(buffer));

            if (debug) {
                plugin.getLogger().info("[DEBUG PACKET] Packet sent successfully!");
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Error sending packet to player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Custom packet payload implementation for Litematica
     */
    public static class LitematicaCustomPayload implements CustomPacketPayload {

        private final FriendlyByteBuf data;
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("servux", "litematics");

        public LitematicaCustomPayload(FriendlyByteBuf buffer) {
            this.data = new FriendlyByteBuf(buffer.copy());
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBytes(data.copy());
        }
    }
}
