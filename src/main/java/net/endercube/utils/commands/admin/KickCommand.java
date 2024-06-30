package net.endercube.utils.commands.admin;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class KickCommand extends Command {
    public KickCommand() {
        super("kick");
        var playerArgument = ArgumentType.Entity("player-name").onlyPlayers(true).singleEntity(true);

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Temporarily removes a player from the server");

        }));

        addSyntax(((sender, context) -> {
            final EntityFinder playerFinder = context.get(playerArgument);
            final Player player = playerFinder.findFirstPlayer(sender);

            if (player == null) {
                sender.sendMessage("The player does not exist");
                return;
            }

            player.kick("You have been kicked");
            sender.sendMessage("Kicked " + player.getUsername());
        }), playerArgument);


    }
}
