package net.endercube.common.commands;

import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;


public class GenericRootCommand extends Command {

    /**
     * Creates an empty command with the given name
     *
     * @param name The name of the command
     */
    public GenericRootCommand(@NotNull String name) {
        super(name);

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("This is the root command and does nothing on it's own, Try pressing tab to see the subcommands");
        }));
    }
}
