package net.endercube.Endercube.commands.admin;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.UUID;

import static net.endercube.Endercube.Main.jedis;

public class UnbanCommand extends Command {
    public UnbanCommand() {
        super("unban", "pardon");
        var playerArgument = ArgumentType.String("player");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Makes a player not banned");
        }));

        addSyntax(((sender, context) -> {
            final String player = context.get(playerArgument);
            // Thanks stackoverflow: https://stackoverflow.com/a/19399768/13247146
            final UUID playerUUID = UUID.fromString(MojangUtils.fromUsername(player).get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            ));

            jedis.del("banned:" + playerUUID);
            sender.sendMessage("Unbanned " + player);
        }), playerArgument);
    }
}
