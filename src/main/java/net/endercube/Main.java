package net.endercube;

import net.endercube.discord.Discord;
import net.endercube.gamelib.EndercubeServer;
import net.endercube.gamelib.commands.GenericRootCommand;
import net.endercube.gamelib.permissions.PermissionLevel;
import net.endercube.gamelib.permissions.PermissionManager;
import net.endercube.global.EndercubePlayer;
import net.endercube.global.blocks.Sign;
import net.endercube.global.blocks.Skull;
import net.endercube.global.commands.DiscordCommand;
import net.endercube.global.commands.GitHubCommand;
import net.endercube.global.commands.GlistCommand;
import net.endercube.global.commands.PerformanceCommand;
import net.endercube.global.commands.admin.BanCommand;
import net.endercube.global.commands.admin.KickCommand;
import net.endercube.global.commands.admin.ResetParkourTimeCommand;
import net.endercube.global.commands.admin.UnbanCommand;
import net.endercube.global.listeners.AsyncPlayerConfiguration;
import net.endercube.global.listeners.PlayerDisconnect;
import net.endercube.global.listeners.ServerTickMonitor;
import net.endercube.hub.HubMinigame;
import net.endercube.parkour.ParkourMinigame;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.util.UUID;

/**
 * This is the entrypoint for the server
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    @Nullable
    public static EndercubeServer endercubeServer;
    public static JedisPooled jedis;
    public static PermissionManager permissionManager;

    public static void main(String[] args) {
        logger.info("Starting Server");

        endercubeServer = new EndercubeServer.EndercubeServerBuilder()
                .addGlobalEvent(new AsyncPlayerConfiguration())
                .addGlobalEvent(new PlayerDisconnect())
                .addGlobalEvent(new ServerTickMonitor())
                .addBlockHandler(Key.key("minecraft:sign"), Sign::new)
                .addBlockHandler(Key.key("minecraft:skull"), Skull::new)
                .startServer();

        endercubeServer
                .addMinigame(new ParkourMinigame(endercubeServer))
                .addMinigame(new SpleefMinigame(endercubeServer))
                .addMinigame(new HubMinigame(endercubeServer));

        jedis = endercubeServer.getJedisPooled();

        permissionManager = new PermissionManager();
        final UUID ZAX71_UUID = UUID.fromString("aa64173b-924d-42d0-a8fc-611a46a70258");
        permissionManager.setPermission(ZAX71_UUID, PermissionLevel.DEFAULT);

        initCommands();

        Discord.init();
    }

    private static void initCommands() {
        // Add admin commands
        GenericRootCommand adminCommand = new GenericRootCommand("admin");
        adminCommand.setCondition(((sender, commandString) -> permissionManager.hasPermission((EndercubePlayer) sender, PermissionLevel.ADMINISTRATOR)));
        adminCommand.addSubcommand(new ResetParkourTimeCommand());
        adminCommand.addSubcommand(new BanCommand());
        adminCommand.addSubcommand(new UnbanCommand());
        adminCommand.addSubcommand(new KickCommand());

        // Add public user commands
        MinecraftServer.getCommandManager().register(adminCommand);
        MinecraftServer.getCommandManager().register(new DiscordCommand());
        MinecraftServer.getCommandManager().register(new PerformanceCommand());
        MinecraftServer.getCommandManager().register(new GitHubCommand());
        MinecraftServer.getCommandManager().register(new GlistCommand());
    }
}
