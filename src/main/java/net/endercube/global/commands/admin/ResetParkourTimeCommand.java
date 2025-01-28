package net.endercube.global.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.mojang.MojangUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static net.endercube.parkour.ParkourMinigame.database;
import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

public class ResetParkourTimeCommand extends Command {

    /**
     * Resets a player's time
     */
    public ResetParkourTimeCommand() {
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
            final String playerUsername = context.get(playerArgument);
            final String map = context.get(mapArgument);

            // Get the player's UUID
            final UUID playerUUID;
            try {
                playerUUID = MojangUtils.getUUID(playerUsername);
            } catch (IOException e) {
                sender.sendMessage(Component.text("Error: " + e.getMessage()).color(NamedTextColor.RED));
                return;
            }

            if (Objects.equals(map, "ALL")) {
                sender.sendMessage("Removing all times from " + playerUsername);
                for (String currentMap : getParkourMaps()) {
                    database.removeTime(playerUUID, currentMap);
                }
                return;
            }

            sender.sendMessage("Removing times for " + playerUsername + " in " + map);
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
