package com.litematics.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.litematics.plugin.LitematicaPlugin;
import com.litematics.plugin.manager.SchematicManager;

public class TestCommand implements CommandExecutor {

    private final LitematicaPlugin plugin;
    private final SchematicManager manager;

    public TestCommand(LitematicaPlugin plugin) {
        this.plugin = plugin;
        this.manager = new SchematicManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("litematics.test")) {
            player.sendMessage("You don't have permission to use this command!");
            return true;
        }

        // Load config
        String schematicFileName = plugin.getConfig().getString("test-schematic", "test.litematica");
        String prefix = plugin.getConfig().getString("prefix", "Tested - ");
        boolean debug = plugin.getConfig().getBoolean("debug", true);

        if (debug) {
            plugin.getLogger().info("[DEBUG] Loading schematic: " + schematicFileName);
            plugin.getLogger().info("[DEBUG] Prefix: " + prefix);
        }

        // Send schematic to player
        manager.sendSchematicToPlayer(player, schematicFileName, prefix);

        return true;
    }
}