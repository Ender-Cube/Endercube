package net.endercube.endercube.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.utils.MathUtils;

import static net.endercube.common.utils.ComponentUtils.getTitle;
import static net.endercube.endercube.listeners.ServerTickMonitor.RAW_MSPT;

public class PerformanceCommand extends Command {
    public PerformanceCommand() {
        super("performance");

        double mspt = MathUtils.round(RAW_MSPT, 2);
        double tps = Math.min(20, 1000 / mspt);

        setDefaultExecutor(((commandSender, commandContext) -> {
            commandSender.sendMessage(getTitle(Component.text("Server Stats"))
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
