package net.endercube.Parkour.commands;

import net.minestom.server.command.builder.Command;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        super("leaderboard");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("You executed the leaderboard command");
        }));
    }
}
