package net.endercube.Endercube.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static net.endercube.Parkour.ParkourMinigame.database;
import static net.endercube.Parkour.ParkourMinigame.parkourMinigame;

public class ResetTimeCommand extends Command {

    /**
     * Resets a player's time
     */
    public ResetTimeCommand() {
        super("resetParkourTime");
        var mapArgument = ArgumentType.Word("mapArgument").from(getParkourMapsAndAll());
        var playerArgument = ArgumentType.String("player");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Select a player & map");
        }));

        // Something wrong
        mapArgument.setCallback(((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage(Component.text("[ERROR] The map " + input + " does not exist!").color(NamedTextColor.RED));
        }));

        // Actually execute command
        addSyntax(((sender, context) -> {
            final String player = context.get(playerArgument);
            final String map = context.get(mapArgument);

            // Thanks stackoverflow: https://stackoverflow.com/a/19399768/13247146
            final UUID playerUUID = UUID.fromString(MojangUtils.fromUsername(player).get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            ));

            if (Objects.equals(map, "ALL")) {
                sender.sendMessage("Removing all times from " + player);
                for (String currentMap : getParkourMaps()) {
                    database.removeTime(playerUUID, currentMap);
                }
                return;
            }

            sender.sendMessage("Removing times for " + player + " in " + map);
            database.removeTime(playerUUID, map);
        }), mapArgument, playerArgument);


    }

    private String[] getParkourMaps() {
        ArrayList<String> mapNames = new ArrayList<>();

        for (InstanceContainer instance : parkourMinigame.getInstances()) {
            mapNames.add(instance.getTag(Tag.String("name")));
        }

        return mapNames.toArray(new String[0]);
    }

    private String[] getParkourMapsAndAll() {
        ArrayList<String> maps = new ArrayList<>(Arrays.asList(getParkourMaps()));
        maps.add("ALL");
        return maps.toArray(new String[0]);
    }
}
