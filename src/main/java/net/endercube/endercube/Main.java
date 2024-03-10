package net.endercube.endercube;

import net.endercube.common.EndercubeServer;
import net.endercube.common.commands.GenericRootCommand;
import net.endercube.endercube.blocks.Sign;
import net.endercube.endercube.blocks.Skull;
import net.endercube.endercube.commands.DiscordCommand;
import net.endercube.endercube.commands.GitHubCommand;
import net.endercube.endercube.commands.PerformanceCommand;
import net.endercube.endercube.commands.admin.BanCommand;
import net.endercube.endercube.commands.admin.KickCommand;
import net.endercube.endercube.commands.admin.ResetParkourTimeCommand;
import net.endercube.endercube.commands.admin.UnbanCommand;
import net.endercube.endercube.listeners.AsyncPlayerConfiguration;
import net.endercube.endercube.listeners.PlayerDisconnect;
import net.endercube.endercube.listeners.ServerTickMonitor;
import net.endercube.hub.HubMinigame;
import net.endercube.parkour.ParkourMinigame;
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
        MinecraftServer.getCommandManager().register(new GitHubCommand());
    }
}
