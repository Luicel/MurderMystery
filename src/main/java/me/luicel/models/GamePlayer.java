package me.luicel.models;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.luicel.MurderMystery;
import me.luicel.tasks.DetectiveBowCooldownTask;
import me.luicel.utils.ChatUtils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GamePlayer {
    private Player player;
    private Role role;
    public enum Role { INNOCENT, DETECTIVE, MURDERER }
    private int gold;

    public GamePlayer(Player player) {
        this.player = player;
        this.role = null;
        this.gold = 0;
    }

    public GamePlayer(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        try {
            return this.role;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public static GamePlayer getGamePlayerFrom(Player player) {
        for (GamePlayer gamePlayer : MurderMystery.getGame().getPlayers()) {
            if (gamePlayer.getPlayer() == player)
                return gamePlayer;
        }
        return null;
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatUtils.format(message));
    }

    public void sendActionBar(String message) {
        ActionBarAPI.sendActionBar(player, ChatUtils.format(message));
    }

    public void sendTitle(String title, String subtitle) {
        player.sendTitle(ChatUtils.format(title), ChatUtils.format(subtitle));
    }

    public void teleportToHub() {
        Location location = MurderMystery.getGame().getLobbySpawnPoint();
        player.teleport(location);
    }

    public void configureDefaultPlayerData() {
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.updateInventory();
        role = null;
        gold = 0;
    }

    public void pickupGold(int amount) {
        gold += amount;
        if (gold >= 10 && getRole() == Role.INNOCENT) {
            player.getInventory().setItem(0, new ItemStack(Material.BOW));
            if (player.getInventory().contains(Material.ARROW))
                player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            else
                player.getInventory().setItem(1, new ItemStack(Material.ARROW));
            gold -= 10;
            sendTitle("", "&a+1 arrow");
        }
        player.getInventory().setItem(8, new ItemStack(Material.GOLD_INGOT, gold));
        player.playSound(player.getLocation(), "entity.item.pickup", 1, 1);
        player.updateInventory();
    }

    public void pickupBow() {
        setRole(Role.DETECTIVE);
        if (!player.getInventory().contains(Material.BOW))
            player.getInventory().setItem(0, new ItemStack(Material.BOW));
        if (player.getInventory().contains(Material.ARROW))
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        else
            player.getInventory().setItem(1, new ItemStack(Material.ARROW));
        player.playSound(player.getLocation(), "entity.item.pickup", 1, 1);
        sendTitle("", "&aYou picked up the detective's bow!");
        MurderMystery.getGame().sendActionBar("&aA player has picked up the detective's bow!");
    }

    public void beginBowCooldown() {
        new DetectiveBowCooldownTask(this, 5).runTaskTimer(MurderMystery.getInstance(), 0, 2);
    }

    public void die(GamePlayer killer) {
        playDeathSound();
        player.setGameMode(GameMode.SPECTATOR);
        MurderMystery.getGame().getSpectators().add(this);
        switch (killer.getRole()) {
            case INNOCENT:
                sendTitle("&cYOU DIED", "&fYou were killed by an innocent, " + killer.getPlayer().getName() + "!");
                break;
            case DETECTIVE:
                sendTitle("&cYOU DIED", "&fYou were killed by the detective, " + killer.getPlayer().getName() + "!");
                break;
            case MURDERER:
                sendTitle("&cYOU DIED", "&fYou were killed by the murderer, " + killer.getPlayer().getName() + "!");
                break;
        }
        if (getRole() == Role.DETECTIVE && MurderMystery.getGame().getAlivePlayersExcludingMurderer() > 0)
            MurderMystery.getGame().dropDetectiveBowAt(this);
    }

    public void dieFromKillingInnocent(GamePlayer victim) {
        playDeathSound();
        player.setGameMode(GameMode.SPECTATOR);
        MurderMystery.getGame().getSpectators().add(this);
        sendTitle("&cYOU DIED", "&fYou killed an innocent player, " + victim.getPlayer().getName() + "!");
        if (getRole() == Role.DETECTIVE && MurderMystery.getGame().getAlivePlayersExcludingMurderer() > 0)
            MurderMystery.getGame().dropDetectiveBowAt(this);
    }

    public void playDeathSound() {
        for (GamePlayer gamePlayer : MurderMystery.getGame().getPlayers()) {
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), "entity.skeleton.hurt", 1, 1);
        }
    }

    public boolean isAlive() {
        if (MurderMystery.getGame().getPlayers().contains(this))
            return !MurderMystery.getGame().getSpectators().contains(this);
        return false;
    }
}
