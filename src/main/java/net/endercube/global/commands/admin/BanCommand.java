package net.endercube.global.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;

import java.io.IOException;
import java.util.UUID;

import static net.endercube.Main.jedis;
import static net.endercube.Main.logger;

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

            sender.sendMessage(ban(player, "You're banned!"));
            sender.sendMessage("Banned " + player);
        }), playerArgument);

        addSyntax(((sender, context) -> {
            final String player = context.get(playerArgument);
            final String message = context.get(messageArgument);

            sender.sendMessage(ban(player, message));
            sender.sendMessage("Banned " + player + " with message: " + message);
        }), playerArgument, messageArgument);
    }

    /**
     * Bans the player
     *
     * @param playerUsername The username of the player to ban
     * @param message        The ban message
     * @return An error if there is one in a format to be sent to the player
     */
    private Component ban(String playerUsername, String message) {

        UUID playerUUID;
        try {
            playerUUID = MojangUtils.getUUID(playerUsername);
        } catch (IOException e) {
            logger.error("Could not ban " + playerUsername + " with the exception: " + e.getMessage());
            return Component.text("Error: Could not ban " + playerUsername + " with the exception: " + e.getMessage()).color(NamedTextColor.RED);
        }

        jedis.set("banned:" + playerUUID, message);
        Player onlinePlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(playerUsername);
        if (onlinePlayer != null) {
            onlinePlayer.kick(message);
        }

        return Component.empty();
    }
}
