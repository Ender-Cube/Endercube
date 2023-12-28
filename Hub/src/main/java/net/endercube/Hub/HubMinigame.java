package net.endercube.Hub;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.EndercubeServer;
import net.endercube.Common.NPC;
import net.endercube.Common.dimensions.FullbrightDimension;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Hub.listeners.MinigamePlayerJoin;
import net.endercube.Hub.listeners.PlayerMove;
import net.endercube.Parkour.inventories.ParkourMapInventory;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the entrypoint for the Hub minigame
 */
public class HubMinigame extends EndercubeMinigame {

    public static HubMinigame hubMinigame;

    public HubMinigame(EndercubeServer endercubeServer) {
        super(endercubeServer);
        hubMinigame = this;

        // Create NPC(s)
        new NPC("Parkour", PlayerSkin.fromUsername("Jeb_"), getInstances().get(0), new Pos(0.5, 71, -5.5),
                player -> player.openInventory(ParkourMapInventory.getInventory(false)));

        // Register events
        eventNode
                .addListener(new PlayerMove())
                .addListener(new MinigamePlayerJoin());

        this.registerCommands();
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
    public ArrayList<InstanceContainer> initInstances() {
        ArrayList<InstanceContainer> instances = new ArrayList<>();
        InstanceContainer hubInstance;
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

        hubInstance.setTimeRate(0);

        // Set the spawn positions
        hubInstance.setTag(Tag.Transient("spawnPos"), configUtils.getPosFromConfig(config.node("world", "spawnPosition")));
        hubInstance.setTag(Tag.Integer("deathY"), Integer.parseInt(configUtils.getOrSetDefault(config.node("world", "deathY"), "9")));
        instances.add(hubInstance);
        return instances;
    }


    /**
     * Sends the player to the hub
     * @param rootCommand The root command (/<this.getName())
     * @return the modified rootCommand
     */
    @Override
    protected Command initCommands(Command rootCommand) {
        rootCommand.setDefaultExecutor(((sender, context) -> {
            EndercubePlayer player = (EndercubePlayer) sender;
            player.gotoHub();
        }));
        return rootCommand;
    }
}