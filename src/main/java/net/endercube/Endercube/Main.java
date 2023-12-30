package net.endercube.Endercube;

import net.endercube.Common.EndercubeServer;
import net.endercube.Common.commands.GenericRootCommand;
import net.endercube.Endercube.blocks.Sign;
import net.endercube.Endercube.blocks.Skull;
import net.endercube.Endercube.commands.ResetTimeCommand;
import net.endercube.Endercube.listeners.AsyncPlayerConfiguration;
import net.endercube.Endercube.listeners.PlayerDisconnect;
import net.endercube.Hub.HubMinigame;
import net.endercube.Parkour.ParkourMinigame;
import net.minestom.server.MinecraftServer;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entrypoint for the server
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    @Nullable
    public static EndercubeServer endercubeServer;

    public static void main(String[] args) {
        logger.info("Starting Server");

        endercubeServer = new EndercubeServer.EndercubeServerBuilder()
                .addGlobalEvent(new AsyncPlayerConfiguration())
                .addGlobalEvent(new PlayerDisconnect())
                .addBlockHandler(NamespaceID.from("minecraft:sign"), Sign::new)
                .addBlockHandler(NamespaceID.from("minecraft:skull"), Skull::new)
                .startServer();

        endercubeServer
                .addMinigame(new ParkourMinigame(endercubeServer))
                .addMinigame(new HubMinigame(endercubeServer));

        GenericRootCommand adminCommand = new GenericRootCommand("admin");

        adminCommand.setCondition(((sender, commandString) -> sender.hasPermission(new Permission("operator"))));
        adminCommand.addSubcommand(new ResetTimeCommand());
        MinecraftServer.getCommandManager().register(adminCommand);
    }
}
