package net.endercube.spleef.minigame.commands;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.spleef.minigame.inventories.MapVoteInventory;
import net.minestom.server.command.builder.Command;

public class VoteCommand extends Command {
    public VoteCommand() {
        super("vote");

        setDefaultExecutor(((sender, context) -> {
            EndercubePlayer player = (EndercubePlayer) sender;

            player.sendMessage("Opening inv");
            player.openInventory(MapVoteInventory.getInventory());
        }));
    }
}
