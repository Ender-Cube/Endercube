package net.endercube.Hub;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.NPC;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Hub.listeners.MinigamePlayerJoinEventListener;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the entrypoint for the Hub minigame
 */
public class HubMinigame extends EndercubeMinigame {

    private InstanceContainer hubInstance;

    public HubMinigame() {
        // Load the map
        try {
            logger.info("Loading Hub world");
            hubInstance = MinecraftServer.getInstanceManager().createInstanceContainer(
                    FullbrightDimension.INSTANCE,
                    new PolarLoader(Paths.get("./config/worlds/hub.polar"))
            );
        } catch (IOException e) {
            logger.error("Could not load the hub world in ./config/worlds/hub.polar");
            throw new RuntimeException(e);
        }

        // Create NPC(s)
        new NPC("Parkour", PlayerSkin.fromUsername("Jeb_"), hubInstance, new Pos(0.5, 71, -5.5),
                player -> player.sendMessage("// TODO: Parkour"));

        // Register events
        eventNode = EventNode.value("hub", EventFilter.ALL, (event) -> {
            logger.info("Event class: " + event.getClass());
            return true;
        });

        eventNode
                .addListener(new MinigamePlayerJoinEventListener());
    }



    @Override
    public String getName() {
        return "hub";
    }

    /**
     * There is only one hub, so only one item
     * @return An {@code ArrayList} containing the hub instance
     */
    @Override
    public ArrayList<InstanceContainer> getInstances() {
        ArrayList<InstanceContainer> instances = new ArrayList<>();
        instances.add(hubInstance);
        return instances;
    }

    @Override
    public Pos[] getSpawnPositions() {
        return configUtils.getPosListFromConfig(config.node("world", "spawnPositions"));
    }
}