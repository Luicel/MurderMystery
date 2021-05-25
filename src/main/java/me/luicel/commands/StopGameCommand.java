package me.luicel.commands;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import me.luicel.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopGameCommand extends CustomCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase(commandName)) {
            if (commandSender instanceof Player) {
                GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom((Player)commandSender);
                executeStopGameCommand(gamePlayer);
            }
        }
        return true;
    }

    private void executeStopGameCommand(GamePlayer gamePlayer) {
        Game game = MurderMystery.getGame();
        if (game.getState() != Game.State.WAITING) {
            gamePlayer.sendMessage("&aYou have stopped the game!");
            game.sendActionBar("&cThe game has been stopped.");
            game.endGame();
        }
    }
}
