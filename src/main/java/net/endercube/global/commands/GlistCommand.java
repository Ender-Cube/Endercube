package net.endercube.global.commands;

import net.endercube.gamelib.utils.ComponentUtils;
import net.endercube.global.EndercubePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

// TODO: Test/make work/do anything/please don't commit me
public class GlistCommand extends Command {
    public GlistCommand() {
        super("glist", "list", "online");

        setDefaultExecutor((sender, context) -> {
            MultiValuedMap<String, String> playersPerMinigame = new ArrayListValuedHashMap<>();

            MinecraftServer.getConnectionManager().getOnlinePlayers()
                    .forEach(player -> {
                        EndercubePlayer endercubePlayer = (EndercubePlayer) player;
                        playersPerMinigame.put(endercubePlayer.getCurrentMinigame(), endercubePlayer.getUsername());
                    });

            playersPerMinigame.asMap()
                    .forEach((minigame, playerNames) -> {
                        sender.sendMessage(
                                ComponentUtils.getTitle(Component.text("Players"))
                                        .append(Component.text(StringUtils.capitalize(minigame))
                                                .color(NamedTextColor.AQUA)
                                        )
                                        .append(Component.text(": ")
                                                .color(NamedTextColor.AQUA)
                                                .decorate(TextDecoration.BOLD)
                                        )
                                        .append(Component.text(prettyCollection(playerNames)))
                        );
                    });

        });
    }

    private String prettyCollection(Collection<String> collectionIn) {
        StringBuilder finalString = new StringBuilder();
        collectionIn.forEach((s -> {
            finalString.append(s);
            finalString.append(" ");
        }));

        return finalString.toString();
    }
}
