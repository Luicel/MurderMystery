package me.luicel.tasks;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.utils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdownTask extends BukkitRunnable {
    private int countdownTime;
    private Game game;

    public GameCountdownTask() {
        this.countdownTime = 20;
        this.game = MurderMystery.getGame();
    }

    @Override
    public void run() {
        if (game.getState() == Game.State.COUNTDOWN) {
            if (countdownTime > 0) {
                game.sendActionBar("&aStarting in " + countdownTime + " second" + (countdownTime == 1 ? "" : "s") + ".");
                countdownTime--;
            } else {
                game.sendActionBar("&aStarting now...");
                game.startGame();
                cancel();
            }
        } else {
            cancel();
        }
    }
}
