package net.endercube.spleef.minigame.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.MathUtils;

import static net.endercube.Common.utils.ComponentUtils.getTitle;
import static net.endercube.spleef.minigame.SpleefMinigame.database;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats");

        setDefaultExecutor(((commandSender, commandContext) -> {
            Player player = (Player) commandSender;
            commandSender.sendMessage(getTitle(Component.text("Spleef Stats"))
                    .append(
                            Component.text("Wins: ")
                    )
                    .append(
                            Component.text(database.getWonGames(player)).color(NamedTextColor.GRAY)
                    )
                    .append(Component.newline())
                    .append(
                            Component.text("Losses: ")
                    )
                    .append(
                            Component.text(database.getLostGames(player)).color(NamedTextColor.GRAY)
                    )
                    .append(Component.newline())
                    .append(
                            Component.text("Games: ")
                    )
                    .append(
                            Component.text(database.getAllGames(player)).color(NamedTextColor.GRAY)
                    )
                    .append(Component.newline())
                    .append(
                            Component.text("Win%: ")
                    )
                    .append(
                            Component.text(getWinPercent(player) + "%").color(NamedTextColor.GRAY)
                    )
            );
        }));
    }

    private float getWinPercent(Player player) {
        // Stop divide by 0 errors
        if (database.getAllGames(player) == 0) {
            return 0;
        }
        return MathUtils.round(((float) database.getWonGames(player) / (float) database.getAllGames(player)) * 100, 1);
    }
}
