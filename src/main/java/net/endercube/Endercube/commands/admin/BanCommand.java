package net.endercube.Endercube.commands.admin;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.UUID;

import static net.endercube.Endercube.Main.jedis;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban");
        var playerArgument = ArgumentType.String("player");
        var messageArgument = ArgumentType.String("message");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Bans a player permanently from the server");
        }));

        addSyntax(((sender, context) -> {
            final String player = context.get(playerArgument);

            ban(player, "You're banned!");
            sender.sendMessage("Banned " + player);
        }), playerArgument);

        addSyntax(((sender, context) -> {
            final String player = context.get(playerArgument);
            final String message = context.get(messageArgument);

            ban(player, message);
            sender.sendMessage("Banned " + player + " with message: " + message);
        }), playerArgument, messageArgument);
    }

    private UUID getUUID(String playerUsername) {
        // Thanks stackoverflow: https://stackoverflow.com/a/19399768/13247146
        return UUID.fromString(
                MojangUtils.fromUsername(playerUsername)
                        .get("id")
                        .getAsString()
                        .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                "$1-$2-$3-$4-$5"
                        )
        );
    }

    private void ban(String playerUsername, String message) {
        jedis.set("banned:" + getUUID(playerUsername), message);
        Player onlinePlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(playerUsername);
        if (onlinePlayer != null) {
            onlinePlayer.kick(message);
        }
    }
}
