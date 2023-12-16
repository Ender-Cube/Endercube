package net.endercube.Endercube;

import net.endercube.Common.EndercubeServer;
import net.endercube.Endercube.blocks.Sign;
import net.endercube.Endercube.blocks.Skull;
import net.endercube.Endercube.listeners.PlayerLogin;
import net.endercube.Hub.HubMinigame;
import net.endercube.Parkour.ParkourMinigame;
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
                .addGlobalEvent(new PlayerLogin())
                .addBlockHandler(NamespaceID.from("minecraft:sign"), Sign::new)
                .addBlockHandler(NamespaceID.from("minecraft:skull"), Skull::new)
                .startServer()
                .addMinigame(new ParkourMinigame())
                .addMinigame(new HubMinigame());
    }


}
