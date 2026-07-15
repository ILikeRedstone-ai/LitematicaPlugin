package com.litematics.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.litematics.plugin.command.TestCommand;

import java.io.File;

public class LitematicaPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Create schematics folder if it doesn't exist
        File schematicsFolder = new File(getDataFolder(), "schematics");
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
            getLogger().info("Created schematics folder at: " + schematicsFolder.getAbsolutePath());
        }

        // Register command
        getCommand("test").setExecutor(new TestCommand(this));

        getLogger().info("LitematicaPlugin enabled!");
        getLogger().info("Schematics folder: " + schematicsFolder.getAbsolutePath());
        getLogger().info("Config file: " + new File(getDataFolder(), "config.yml").getAbsolutePath());
    }

    @Override
    public void onDisable() {
        getLogger().info("LitematicaPlugin disabled!");
    }
}