package net.endercube.discord.bot;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.adventure.audience.Audiences;
import org.jetbrains.annotations.NotNull;

public class DiscordBotListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        String message = event.getMessage().getContentDisplay();

        // Don't echo the bot messages too!
        if (user.isBot() || user.isSystem()) {
            return;
        }

        Component messageComponent = Component.text()
                .content("[Discord] ")
                .color(TextColor.fromHexString("#5865F2"))
                .decoration(TextDecoration.BOLD, true)
                .append(
                        Component.text(user.getEffectiveName() + " ")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.BOLD, true)
                )
                .append(
                        Component.text(message)
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.BOLD, false)
                )
                .build();

        Audiences.server().sendMessage(messageComponent);
    }

}
