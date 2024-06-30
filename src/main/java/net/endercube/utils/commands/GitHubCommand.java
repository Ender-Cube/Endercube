package net.endercube.utils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.builder.Command;

public class GitHubCommand extends Command {
    public GitHubCommand() {
        super("github", "bug");

        setDefaultExecutor(((commandSender, commandContext) -> {
            commandSender.sendMessage(Component.empty()
                    .append(
                            Component.text("All the code for Endercube is open source on our ")
                    )
                    .append(
                            Component.text("GitHub")
                                    .decorate(TextDecoration.UNDERLINED)
                                    .decorate(TextDecoration.BOLD)
                                    .hoverEvent(HoverEvent.showText(Component.text("https://github.com/Ender-Cube/Endercube")))
                                    .clickEvent(ClickEvent.openUrl("https://github.com/Ender-Cube/Endercube"))
                    )
                    .append(
                            Component.text("! That means that you can modify the code, look at how we implement features or easily ")
                    )
                    .append(
                            Component.text("report bugs")
                                    .decorate(TextDecoration.UNDERLINED)
                                    .decorate(TextDecoration.BOLD)
                                    .hoverEvent(HoverEvent.showText(Component.text("https://github.com/Ender-Cube/Endercube/issues/new")))
                                    .clickEvent(ClickEvent.openUrl("https://github.com/Ender-Cube/Endercube/issues/new"))
                    )
                    .append(
                            Component.text(".")
                    )
            );
        }));
    }
}
