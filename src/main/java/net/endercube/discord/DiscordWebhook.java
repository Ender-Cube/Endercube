package net.endercube.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.gamelib.config.ConfigFile;
import net.endercube.global.EndercubePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordWebhook {

    private static Logger logger;
    private static final ConfigFile config;
    private WebhookClient webhookClient;

    static {
        logger = LoggerFactory.getLogger(Discord.class);
        config = new ConfigFile("discord", "The Discord bots settings");
    }

    public DiscordWebhook() {
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

        this.webhookClient = builder.build();
    }

    public void sendMessage(EndercubePlayer player, String message) {
        this.webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername())
                // use the player's head as the avatar
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid())
                .setContent(message)
                .build()
        );
    }

    public void sendServerMessage(String message) {
        this.webhookClient.send(new WebhookMessageBuilder()
                .setUsername("Endercube")
                // use the Endercube logo as the avatar
                .setAvatarUrl("https://raw.githubusercontent.com/Ender-Cube/Branding/main/Logo/EndercubeSquare_1024x1024.png")
                .setContent(message)
                .build()
        );
    }
}
