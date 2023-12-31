package net.endercube.Endercube.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("discord");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(Component.text("")
                    .append(Component.text("Join our "))
                    .append(
                            Component.text("Discord")
                            .color(TextColor.fromHexString("#5865F2"))
                            .clickEvent(ClickEvent.openUrl("https://discord.com/invite/x3aynQK"))
                            .hoverEvent(HoverEvent.showText(Component.text("https://discord.com/invite/x3aynQK")))
                    )
                    .append(Component.text("!")));
        }));
    }
}
