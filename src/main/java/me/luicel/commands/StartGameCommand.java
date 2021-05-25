package me.luicel.commands;

import me.luicel.MurderMystery;
import me.luicel.models.Game;
import me.luicel.models.GamePlayer;
import me.luicel.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartGameCommand extends CustomCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase(commandName))
            if (commandSender instanceof Player) {
                GamePlayer gamePlayer = GamePlayer.getGamePlayerFrom((Player)commandSender);
                executeStartGameCommand(gamePlayer);
            }
        return true;
    }

    private void executeStartGameCommand(GamePlayer gamePlayer) {
        Game game = MurderMystery.getGame();
        if (game.tryToStartCountdown())
            gamePlayer.sendMessage("&aYou have started the game!");
        else
            if (game.getState() != Game.State.WAITING) // not in WAITING state
                gamePlayer.sendMessage("&cThe game could not be started at this time!");
            else if (game.getPlayers().size() < game.getMinPlayers()) // under player limit
                gamePlayer.sendMessage("&cThere is not enough players to start a game!");
    }
}
