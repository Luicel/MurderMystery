package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import me.luicel.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        removePlayerFromGame(event.getPlayer());

        String quitMessage = ChatUtils.format("&c&lLEAVE! &7" + event.getPlayer().getName());
        event.setQuitMessage(quitMessage);

    }

    private void removePlayerFromGame(Player player) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom(player);

        game.removePlayer(gamePlayer);
        game.tryToEndGame();
    }
}
