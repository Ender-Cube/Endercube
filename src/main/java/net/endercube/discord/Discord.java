package net.endercube.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.discord.listeners.AsyncPlayerConfiguration;
import net.endercube.discord.listeners.PlayerChat;
import net.endercube.discord.listeners.PlayerDisconnect;
import net.endercube.discord.listeners.PlayerParkourWorldRecord;
import net.endercube.gamelib.config.ConfigFile;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discord {

    public static Logger logger;
    public static WebhookClient webhookClient;
    private static final ConfigFile config;

    static {
        logger = LoggerFactory.getLogger(Discord.class);
        config = new ConfigFile("discord", "The Discord bots settings");
    }

    public static void init() {
        logger.info("Initialising Discord integration");

        // Initialise a webhookClient
        WebhookClientBuilder builder = new WebhookClientBuilder(
                config.getOrSetDefault(config.getConfig().node("webhookURL"), "<The webhook's url>")
        );

        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Discord integration");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        webhookClient = builder.build();

        // Register Events
        MinecraftServer.getGlobalEventHandler()
                .addListener(new AsyncPlayerConfiguration())
                .addListener(new PlayerChat())
                .addListener(new PlayerDisconnect())
                .addListener(new PlayerParkourWorldRecord());

        // Say the server has started
        sendServerMessage("The server has started!");

        // Say when the server has stopped
        Runtime.getRuntime().addShutdownHook((new Thread(() -> sendServerMessage("The server is shutting down :("))));
    }

    public static void sendMessage(EndercubePlayer player, String message) {
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername())
                // use the player's head as the avatar
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid())
                .setContent(message)
                .build()
        );
    }

    public static void sendServerMessage(String message) {
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername("Endercube")
                // use the Endercube logo as the avatar
                .setAvatarUrl("https://raw.githubusercontent.com/Ender-Cube/Branding/main/Logo/EndercubeSquare_1024x1024.png")
                .setContent(message)
                .build()
        );
    }
}