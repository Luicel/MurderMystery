package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom(event.getPlayer());

        if (event.getItem().getItemStack().getType() == Material.GOLD_INGOT) {
            event.getItem().remove();
            event.setCancelled(true);
            gamePlayer.pickupGold(event.getItem().getItemStack().getAmount());
        } else if (event.getItem().getItemStack().getType() == Material.BOW) {
            if (gamePlayer.getRole() != GamePlayer.Role.MURDERER) {
                event.getItem().remove();
                gamePlayer.pickupBow();
            }
            event.setCancelled(true);
        } else if (event.getItem().getItemStack().getType() == Material.ARROW) {
            event.setCancelled(true);
        }
    }
}
