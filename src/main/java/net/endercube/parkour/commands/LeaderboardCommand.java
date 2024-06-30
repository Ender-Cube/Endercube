package net.endercube.parkour.commands;

import net.endercube.common.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

import static net.endercube.common.EndercubeMinigame.logger;
import static net.endercube.parkour.ParkourMinigame.database;
import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        super("leaderboard");
        var mapArgument = ArgumentType.Word("mapArgument").from(getMaps());

        // No map defined
        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(Component.text("[ERROR] You must specify a map").color(NamedTextColor.RED));
        }));

        // Non-existent map defined
        mapArgument.setCallback(((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage(Component.text("[ERROR] The map " + input + " does not exist!").color(NamedTextColor.RED));
        }));

        // Actually execute command
        addSyntax(((sender, context) -> {
            final String map = context.get(mapArgument);
            sender.sendMessage(createLeaderboard(map));
        }), mapArgument);
    }

    /**
     * Generates a leaderboard
     *
     * @param mapName The map
     * @return A leaderboard or formatted text for an error if there is one
     */
    private TextComponent createLeaderboard(String mapName) {
        Component placementComponent = Component.text("");

        // Get list of times from db
        List<Tuple> leaderboardTuple = database.getLeaderboard(mapName, 9);

        // Tell the player and the log that something went wrong if the database returns null
        if (leaderboardTuple == null) {
            logger.warn("The database call for leaderboards in parkour was null for some reason, continuing but something has gone wrong");
            return Component.text("")
                    .append(Component.text("[ERROR] ")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                    )
                    .append(Component.text("Something went wrong when reading the database. Please contact admins on the Endercube Discord")
                            .color(NamedTextColor.RED)
                    );
        }

        // Tell the player what happened if there are no times
        if (leaderboardTuple.isEmpty()) {
            return Component.text("")
                    .append(Component.text("No times exist for ").color(NamedTextColor.AQUA))
                    .append(Component.text(mapName).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" yet! Why not set some?").color(NamedTextColor.AQUA));
        }

        // Add places 1-3
        if (leaderboardTuple.size() >= 1) { // This is always true. Leaving it in to make this easier to read
            placementComponent = placementComponent.append(leaderboardEntry("#FFD700",
                    leaderboardTuple.get(0).getElement(),
                    leaderboardTuple.get(0).getScore(),
                    1)
            );
        }

        if (leaderboardTuple.size() >= 2) {
            placementComponent = placementComponent.append(leaderboardEntry("#808080",
                    leaderboardTuple.get(1).getElement(),
                    leaderboardTuple.get(1).getScore(),
                    2)
            );
        }

        if (leaderboardTuple.size() >= 3) {
            placementComponent = placementComponent.append(leaderboardEntry("#CD7F32",
                    leaderboardTuple.get(2).getElement(),
                    leaderboardTuple.get(2).getScore(),
                    3)
            ).append(Component.newline());
        }


        // Add places 4-10
        if (leaderboardTuple.size() >= 4) {
            for (int i = 3; i < leaderboardTuple.size(); i++) {
                placementComponent = placementComponent.append(leaderboardEntry("#AAAAAA",
                        leaderboardTuple.get(i).getElement(),
                        leaderboardTuple.get(i).getScore(),
                        i + 1)
                );
            }
        }


        return Component.text()
                .append(ComponentUtils.centerComponent(MiniMessage.miniMessage().deserialize("<bold><gradient:#FF416C:#FF4B2B>All Time Leaderboard For " + mapName)))
                .append(Component.newline())
                .append(Component.newline())
                .append(placementComponent)
                .build();
    }

    private Component leaderboardEntry(String color, String player, double time, int placement) {
        String placementToNameGap;
        if (placement >= 10) {
            placementToNameGap = " ";
        } else {
            placementToNameGap = "  ";
        }
        return MiniMessage.miniMessage()
                .deserialize("<" + color + ">#<bold>" + placement + placementToNameGap + player + "</bold> " + ComponentUtils.toHumanReadableTime((long) time))
                .append(Component.newline());
    }

    private String[] getMaps() {
        ArrayList<String> mapNames = new ArrayList<>();

        for (InstanceContainer instance : parkourMinigame.getInstances()) {
            mapNames.add(instance.getTag(Tag.String("name")));
        }

        return mapNames.toArray(new String[0]);
    }
}
