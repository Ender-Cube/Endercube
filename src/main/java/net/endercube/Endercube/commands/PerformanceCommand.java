package net.endercube.Endercube.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.builder.Command;
import net.minestom.server.utils.MathUtils;

import static net.endercube.Endercube.listeners.ServerTickMonitor.RAW_MSPT;

public class PerformanceCommand extends Command {
    public PerformanceCommand() {
        super("performance");

        double mspt = MathUtils.round(RAW_MSPT, 2);
        double tps = Math.min(20, 1000 / mspt);

        setDefaultExecutor(((commandSender, commandContext) -> {
            commandSender.sendMessage(Component.empty()
                    .append(
                            Component.text("    ").decorate(TextDecoration.STRIKETHROUGH).color(NamedTextColor.BLUE)
                    )
                    .append(
                            Component.text(" Server Performance ").color(NamedTextColor.BLUE)
                    )
                    .append(
                            Component.text("    ").decorate(TextDecoration.STRIKETHROUGH).color(NamedTextColor.BLUE)
                    )
                    .append(Component.newline())
                    .append(
                            Component.text("MSPT: ").color(NamedTextColor.WHITE)
                    )
                    .append(
                            Component.text(mspt).color(NamedTextColor.GRAY)
                    )
                    .append(
                            Component.text(".").color(NamedTextColor.WHITE)
                    )
                    .append(Component.newline())
                    .append(
                            Component.text("TPS: ").color(NamedTextColor.WHITE)
                    )
                    .append(
                            Component.text(tps).color(NamedTextColor.GRAY)
                    )
                    .append(
                            Component.text(".").color(NamedTextColor.WHITE)
                    )
            );
        }));
    }
}
