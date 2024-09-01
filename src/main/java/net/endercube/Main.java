package net.endercube;

import net.endercube.discord.Discord;
import net.endercube.gamelib.EndercubeServer;
import net.endercube.gamelib.commands.GenericRootCommand;
import net.endercube.global.blocks.Sign;
import net.endercube.global.blocks.Skull;
import net.endercube.global.commands.DiscordCommand;
import net.endercube.global.commands.GitHubCommand;
import net.endercube.global.commands.PerformanceCommand;
import net.endercube.global.commands.admin.BanCommand;
import net.endercube.global.commands.admin.KickCommand;
import net.endercube.global.commands.admin.ResetParkourTimeCommand;
import net.endercube.global.commands.admin.UnbanCommand;
import net.endercube.global.listeners.AsyncPlayerConfiguration;
import net.endercube.global.listeners.AsyncPlayerPreLogin;
import net.endercube.global.listeners.PlayerDisconnect;
import net.endercube.global.listeners.ServerTickMonitor;
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
                .addGlobalEvent(new AsyncPlayerPreLogin())
                .addBlockHandler(NamespaceID.from("minecraft:sign"), Sign::new)
                .addBlockHandler(NamespaceID.from("minecraft:skull"), Skull::new)
                .startServer();

        endercubeServer
                .addMinigame(new ParkourMinigame(endercubeServer))
                .addMinigame(new SpleefMinigame(endercubeServer))
                .addMinigame(new HubMinigame(endercubeServer));

        jedis = endercubeServer.getJedisPooled();

        initCommands();

        Discord.init();
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
