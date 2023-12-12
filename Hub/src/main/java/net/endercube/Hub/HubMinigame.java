package net.endercube.Hub;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.InstanceContainer;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This is the entrypoint for the Hub minigame
 */
public class HubMinigame extends EndercubeMinigame {

    private InstanceContainer hubMap = null;

    @Override
    public EventNode<Event> getEventNode() {
        return EventNode.all("hub");
    }

    @Override
    public String getName() {
        return "hub";
    }

    @Override
    public InstanceContainer getInstance() {
        if (hubMap != null) {
            return hubMap;
        }

        try {
            logger.info("Loading Hub world");
            hubMap = MinecraftServer.getInstanceManager().createInstanceContainer(
                    FullbrightDimension.INSTANCE,
                    new PolarLoader(Paths.get("./config/worlds/hub.polar"))
            );
            return hubMap;
        } catch (IOException e) {
            logger.error("Could not load the hub world in ./config/worlds/hub.polar");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pos[] getSpawnPositions() {
        return configUtils.getPosListFromConfig(config.node("world", "spawnPositions"));
    }
}