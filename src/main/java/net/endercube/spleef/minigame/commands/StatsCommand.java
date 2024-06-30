package net.endercube.spleef.minigame.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.mojang.MojangUtils;

import java.io.IOException;
import java.util.UUID;

import static net.endercube.common.utils.ComponentUtils.getTitle;
import static net.endercube.spleef.minigame.SpleefMinigame.database;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats");

        var playerArgument = ArgumentType.Word("player-name");

        setDefaultExecutor((commandSender, commandContext) -> {
            Player player = (Player) commandSender;
            commandSender.sendMessage(getStats(player));
        });

        addSyntax((sender, context) -> {
            sender.sendMessage(getStats(context.get(playerArgument)));
        }, playerArgument);
    }

    private float getWinPercent(UUID playerUUID) {
        // Stop divide by 0 errors
        if (database.getAllGames(playerUUID) == 0) {
            return 0;
        }
        return MathUtils.round(((float) database.getWonGames(playerUUID) / (float) database.getAllGames(playerUUID)) * 100, 1);
    }

    private Component getStats(Player player) {
        return getStats(player.getUuid());
    }

    private Component getStats(String playerName) {
        try {
            return getStats(MojangUtils.getUUID(playerName));
        } catch (IOException e) {
            return Component.text("Error: " + e.getMessage()).color(NamedTextColor.RED);
        }
    }

    private Component getStats(UUID playerUUID) {

        // Tell the user if the player does not exist on the database
        if (!database.hasPlayed(playerUUID)) {
            return Component.text("Error: That player has not played spleef").color(NamedTextColor.RED);
        }

        return getTitle(Component.text("Spleef Stats"))
                .append(
                        Component.text("Wins: ")
                )
                .append(
                        Component.text(database.getWonGames(playerUUID)).color(NamedTextColor.GRAY)
                )
                .append(Component.newline())
                .append(
                        Component.text("Losses: ")
                )
                .append(
                        Component.text(database.getLostGames(playerUUID)).color(NamedTextColor.GRAY)
                )
                .append(Component.newline())
                .append(
                        Component.text("Games: ")
                )
                .append(
                        Component.text(database.getAllGames(playerUUID)).color(NamedTextColor.GRAY)
                )
                .append(Component.newline())
                .append(
                        Component.text("Win%: ")
                )
                .append(
                        Component.text(getWinPercent(playerUUID) + "%").color(NamedTextColor.GRAY)
                );
    }


}
