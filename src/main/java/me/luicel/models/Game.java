package me.luicel.models;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.luicel.MurderMystery;
import me.luicel.tasks.GameCountdownTask;
import me.luicel.tasks.GoldSpawnTask;
import me.luicel.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.management.relation.Role;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    private final int minPlayers;
    private final int maxPlayers;

    private GamePlayer murderer;
    private GamePlayer detective;

    private List<GamePlayer> players = new ArrayList<GamePlayer>();
    private List<GamePlayer> spectators = new ArrayList<GamePlayer>();

    private Location lobbySpawnPoint;
    private List<Location> gameSpawnPoints = new ArrayList<Location>();
    private List<Location> goldSpawnPoints = new ArrayList<Location>();

    private State state;
    public enum State { WAITING, COUNTDOWN, PLAYING, ENDING }

    public Game() {
        this.minPlayers = 2;
        this.maxPlayers = 8;
        this.state = State.WAITING;
    }

    public boolean tryToStartCountdown() {
        if (getPlayers().size() >= minPlayers && getState() == State.WAITING) {
            startCountdown();
            return true;
        } else {
            return false;
        }
    }

    public void startCountdown() {
        new GameCountdownTask().runTaskTimer(MurderMystery.getInstance(), 0, 20);
        setState(State.COUNTDOWN);
    }

    public void startGame() {
        sendMessage("The game has started...");
        setState(State.PLAYING);
        teleportPlayersToGame();
        assignRolesToPlayers();
        broadcastRolesToPlayers();
        giveItemsToPlayers();
        new GoldSpawnTask(5).runTaskTimer(MurderMystery.getInstance(), 0, 20);
    }

    public void teleportPlayersToGame() {
        List<Location> tempGameSpawnPoints = new ArrayList<Location>(gameSpawnPoints);
        for (GamePlayer gamePlayer : getPlayers()) {
            int randomIndex = (int)(Math.random() * tempGameSpawnPoints.size());
            gamePlayer.getPlayer().teleport(tempGameSpawnPoints.get(randomIndex));
            tempGameSpawnPoints.remove(randomIndex);
        }
    }

    public void assignRolesToPlayers() {
        List<GamePlayer> tempPlayers = new ArrayList<GamePlayer>(players);
        for (GamePlayer gamePlayer : getPlayers()) {
            int randomIndex = (int)(Math.random() * tempPlayers.size());
            if (randomIndex == 0 && murderer == null) {
                gamePlayer.setRole(GamePlayer.Role.MURDERER);
                murderer = gamePlayer;
            } else if (randomIndex == 0 && detective == null) {
                gamePlayer.setRole(GamePlayer.Role.DETECTIVE);
                detective = gamePlayer;
            } else if (randomIndex == 1 && detective == null) {
                gamePlayer.setRole(GamePlayer.Role.DETECTIVE);
                detective = gamePlayer;
            } else {
                gamePlayer.setRole(GamePlayer.Role.INNOCENT);
            }
            tempPlayers.remove(randomIndex);
        }
    }

    public void broadcastRolesToPlayers() {
        for (GamePlayer gamePlayer : getPlayers()) {
            switch (gamePlayer.getRole()) {
                case INNOCENT:
                    gamePlayer.sendTitle("&aINNOCENT", "&fSurvive from the murderer!");
                    break;
                case DETECTIVE:
                    gamePlayer.sendTitle("&bDETECTIVE", "&fKill the murderer!");
                    break;
                case MURDERER:
                    gamePlayer.sendTitle("&cMURDERER", "&fKill all other players!");
            }
        }
    }

    public void giveItemsToPlayers() {
        for (GamePlayer gamePlayer : getPlayers()) {
            switch (gamePlayer.getRole()) {
                case INNOCENT:
                    break;
                case DETECTIVE:
                    gamePlayer.getPlayer().getInventory().setItem(0, new ItemStack(Material.BOW));
                    gamePlayer.getPlayer().getInventory().setItem(1, new ItemStack(Material.ARROW));
                    break;
                case MURDERER:
                    gamePlayer.getPlayer().getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
                    break;
            }
            gamePlayer.getPlayer().getInventory().setHeldItemSlot(4);
            gamePlayer.getPlayer().updateInventory();
        }
    }

    public void dropDetectiveBowAt(GamePlayer player) {
        Location location = player.getPlayer().getLocation();
        ItemStack bowItem = new ItemStack(Material.BOW, 1);
        player.getPlayer().getWorld().dropItemNaturally(location, bowItem);
        MurderMystery.getGame().sendActionBar("&cThe detective's bow has been dropped!");
    }

    public void tryToEndGame() {
        if (getState() == State.PLAYING) {
            if (!murderer.isAlive()) {
                for (GamePlayer gamePlayer : getPlayers()) {
                    if (gamePlayer.getRole() == GamePlayer.Role.MURDERER)
                        gamePlayer.sendTitle("&cYOU LOSE!", "&fYou have been killed!");
                    else
                        gamePlayer.sendTitle("&aYOU WIN!", "&fThe murderer has been killed!");
                }
                endGame();
            } else if (getAlivePlayersExcludingMurderer() == 0) {
                for (GamePlayer gamePlayer : getPlayers()) {
                    if (gamePlayer.getRole() == GamePlayer.Role.MURDERER)
                        gamePlayer.sendTitle("&aYOU WIN!", "&fAll innocents have died!");
                    else
                        gamePlayer.sendTitle("&cYOU LOSE!", "&fAll innocents have died!");
                }
                endGame();
            }
        }
    }

    public void endGame() {
        setState(State.ENDING);
        murderer = null;
        detective = null;
        clearAllItemsFromWorld();
        new BukkitRunnable() {
            @Override
            public void run() {
                handlePlayersAfterGameEnd();
            }
        }.runTaskLater(MurderMystery.getInstance(), 100);
    }

    public void clearAllItemsFromWorld() {
        World world = Bukkit.getWorld("world");
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Item)
                if (((Item) entity).getItemStack().getType() == Material.GOLD_INGOT)
                    entity.remove();
                else if (((Item) entity).getItemStack().getType() == Material.BOW)
                    entity.remove();
        }
    }

    public void handlePlayersAfterGameEnd() {
        for (GamePlayer gamePlayer : getPlayers()) {
            if (spectators.contains(gamePlayer))
                spectators.remove(gamePlayer);
            gamePlayer.teleportToHub();
            gamePlayer.configureDefaultPlayerData();
            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().updateInventory();
        }
        setState(State.WAITING);
    }

    public int getAlivePlayersExcludingMurderer() {
        int i = getPlayers().size();
        for (GamePlayer gamePlayer : getPlayers()) {
            if (getSpectators().contains(gamePlayer))
                i--;
            else if (gamePlayer.getRole() == GamePlayer.Role.MURDERER)
                i--;
        }
        return i;
    }

    public void addPlayer(GamePlayer gamePlayer) {
        players.add(gamePlayer);
    }

    public void removePlayer(GamePlayer gamePlayer) {
        players.remove(gamePlayer);
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public Location getLobbySpawnPoint() {
        return lobbySpawnPoint;
    }

    public void setLobbySpawnPoint(Location location) {
        lobbySpawnPoint = location;
    }

    public List<Location> getGameSpawnPoints() {
        return gameSpawnPoints;
    }

    public void addGameSpawnPoint(Location location) {
        gameSpawnPoints.add(location);
    }

    public List<Location> getGoldSpawnPoints() {
        return goldSpawnPoints;
    }

    public void addGoldSpawnPoint(Location location) {
        goldSpawnPoints.add(location);
    }

    public void sendMessage(String message) {
        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.getPlayer().sendMessage(ChatUtils.format(message));
        }
    }

    public void sendActionBar(String message) {
        for (GamePlayer gamePlayer : getPlayers()) {
            ActionBarAPI.sendActionBar(gamePlayer.getPlayer(), ChatUtils.format(message));
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.getPlayer().sendTitle(ChatUtils.format(title), ChatUtils.format(subtitle));
        }
    }
}
