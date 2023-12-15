package net.endercube.Endercube;

import ch.qos.logback.classic.Level;
import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Common.utils.ConfigUtils;
import net.endercube.Endercube.blocks.Sign;
import net.endercube.Endercube.blocks.Skull;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The "abstract" endercubeServer that Main.java calls. Most of the main server's logic is in here
 */
public class EndercubeServer {

    private final ArrayList<EndercubeMinigame> minigames = new ArrayList<>();
    private final CommentedConfigurationNode globalConfig;
    private final ConfigUtils globalConfigUtils;

    private static final Logger logger;


    // Initializes the logger, only on the first initialization of this class
    static {
        logger = LoggerFactory.getLogger(EndercubeServer.class);
    }

    private EndercubeServer(EndercubeServerBuilder builder) {
        this.globalConfig = builder.globalConfig;
        this.globalConfigUtils = builder.globalConfigUtils;
    }

    /**
     * Add a minigame to the server
     * @param minigame The minigame to add
     * @return The builder
     */
    public EndercubeServer addMinigame(EndercubeMinigame minigame) {
        minigames.add(minigame);
        return this;
    }



    public @NotNull CommentedConfigurationNode getGlobalConfig() {
        return globalConfig;
    }
    public @NotNull ConfigUtils getGlobalConfigUtils() {
        return globalConfigUtils;
    }

    @Nullable
    public EndercubeMinigame getMinigameByName(@NotNull String name) {
        return minigames.stream()
                .filter(((endercubeMinigame) -> endercubeMinigame.getName().equals(name)))
                .findFirst()
                .orElse(null);
    }

    /**
     * A builder for EndercubeServer
     */
    public static class EndercubeServerBuilder {
        private final EventNode<Event> globalEvents;
        private CommentedConfigurationNode globalConfig;
        private ConfigUtils globalConfigUtils;

        public EndercubeServerBuilder() {
            globalEvents = EventNode.all("globalListeners");
        }


        /**
         * Add a global event, will be called regardless of minigame
         * @param listener The listener
         * @return The builder
         */
        public EndercubeServerBuilder addGlobalEvent(EventListener<?> listener) {
            globalEvents.addListener(listener);
            return this;
        }

        /**
         * Add a global event, will be called regardless of minigame
         * @param eventType The type of event to listen for
         * @param listener The listener
         * @return The builder
         */
        public <E extends Event> EndercubeServerBuilder addGlobalEvent(@NotNull Class<E> eventType, @NotNull Consumer<E> listener) {
            globalEvents.addListener(eventType, listener);
            return this;
        }

        /**
         * Creates and loads the global config
         */
        private void initGlobalConfig() {
            String fileName = "globalConfig.conf";

            // Create config directories
            if (!Files.exists(Paths.get("./config/worlds/"))) {
                logger.info("Creating configuration files");

                try {
                    Files.createDirectories(Paths.get("./config/worlds/"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .path(Paths.get("./config/globalConfig.conf"))
                    .build();

            try {
                globalConfig = loader.load();
            } catch (ConfigurateException e) {
                logger.error("An error occurred while loading " + fileName + ": " + e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
                MinecraftServer.stopCleanly();
            }

            globalConfigUtils = new ConfigUtils(loader, globalConfig);
        }

        /**
         * Start Minestom and the like
         */
        public void createServer() {

            // Server Initialization
            MinecraftServer minecraftServer = MinecraftServer.init();

            // Add global events
            MinecraftServer.getGlobalEventHandler().addChild(globalEvents);

            // Register block handlers
            MinecraftServer.getBlockManager().registerHandler(NamespaceID.from("minecraft:sign"), Sign::new);
            MinecraftServer.getBlockManager().registerHandler(NamespaceID.from("minecraft:skull"), Skull::new);
            logger.debug("Set block handlers");

            // Set encryption
            EncryptionMode encryptionMode;
            try {
                encryptionMode = EncryptionMode.valueOf(globalConfigUtils.getOrSetDefault(globalConfig.node("connection", "mode"), "online").toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Cannot read encryption mode from config, falling back to ONLINE");
                encryptionMode = EncryptionMode.ONLINE;
            }
            initEncryption(encryptionMode, globalConfigUtils.getOrSetDefault(globalConfig.node("connection", "velocitySecret"), ""));

            // Start server
            int port = Integer.parseInt(globalConfigUtils.getOrSetDefault(globalConfig.node("connection", "port"), "25565"));
            minecraftServer.start("0.0.0.0", port);

            // Set player provider
            MinecraftServer.getConnectionManager().setPlayerProvider(EndercubePlayer::new);
            logger.debug("Set player provider");

            // Register the void
            // Register minecraft:the_void
            MinecraftServer.getBiomeManager().addBiome(Biome
                    .builder()
                    .name(NamespaceID.from("minecraft:the_void"))
                    .build()
            );
        }

        enum EncryptionMode {
            ONLINE,
            VELOCITY
        }

        private void initEncryption(EncryptionMode mode, String velocitySecret) {
            switch (mode) {
                case ONLINE -> MojangAuth.init();
                case VELOCITY -> {
                    if (!Objects.equals(velocitySecret, "")) {
                        VelocityProxy.enable(velocitySecret);
                    }
                }
            }
        }

        /**
         * Sets the logback logging level
         * @param level The level to set to, INFO by default
         */
        private static void setLoggingLevel(Level level) {
            // https://stackoverflow.com/a/9787965/13247146
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(level);
        }


        public EndercubeServer startServer() {
            // Init config
            this.initGlobalConfig();

            // Make logging level configurable
            Level logLevel = Level.toLevel(
                    globalConfigUtils.getOrSetDefault(
                            globalConfig.node("logLevel"),
                            "INFO"
                    )
            );

            setLoggingLevel(logLevel);
            logger.trace("Log level is: " + logLevel.levelStr);

            // Start the server
            this.createServer();

            return new EndercubeServer(this);
        }
    }


}
