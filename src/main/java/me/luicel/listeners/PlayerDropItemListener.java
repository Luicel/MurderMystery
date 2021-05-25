package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom(event.getPlayer());

        if (game.getState() == Game.State.PLAYING || game.getState() == Game.State.ENDING)
            if (event.getItemDrop().getItemStack().getType() == Material.IRON_SWORD)
                event.setCancelled(true);
            else if (event.getItemDrop().getItemStack().getType() == Material.BOW)
                event.setCancelled(true);
            else if (event.getItemDrop().getItemStack().getType() == Material.ARROW)
                event.setCancelled(true);
            else if (event.getItemDrop().getItemStack().getType() == Material.GOLD_INGOT)
                event.setCancelled(true);
    }
}
