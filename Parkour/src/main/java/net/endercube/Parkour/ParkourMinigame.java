package net.endercube.Parkour;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.endercube.Parkour.listeners.MinigamePlayerJoinEventListener;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
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

    public ParkourMinigame() {
        parkourMinigame = this;

        // Register events
        eventNode
                .addListener(new MinigamePlayerJoinEventListener());
    }

    @Override
    public String getName() {
        return "parkour";
    }



    @Override
    protected ArrayList<InstanceContainer> initInstances() {
        File[] worldFiles = Paths.get("./config/worlds/parkour").toFile().listFiles();
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

                ConfigurationNode configNode = config.node("maps", mapName);


                InstanceContainer currentInstance = MinecraftServer.getInstanceManager().createInstanceContainer(
                        FullbrightDimension.INSTANCE,
                        new PolarLoader(Path.of(worldFile.getPath()))
                );

                // Set all tags from config
                currentInstance.setTag(Tag.Transient("checkpointsPosArray"), configUtils.getPosListFromConfig(configNode.node("checkpoints")));
                currentInstance.setTag(Tag.Integer("death-y"), configNode.node("death-y").getInt());
                currentInstance.setTag(Tag.String("difficulty"), configNode.node("difficulty").getString());
                currentInstance.setTag(Tag.Transient("finishPos"), configUtils.getPosFromConfig(configNode.node("finish")));
                currentInstance.setTag(Tag.Integer("order"), configNode.node("order").getInt());
                currentInstance.setTag(Tag.Transient("spawnPos"), configUtils.getPosFromConfig(configNode.node("spawn")));
                instances.add(currentInstance);

                logger.info("Added the Parkour map: " + mapName);
                logger.debug(mapName + "'s details:");
                logger.debug("checkpointsPosArray: " + currentInstance.getTag(Tag.Transient("checkpointsPosArray")));
                logger.debug("death-y: " + currentInstance.getTag(Tag.Integer("death-y")));
                logger.debug("difficulty: " + currentInstance.getTag(Tag.String("difficulty")));
                logger.debug("finishPos: " + currentInstance.getTag(Tag.Transient("finishPos")));
                logger.debug("order: " + currentInstance.getTag(Tag.Integer("order")));
                logger.debug("spawnPos: " + currentInstance.getTag(Tag.Transient("spawnPos")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return instances;
    }
}