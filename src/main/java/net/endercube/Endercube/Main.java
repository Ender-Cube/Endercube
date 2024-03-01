package net.endercube.Endercube;

import net.endercube.Common.EndercubeServer;
import net.endercube.Common.commands.GenericRootCommand;
import net.endercube.Endercube.blocks.Sign;
import net.endercube.Endercube.blocks.Skull;
import net.endercube.Endercube.commands.DiscordCommand;
import net.endercube.Endercube.commands.PerformanceCommand;
import net.endercube.Endercube.commands.admin.BanCommand;
import net.endercube.Endercube.commands.admin.KickCommand;
import net.endercube.Endercube.commands.admin.ResetParkourTimeCommand;
import net.endercube.Endercube.commands.admin.UnbanCommand;
import net.endercube.Endercube.listeners.AsyncPlayerConfiguration;
import net.endercube.Endercube.listeners.PlayerDisconnect;
import net.endercube.Endercube.listeners.ServerTickMonitor;
import net.endercube.Hub.HubMinigame;
import net.endercube.Parkour.ParkourMinigame;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.minestom.server.MinecraftServer;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

/**
 * This is the entrypoint for the server
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    @Nullable
    public static EndercubeServer endercubeServer;
    public static JedisPooled jedis;

    public static void main(String[] args) {
        logger.info("Starting Server");

        endercubeServer = new EndercubeServer.EndercubeServerBuilder()
                .addGlobalEvent(new AsyncPlayerConfiguration())
                .addGlobalEvent(new PlayerDisconnect())
                .addGlobalEvent(new ServerTickMonitor())
                .addBlockHandler(NamespaceID.from("minecraft:sign"), Sign::new)
                .addBlockHandler(NamespaceID.from("minecraft:skull"), Skull::new)
                .startServer();

        endercubeServer
                .addMinigame(new ParkourMinigame(endercubeServer))
                .addMinigame(new SpleefMinigame(endercubeServer))
                .addMinigame(new HubMinigame(endercubeServer));

        jedis = endercubeServer.getJedisPooled();

        initCommands();
    }

    private static void initCommands() {
        // Add admin commands
        GenericRootCommand adminCommand = new GenericRootCommand("admin");
        adminCommand.setCondition(((sender, commandString) -> sender.hasPermission(new Permission("operator"))));
        adminCommand.addSubcommand(new ResetParkourTimeCommand());
        adminCommand.addSubcommand(new BanCommand());
        adminCommand.addSubcommand(new UnbanCommand());
        adminCommand.addSubcommand(new KickCommand());

        // Add public user commands
        MinecraftServer.getCommandManager().register(adminCommand);
        MinecraftServer.getCommandManager().register(new DiscordCommand());
        MinecraftServer.getCommandManager().register(new PerformanceCommand());
    }
}
