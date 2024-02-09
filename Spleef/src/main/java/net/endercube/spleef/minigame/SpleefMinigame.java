package net.endercube.spleef.minigame;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.EndercubeServer;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.endercube.spleef.minigame.commands.VoteCommand;
import net.endercube.spleef.minigame.listeners.MinigamePlayerJoin;
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

    public SpleefMinigame(EndercubeServer endercubeServer) {
        super(endercubeServer);
        spleefMinigame = this;

        eventNode
                .addListener(new MinigamePlayerJoin());

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
        rootCommand.addSubcommand(new VoteCommand());
        return rootCommand;
    }

    public int getMinimumPlayers() {
        return config.node("minimumPlayers").getInt();
    }

    public int getMaximumPlayers() {
        return config.node("maximumPlayers").getInt();
    }
}