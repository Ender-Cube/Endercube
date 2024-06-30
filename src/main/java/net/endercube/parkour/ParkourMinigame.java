package net.endercube.parkour;

import net.endercube.common.EndercubeMinigame;
import net.endercube.common.EndercubeServer;
import net.endercube.common.dimensions.FullbrightDimension;
import net.endercube.common.players.EndercubePlayer;
import net.endercube.parkour.commands.LeaderboardCommand;
import net.endercube.parkour.database.ParkourDatabase;
import net.endercube.parkour.listeners.*;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.tag.Tag;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the entrypoint for Parkour
 */
public class ParkourMinigame extends EndercubeMinigame {

    public static ParkourMinigame parkourMinigame;
    public static ParkourDatabase database;
    public static Team parkourTeam;

    static {
        parkourTeam = MinecraftServer.getTeamManager().createBuilder("parkourTeam")
                .collisionRule(TeamsPacket.CollisionRule.NEVER)
                .build();
    }

    public ParkourMinigame(EndercubeServer endercubeServer) {
        super(endercubeServer);
        parkourMinigame = this;

        // Register events
        eventNode
                .addListener(new PlayerMove())
                .addListener(new PlayerUseItem())
                .addListener(new MinigamePlayerJoin())
                .addListener(new MinigamePlayerLeave())
                .addListener(new InventoryPreClick())
                .addListener(PlayerSwapItemEvent.class, event -> event.setCancelled(true));

        try {
            database = this.createDatabase(ParkourDatabase.class);
        } catch (Exception e) {
            logger.error("Failed to create a ParkourDatabase");
            throw new RuntimeException(e);
        }

        this.registerCommands();


    }

    @Override
    public String getName() {
        return "parkour";
    }

    @Override
    protected ArrayList<InstanceContainer> initInstances() {
        File[] worldFiles = Paths.get("./config/worlds/parkour").toFile().listFiles();
        ArrayList<InstanceContainer> instances = new ArrayList<>();
        if (worldFiles == null) {
            logger.error("No parkour maps are found, please place some Polar worlds in ./config/worlds/parkour/");
            MinecraftServer.stopCleanly();
            return null;
        }
        try {
            for (File worldFile : worldFiles) {
                // Get the name of the map by removing .polar from the file name
                String mapName = worldFile.getName();
                mapName = mapName.substring(0, mapName.length() - 6);

                ConfigurationNode configNode = config.getConfig().node("maps", mapName);


                InstanceContainer currentInstance = MinecraftServer.getInstanceManager().createInstanceContainer(
                        FullbrightDimension.INSTANCE,
                        new PolarLoader(Path.of(worldFile.getPath()))
                );

                currentInstance.setTimeRate(0);

                // Set all tags from config
                currentInstance.setTag(Tag.Transient("checkpointsPosArray"), config.getPosListFromConfig(configNode.node("checkpoints")));
                currentInstance.setTag(Tag.Integer("death-y"), configNode.node("death-y").getInt());
                currentInstance.setTag(Tag.String("difficulty"), configNode.node("difficulty").getString());
                currentInstance.setTag(Tag.Transient("finishPos"), config.getPosFromConfig(configNode.node("finish")));
                currentInstance.setTag(Tag.Integer("order"), configNode.node("order").getInt());
                currentInstance.setTag(Tag.Transient("spawnPos"), config.getPosFromConfig(configNode.node("spawn")));
                currentInstance.setTag(Tag.String("name"), mapName);
                currentInstance.setTag(Tag.String("UI_material"), configNode.node("UIMaterial", "material").getString());
                currentInstance.setTag(Tag.String("UI_name"), configNode.node("UIMaterial", "name").getString());
                instances.add(currentInstance);

                logger.info("Added the Parkour map: " + mapName);
                logger.debug(mapName + "'s details:");
                logger.debug("checkpointsPosArray: " + currentInstance.getTag(Tag.Transient("checkpointsPosArray")));
                logger.debug("death-y: " + currentInstance.getTag(Tag.Integer("death-y")));
                logger.debug("difficulty: " + currentInstance.getTag(Tag.String("difficulty")));
                logger.debug("finishPos: " + currentInstance.getTag(Tag.Transient("finishPos")));
                logger.debug("order: " + currentInstance.getTag(Tag.Integer("order")));
                logger.debug("spawnPos: " + currentInstance.getTag(Tag.Transient("spawnPos")));
                logger.debug("name: " + currentInstance.getTag(Tag.String("name")));
                logger.debug("UI_material: " + currentInstance.getTag(Tag.String("UI_material")));
                logger.debug("UI_name: " + currentInstance.getTag(Tag.String("UI_name")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return instances;
    }

    /**
     * Adds a leaderboard command
     *
     * @param rootCommand The root command (/<this.getName())
     * @return The modified root command
     */
    @Override
    protected Command initCommands(Command rootCommand) {
        rootCommand.addSubcommand(new LeaderboardCommand());
        return rootCommand;
    }

    /**
     * Teleports the player back to the checkpoint they are on
     *
     * @param player The player to teleport
     */
    public static void sendToCheckpoint(EndercubePlayer player) {
        int currentCheckpoint = player.getTag(Tag.Integer("parkour_checkpoint"));
        Instance currentInstance = player.getInstance();
        Pos[] checkpoints = currentInstance.getTag(Tag.Transient("checkpointsPosArray"));

        if (currentCheckpoint == -1) {
            player.teleport(currentInstance.getTag(Tag.Transient("spawnPos")));
            player.setTag(Tag.Long("parkour_startTime"), System.currentTimeMillis()); // Reset timer
        } else {
            player.teleport(checkpoints[currentCheckpoint].add(0.5, 0, 0.5)); // Teleport to the center of the block
        }
    }

    public static void restartMap(EndercubePlayer player) {
        player.teleport(player.getInstance().getTag(Tag.Transient("spawnPos")));
        player.setTag(Tag.Boolean("parkour_timerStarted"), false);
        player.setTag(Tag.Integer("parkour_checkpoint"), -1);
    }
}