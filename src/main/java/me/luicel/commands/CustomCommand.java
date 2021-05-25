package me.luicel.commands;

import me.luicel.models.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class CustomCommand implements CommandExecutor {
    public String commandName;

    public CustomCommand() {
        commandName = this.getClass().getSimpleName().replace("Command", "").toLowerCase();
    }
}
