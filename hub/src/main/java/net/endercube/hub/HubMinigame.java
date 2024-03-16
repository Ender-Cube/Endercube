package net.endercube.hub;

import net.endercube.common.EndercubeMinigame;
import net.endercube.common.EndercubeServer;
import net.endercube.common.NPC;
import net.endercube.common.dimensions.FullbrightDimension;
import net.endercube.common.events.MinigamePlayerJoinEvent;
import net.endercube.common.players.EndercubePlayer;
import net.endercube.hub.listeners.MinigamePlayerJoin;
import net.endercube.hub.listeners.PlayerMove;
import net.endercube.parkour.inventories.ParkourMapInventory;
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

        // Create game NPCs
        new NPC("Parkour", PlayerSkin.fromUsername("Jeb_"), getInstances().getFirst(), new Pos(0.5, 101, -5.5),
                player -> player.openInventory(ParkourMapInventory.getInventory(false)));

        new NPC("Spleef", PlayerSkin.fromUsername("MinieChaos"), getInstances().getFirst(), new Pos(-2.5, 101, -5.5),
                player -> MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerJoinEvent("spleef", (EndercubePlayer) player, "")));

        // Create credits NPCs
        new NPC("Rambino_PorkChop", PlayerSkin.fromUsername("Rambino_PorkChop"), getInstances().getFirst(), new Pos(-39.5, 101, 1.5, -90, 0),
                player -> {
                });

        new NPC("Zax71", PlayerSkin.fromUsername("Zax71"), getInstances().getFirst(), new Pos(-39.5, 101, 0.5, -90, 0),
                player -> player.sendMessage("I do the dev work on the server!"));

        new NPC("david123rob", PlayerSkin.fromUsername("david123rob"), getInstances().getFirst(), new Pos(-39.5, 101, -0.5, -90, 0),
                player -> player.sendMessage("I built the hub!"));


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
     *
     * @return An {@code ArrayList} containing the hub instance
     */
    @Override
    public ArrayList<InstanceContainer> initInstances() throws IOException {
        ArrayList<InstanceContainer> instances = new ArrayList<>();
        InstanceContainer hubInstance;
        // Load the map
        logger.info("Loading Hub world");
        hubInstance = MinecraftServer.getInstanceManager().createInstanceContainer(
                FullbrightDimension.INSTANCE,
                new PolarLoader(Paths.get("./config/worlds/hub.polar"))
        );

        hubInstance.setTimeRate(0);

        // Set the spawn positions
        hubInstance.setTag(Tag.Transient("spawnPos"), config.getPosFromConfig(config.getConfig().node("world", "spawnPosition")));
        hubInstance.setTag(Tag.Integer("deathY"), Integer.parseInt(config.getOrSetDefault(config.getConfig().node("world", "deathY"), "9")));
        instances.add(hubInstance);
        return instances;
    }


    /**
     * Sends the player to the hub
     *
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