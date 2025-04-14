package net.endercube.discord.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.endercube.discord.Discord;
import net.endercube.gamelib.config.ConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class DiscordBot {

    public static Logger logger;
    private static final ConfigFile config;

    static {
        logger = LoggerFactory.getLogger(Discord.class);
        config = new ConfigFile("discord", "The Discord bots settings");
    }

    public static void initDiscordBot() {
        String token = config.getOrSetDefault(config.getConfig().node("botToken"), "<Discord bot token>");
        String channelID = config.getOrSetDefault(config.getConfig().node("channelID"), "<Discord channel ID to use for two way chat>");

        EnumSet<GatewayIntent> intents = EnumSet.of(
                // Enables MessageReceivedEvent for guild (also known as servers)
                GatewayIntent.GUILD_MESSAGES,
                // Enables access to message.getContentRaw()
                GatewayIntent.MESSAGE_CONTENT
        );

        // Init the Discord bot
        JDA jda = JDABuilder.createLight(token, intents)
                .addEventListeners(new DiscordBotListeners())
                .setActivity(Activity.playing("mc.Endercube.net"))
                .build();

        // Runs on successful bot log in
        jda.getRestPing().queue(ping ->
                // shows ping in milliseconds
                logger.info("Discord bot successfully logged in with ping: " + ping + "ms")
        );

    }
}

