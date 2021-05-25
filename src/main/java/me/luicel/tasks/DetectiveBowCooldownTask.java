package me.luicel.tasks;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectiveBowCooldownTask extends BukkitRunnable {
    private final Game game;
    private final GamePlayer gamePlayer;
    private final float startCooldownTime;

    private float cooldownTime;

    public DetectiveBowCooldownTask(GamePlayer gamePlayer, int startCooldownTime) {
        this.game = MurderMystery.getGame();
        this.gamePlayer = gamePlayer;
        this.startCooldownTime = startCooldownTime;
        this.cooldownTime = startCooldownTime;
    }

    @Override
    public void run() {
        if (game.getState() == Game.State.PLAYING) {
            if (cooldownTime > 0) {
                printCooldownActionbarTo(gamePlayer);
                cooldownTime -= 0.1;
            } else {
                gamePlayer.sendActionBar("&aYour bow has recharged!");
                gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), "entity.experience_orb.pickup", 1, 1);
                gamePlayer.getPlayer().getInventory().setItem(1, new ItemStack(Material.ARROW));
                cancel();
            }
        } else {
            cancel();
        }
    }

    private void printCooldownActionbarTo(GamePlayer gamePlayer) {
        float secondsPerBox = startCooldownTime / 10;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%.1f", cooldownTime)).append(" ");
        for (int i = 1; i <= 10; i++) {
            if (cooldownTime > (secondsPerBox * i))
                stringBuilder.append("&b&m-");
            else
                stringBuilder.append("&f&m-");
        }

        gamePlayer.sendActionBar(stringBuilder.toString());
    }
}
