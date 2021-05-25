package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import me.luicel.utils.ChatUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isGameFull())
            event.getPlayer().kickPlayer(ChatUtils.format("&cSorry! This game is currently full!"));

        addPlayerToGame(event.getPlayer());

        String joinMessage = ChatUtils.format("&a&lJOIN! &7" + event.getPlayer().getName());
        event.setJoinMessage(joinMessage);
    }

    private boolean isGameFull() {
        return MurderMystery.getGame().getPlayers().size() >= MurderMystery.getGame().getMaxPlayers();
    }

    private void addPlayerToGame(Player player) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = new GamePlayer(player);

        MurderMystery.getGame().addPlayer(gamePlayer);
        gamePlayer.teleportToHub();
        gamePlayer.configureDefaultPlayerData();
        if (game.getState() == Game.State.PLAYING || game.getState() == Game.State.ENDING) {
            game.getSpectators().add(gamePlayer);
            gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }
}
