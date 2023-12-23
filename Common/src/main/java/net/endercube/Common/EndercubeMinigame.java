package net.endercube.Common;

import net.endercube.Common.commands.GenericRootCommand;
import net.endercube.Common.database.AbstractDatabase;
import net.endercube.Common.events.eventTypes.PlayerMinigameEvent;
import net.endercube.Common.utils.ConfigUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import redis.clients.jedis.JedisPooled;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Override this class to add minigames
 */
public abstract class EndercubeMinigame {

    public static final Logger logger;
    private HoconConfigurationLoader configLoader;
    protected CommentedConfigurationNode config;
    protected ConfigUtils configUtils;
    protected @NotNull EventNode<Event> eventNode;
    protected ArrayList<InstanceContainer> instances;
    private EndercubeServer endercubeServer;

    // Create an instance of the logger
    static {
        logger = LoggerFactory.getLogger(EndercubeMinigame.class);
    }

    protected EndercubeMinigame(EndercubeServer endercubeServer) {
        this.endercubeServer = endercubeServer;

        createConfig();

        // Initialise instances
        instances = new ArrayList<>();
        instances = initInstances();

        // Create the eventNode
        // Filter if the event is an instanceEvent happening in our instances or a minigameEvent on this minigame
        eventNode = EventNode.event(getName(), EventFilter.ALL, (Event event) -> {
            if (event instanceof InstanceEvent instanceEvent) {
                return getInstances().contains(instanceEvent.getInstance());
            }

            if (event instanceof PlayerMinigameEvent minigameEvent) {
                return minigameEvent.getMinigame().equals(getName());
            }

            return false;
        });

        // Register the event node
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);


    }


    /**
     * The name of this minigame. must be unique
     * @return The name
     */
    public abstract String getName();

    /**
     * Loads all the instances
     * @return An ArrayList of InstanceContainer's
     */
    protected abstract ArrayList<InstanceContainer> initInstances();

    /**
     * Called to add subcommands to the rootCommand given
     * @param rootCommand The root command (/<this.getName())
     * @return The root command with extra commands added to it (or not! I don't mind)
     */
    protected abstract Command initCommands(Command rootCommand);

    protected void registerCommands() {
        // Init and register commands
        MinecraftServer.getCommandManager().register(
                this.initCommands(new GenericRootCommand(this.getName()))
        );
    }

    /**
     * Get the instances associated with this minigame
     * @return The instances
     */
    public ArrayList<InstanceContainer> getInstances() {
        return instances;
    }

    public void setEndercubeServer(EndercubeServer server) {
        endercubeServer = server;
    }

    @Nullable
    public EndercubeServer getEndercubeServer() {
        return endercubeServer;
    }

    /**
     * Creates a database object
     * @param clazz The class extending AbstractDatabase to use
     * @return An instance of the database
     */
    public <T extends AbstractDatabase> T createDatabase(Class<T> clazz) throws Exception {
        CommentedConfigurationNode globalConfigNode = this.getEndercubeServer().getGlobalConfig();
        String jedisURL = this.getEndercubeServer().getGlobalConfigUtils().getOrSetDefault(globalConfigNode.node("database", "redis", "url"), "localhost");
        int jedisPort = Integer.parseInt(this.getEndercubeServer().getGlobalConfigUtils().getOrSetDefault(globalConfigNode.node("database", "redis", "port"), "6379"));

        return clazz.getConstructor(JedisPooled.class, String.class).newInstance(new JedisPooled(jedisURL, jedisPort), this.getName());
    }

    private void createConfig() {
        // Create config and configUtils
        String fileName = getName() + ".conf";


        configLoader = HoconConfigurationLoader.builder()
                .path(Paths.get("./config/" + fileName))
                .defaultOptions(configurationOptions -> configurationOptions.header("This is the configuration file for the " + getName() + " minigame"))
                .build();


        try {
            config = configLoader.load();
        } catch (ConfigurateException e) {
            logger.error("An error occurred while loading \"" + fileName + "\": " + e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            MinecraftServer.stopCleanly();
        }

        configUtils = new ConfigUtils(configLoader, config);

        // Required to create the config file
        configUtils.saveConfig();
    }
}
