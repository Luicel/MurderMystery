package me.luicel.listeners;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (MurderMystery.getGame().getState() != Game.State.PLAYING)
            event.setCancelled(true);
        else if (event.getDamager() instanceof Player && event.getEntity() instanceof Player)
            handlePlayerDamaging((Player)event.getDamager(), (Player)event.getEntity(), event);
        else if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player)
            handleBowDamaging((Projectile)event.getDamager(), (Player)event.getEntity(), event);
    }

    public void handlePlayerDamaging(Player damager, Player entity, EntityDamageByEntityEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer damagerGamePlayer = GamePlayer.getGamePlayerFrom(damager);
        GamePlayer entityGamePlayer = GamePlayer.getGamePlayerFrom(entity);
        if (damagerGamePlayer.getRole() == GamePlayer.Role.MURDERER && damagerGamePlayer.getPlayer().getItemInHand().getType() == Material.IRON_SWORD) {
            entityGamePlayer.die(damagerGamePlayer);
            game.tryToEndGame();
        } else {
            event.setCancelled(true);
        }
    }

    private void handleBowDamaging(Projectile damager, Player entity, EntityDamageByEntityEvent event) {
        Game game = MurderMystery.getGame();
        GamePlayer damagerGamePlayer = GamePlayer.getGamePlayerFrom((Player)damager.getShooter());
        GamePlayer entityGamePlayer = GamePlayer.getGamePlayerFrom(entity);
        if (damagerGamePlayer.getRole() == GamePlayer.Role.INNOCENT || damagerGamePlayer.getRole() == GamePlayer.Role.DETECTIVE) {
            entityGamePlayer.die(damagerGamePlayer);
            if (entityGamePlayer.getRole() != GamePlayer.Role.MURDERER)
                damagerGamePlayer.dieFromKillingInnocent(entityGamePlayer);
            game.tryToEndGame();
        } else {
            event.setCancelled(true);
        }
    }
}
