package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreakByEntityListener implements Listener {
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom((Player)event.getRemover());
        if (gamePlayer.getPlayer().getGameMode() != GameMode.CREATIVE)
            if (event.getEntity() instanceof ItemFrame)
                event.setCancelled(true);
            else if (event.getEntity() instanceof Painting)
                event.setCancelled(true);
    }
}
