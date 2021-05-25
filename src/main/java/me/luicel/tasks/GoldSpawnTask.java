package me.luicel.tasks;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GoldSpawnTask extends BukkitRunnable {
    private final Game game;
    private int delayTime;

    private final int spawnInterval;

    public GoldSpawnTask(int spawnInterval) {
        this.game = MurderMystery.getGame();
        this.delayTime = spawnInterval;
        this.spawnInterval = spawnInterval;
    }

    @Override
    public void run() {
        if (game.getState() == Game.State.PLAYING) {
            if (delayTime <= 0) {
                int randomIndex = (int)(Math.random() * game.getGoldSpawnPoints().size());

                World world = Bukkit.getWorld("world"); // I don't like how this is hardcoded...
                Location location = game.getGoldSpawnPoints().get(randomIndex);
                ItemStack goldItem = new ItemStack(Material.GOLD_INGOT, 1);
                world.dropItemNaturally(location, goldItem);

                delayTime = spawnInterval;
            } else {
                delayTime--;
            }
        } else {
            cancel();
        }
    }
}
