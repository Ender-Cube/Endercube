package net.endercube.global.commands;

import net.endercube.global.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.builder.Command;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

// TODO: Test/make work/do anything/please don't commit me
public class GlistCommand extends Command {
    public GlistCommand() {
        super("glist", "list", "online");

        setDefaultExecutor(((sender, context) -> {
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach((player -> {
                EndercubePlayer player1 = (EndercubePlayer) player;
                sender.sendMessage(player1.getUsername() + ": " + player1.getCurrentMinigame() + " - " + player1.getInstance().getTag(Tag.String("name")));
            }));
        }));
    }
}
