package net.endercube.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import net.endercube.discord.bot.DiscordBot;
import net.endercube.discord.listeners.AsyncPlayerConfiguration;
import net.endercube.discord.listeners.PlayerChat;
import net.endercube.discord.listeners.PlayerDisconnect;
import net.endercube.discord.listeners.PlayerParkourWorldRecord;
import net.endercube.gamelib.config.ConfigFile;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discord {

    public static Logger logger;
    public static DiscordWebhook discordWebhook;
    private static final ConfigFile config;

    static {
        logger = LoggerFactory.getLogger(Discord.class);
        config = new ConfigFile("discord", "The Discord bots settings");
    }

    public static void init() {
        logger.info("Initialising Discord integration");

        // Initialise a webhookClient
        discordWebhook = new DiscordWebhook();

        // Init the Discord bot
        DiscordBot.initDiscordBot();


        // Register Events
        MinecraftServer.getGlobalEventHandler()
                .addListener(new AsyncPlayerConfiguration())
                .addListener(new PlayerChat())
                .addListener(new PlayerDisconnect())
                .addListener(new PlayerParkourWorldRecord());

        // Say the server has started
        discordWebhook.sendServerMessage("The server has started!");

        // Say when the server has stopped
        Runtime.getRuntime().addShutdownHook((new Thread(() -> discordWebhook.sendServerMessage("The server is shutting down :("))));
    }

    private static WebhookClient initWebhook() {
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

        return builder.build();
    }
}