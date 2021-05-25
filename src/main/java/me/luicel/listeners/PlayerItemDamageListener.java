package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class PlayerItemDamageListener implements Listener {
    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom(event.getPlayer());

        if (game.getState() == Game.State.PLAYING || game.getState() == Game.State.ENDING) {
            if (event.getItem().getType() == Material.IRON_SWORD)
                event.setCancelled(true);
            else if (event.getItem().getType() == Material.BOW)
                event.setCancelled(true);
        }
    }
}
