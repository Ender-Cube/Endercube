package net.endercube.Endercube;

import net.endercube.Endercube.listeners.PlayerLogin;
import net.endercube.Hub.HubMinigame;
import net.endercube.Parkour.ParkourMinigame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entrypoint for the server
 */
public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static EndercubeServer endercubeServer;

    public static void main(String[] args) {
        logger.info("Starting Server");

        endercubeServer = new EndercubeServer.EndercubeServerBuilder()
                .addGlobalEvent(new PlayerLogin())
                .startServer()
//                .addMinigame(new ParkourMinigame())
                .addMinigame(new HubMinigame());
    }
}
