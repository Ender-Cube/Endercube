package net.endercube.Parkour.commands;

import net.endercube.Common.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

import static net.endercube.Common.EndercubeMinigame.logger;
import static net.endercube.Parkour.ParkourMinigame.database;
import static net.endercube.Parkour.ParkourMinigame.parkourMinigame;

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

    private TextComponent createLeaderboard(String mapName) {

        // Get list of times from db
        List<Tuple> leaderboardTuple = database.getLeaderboard(mapName, 9);

        logger.info("Leaderboard: " + leaderboardTuple.get(0).getElement() + " score: " + leaderboardTuple.get(0).getScore());
        logger.info("Size: " + leaderboardTuple.size());

        // Add places 1-3
        Component placementComponent = Component.text("");

        if (leaderboardTuple.size() >= 1) {
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
