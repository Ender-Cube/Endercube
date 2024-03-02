package net.endercube.spleef.minigame;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.EndercubeServer;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.endercube.spleef.minigame.listeners.MinigamePlayerJoin;
import net.endercube.spleef.minigame.listeners.MinigamePlayerLeave;
import net.endercube.spleef.minigame.listeners.PlayerMove;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SpleefMinigame extends EndercubeMinigame {

    public static SpleefMinigame spleefMinigame;
    public Instance spleefHub;
    public static SpleefDatabase database;
    private final int minimumPlayersConfigValue = config.node("minimumPlayers").getInt();
    private final int maximumPlayersConfigValue = config.node("maximumPlayers").getInt();

    public SpleefMinigame(EndercubeServer endercubeServer) {
        super(endercubeServer);
        spleefMinigame = this;

        eventNode
                .addListener(new PlayerMove())
                .addListener(new MinigamePlayerLeave())
                .addListener(new MinigamePlayerJoin());

        try {
            database = this.createDatabase(SpleefDatabase.class);
        } catch (Exception e) {
            logger.error("Failed to create a spleef database");
            throw new RuntimeException(e);
        }

        this.registerCommands();
    }

    @Override
    public String getName() {
        return "spleef";
    }

    @Override
    protected ArrayList<InstanceContainer> initInstances() throws IOException {
        final File[] worldFiles = Paths.get("./config/worlds/spleef").toFile().listFiles();
        ArrayList<InstanceContainer> instances = new ArrayList<>();
        final String hubName = "spleefHub";

        // Check that there are maps
        if (worldFiles == null) {
            logger.error("No spleef maps found, please place some Polar worlds in ./config/worlds/spleef/");
            MinecraftServer.stopCleanly();
            return null;
        }

        // Check that a hub exists
        if (Arrays.stream(worldFiles).noneMatch((file) -> file.getName().equals(hubName + ".polar"))) {
            logger.error("No spleef hub found, please create /config/worlds/spleef/" + hubName + ".polar");
            MinecraftServer.stopCleanly();
            return null;
        }

        // Load the hub
        spleefHub = MinecraftServer.getInstanceManager().createInstanceContainer(
                FullbrightDimension.INSTANCE,
                new PolarLoader(Paths.get("./config/worlds/spleef/" + hubName + ".polar"))
        );

        spleefHub.setTimeRate(0);

        spleefHub.setTag(Tag.Transient("spawnPos"), configUtils.getPosFromConfig(config.node("hub", "spawn")));
        spleefHub.setTag(Tag.Integer("deathY"), config.node("hub", "deathY").getInt());

        // Loop through all maps to load them
        for (File worldFile : worldFiles) {
            // Get the name of the map by removing .polar from the file name
            String mapName = worldFile.getName();
            mapName = mapName.substring(0, mapName.length() - 6);

            // We've already loaded the hub, no need to do anything else
            if (mapName.equals(hubName)) {
                continue;
            }

            CommentedConfigurationNode configNode = config.node("maps", mapName);

            // Load instance
            InstanceContainer currentInstance = MinecraftServer.getInstanceManager().createInstanceContainer(
                    FullbrightDimension.INSTANCE,
                    new PolarLoader(Path.of(worldFile.getPath()))
            );

            // Add tags
            var loadRadiusNode = configNode.node("chunkLoadRadius");
            loadRadiusNode.comment("The radius to load chunks around when copying the map. Should contain the whole map.\n0 is 1 chunk and 2 is a 3x3");
            logger.debug("Load Radius for " + mapName + " is: " + loadRadiusNode.getInt());

            currentInstance.setTag(Tag.Transient("spawnPos"), configUtils.getPosFromConfig(configNode.node("spawn")));
            currentInstance.setTag(Tag.Integer("deathY"), configNode.node("deathY").getInt());
            currentInstance.setTag(Tag.String("name"), mapName);
            currentInstance.setTag(Tag.Integer("chunkLoadRadius"), loadRadiusNode.getInt());


            instances.add(currentInstance);
            logger.info("Added the Spleef map: " + mapName);
        }


        return instances;
    }

    @Override
    protected Command initCommands(Command rootCommand) {
        // TODO: implement map voting
        // rootCommand.addSubcommand(new VoteCommand());
        return rootCommand;
    }

    public int getMinimumPlayers() {
        checkMaxMin();
        return minimumPlayersConfigValue;
    }

    public int getMaximumPlayers() {
        checkMaxMin();
        return maximumPlayersConfigValue;
    }

    private void checkMaxMin() {
        if (minimumPlayersConfigValue > maximumPlayersConfigValue) {
            logger.error("The maximum player count for spleef should be larger than the minimum player count");
            MinecraftServer.stopCleanly();
        }

        if (minimumPlayersConfigValue < 2) {
            logger.error("There must be at least 2 players in a spleef game! Update your minimum player count to reflect this");
            MinecraftServer.stopCleanly();
        }
    }
}