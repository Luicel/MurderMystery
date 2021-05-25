package me.luicel.files;

import me.luicel.MurderMystery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class LocationsFile {
    private static YamlConfiguration locations;
    private static File file;

    private String DATA_FOLDER = MurderMystery.getInstance().getDataFolder() + "/";

    public LocationsFile(String fileName) {
        locations = createConfig(fileName);
        file = getFile(fileName);
        registerLobbySpawnPoint();
        registerGameSpawnPoints();
        registerGoldSpawnPoints();
    }

    public YamlConfiguration createConfig(String fileName) {
        if (!MurderMystery.getInstance().getDataFolder().exists())
            MurderMystery.getInstance().getDataFolder().mkdir();

        YamlConfiguration configuration = new YamlConfiguration();

        File file = getFile(fileName);
        if (!file.exists())
            file = copyDefault(fileName);

        return configuration.loadConfiguration(file);
    }

    public File getFile(String path) {
        return new File(DATA_FOLDER + path);
    }

    private File copyDefault(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + fileName)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FOLDER + fileName));

            String line;
            while ((line=reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader.close();
            writer.close();
            return getFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerLobbySpawnPoint() {
        World world = Bukkit.getWorld("world");
        double x = locations.getDouble("locations.lobby_spawn_point.x");
        double y = locations.getDouble("locations.lobby_spawn_point.y");
        double z = locations.getDouble("locations.lobby_spawn_point.z");
        float yaw = (float)locations.getDouble("locations.lobby_spawn_point.yaw");
        float pitch = (float)locations.getDouble("locations.lobby_spawn_point.pitch");
        Location location = new Location(world, x, y, z, yaw, pitch);
        
        MurderMystery.getGame().setLobbySpawnPoint(location);
    }

    private void registerGameSpawnPoints() {
        for (String section : locations.getConfigurationSection("locations.game_spawn_points").getKeys(false)) {
            World world = Bukkit.getWorld("world");
            double x = locations.getDouble("locations.game_spawn_points." + section + ".x");
            double y = locations.getDouble("locations.game_spawn_points." + section + ".y");
            double z = locations.getDouble("locations.game_spawn_points." + section + ".z");
            Location location = new Location(world, x, y, z);

            MurderMystery.getGame().addGameSpawnPoint(location);
        }
    }

    private void registerGoldSpawnPoints() {
        for (String section : locations.getConfigurationSection("locations.gold_spawn_points").getKeys(false)) {
            World world = Bukkit.getWorld("world");
            double x = locations.getDouble("locations.gold_spawn_points." + section + ".x");
            double y = locations.getDouble("locations.gold_spawn_points." + section + ".y");
            double z = locations.getDouble("locations.gold_spawn_points." + section + ".z");
            Location location = new Location(world, x, y, z);

            MurderMystery.getGame().addGoldSpawnPoint(location);
        }
    }
}
