package me.luicel;

import me.luicel.commands.CustomCommand;
import me.luicel.files.LocationsFile;
import me.luicel.listeners.*;
import me.luicel.models.Game;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.io.IOException;

public class MurderMystery extends JavaPlugin {
    private static MurderMystery instance;
    private static Game game;

    public void onEnable() {
        instance = this;
        game = new Game();
        registerEvents();
        registerCommands();
        try {
            registerFiles();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        getGame().clearAllItemsFromWorld();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new EntityShootBowListener(), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new HangingBreakByEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    private void registerCommands() {
        new Reflections("me.luicel.commands").getSubTypesOf(CustomCommand.class).forEach(command -> {
            try {
                CustomCommand customCommand = command.newInstance();
                getCommand(customCommand.commandName).setExecutor(customCommand);
            } catch (IllegalAccessError | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerFiles() throws IOException, InvalidConfigurationException {
        new LocationsFile("locations.yml");
    }

    public static MurderMystery getInstance() {
        return instance;
    }

    public static Game getGame() {
        return game;
    }
}
