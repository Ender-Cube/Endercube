package net.endercube.gamelib;

import ch.qos.logback.classic.Level;
import net.endercube.gamelib.config.ConfigFile;
import net.endercube.global.EndercubePlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The "abstract" endercubeServer that Main.java calls. Most of the main server's logic is in here
 */
public class EndercubeServer {

    private final ArrayList<EndercubeMinigame> minigames = new ArrayList<>();
    private final ConfigFile globalConfig;

    private static final Logger logger;


    // Initializes the logger, only on the first initialization of this class
    static {
        logger = LoggerFactory.getLogger(EndercubeServer.class);
    }

    private EndercubeServer(EndercubeServerBuilder builder) {
        this.globalConfig = builder.globalConfig;
    }

    /**
     * Add a minigame to the server
     *
     * @param minigame The minigame to add
     * @return The builder
     */
    public EndercubeServer addMinigame(EndercubeMinigame minigame) {
        minigame.setEndercubeServer(this);
        minigames.add(minigame);
        return this;
    }

    public @NotNull ConfigFile getGlobalConfig() {
        return globalConfig;
    }


    @Nullable
    public EndercubeMinigame getMinigameByName(@NotNull String name) {
        return minigames.stream()
                .filter(((endercubeMinigame) -> endercubeMinigame.getName().equals(name)))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    public JedisPooled createJedis() {
        String jedisURL = globalConfig.getOrSetDefault(globalConfig.getConfig().node("database", "redis", "url"), "localhost");
        int jedisPort = Integer.parseInt(globalConfig.getOrSetDefault(globalConfig.getConfig().node("database", "redis", "port"), "6379"));
        return new JedisPooled(jedisURL, jedisPort);
    }

    /**
     * A builder for EndercubeServer
     */
    public static class EndercubeServerBuilder {
        private final EventNode<Event> globalEvents;
        private ConfigFile globalConfig;
        private HashMap<Key, Supplier<BlockHandler>> blockHandlers = new HashMap<>();

        public EndercubeServerBuilder() {
            globalEvents = EventNode.all("globalListeners");
        }


        /**
         * Add a global event, will be called regardless of minigame
         *
         * @param listener The listener
         * @return The builder
         */
        public EndercubeServerBuilder addGlobalEvent(EventListener<?> listener) {
            globalEvents.addListener(listener);
            return this;
        }

        /**
         * Add a global event, will be called regardless of minigame
         *
         * @param eventType The type of event to listen for
         * @param listener  The listener
         * @return The builder
         */
        public <E extends Event> EndercubeServerBuilder addGlobalEvent(@NotNull Class<E> eventType, @NotNull Consumer<E> listener) {
            globalEvents.addListener(eventType, listener);
            return this;
        }

        /**
         * Add a block handler
         *
         * @param namespace       The namespace name for this block
         * @param handlerSupplier THe handler for this block
         * @return The builder
         */
        public EndercubeServerBuilder addBlockHandler(Key namespace, Supplier<BlockHandler> handlerSupplier) {
            // Add block handlers
            blockHandlers.put(namespace, handlerSupplier);
            return this;
        }

        private String getVelocitySecret() {
            Path path = Paths.get("./config/velocity.secret");

            try {
                return Files.readString(path);
            } catch (IOException e) {
                logger.error("Failed to read velocity secret from \"./config/velocity.secret\" and velocity is enabled in config. Aborting start");
                throw new RuntimeException(e);
            }
        }

        /**
         * Start Minestom and the like
         */
        public void createServer() {
            // Get config values
            int port = Integer.parseInt(globalConfig.getOrSetDefault(globalConfig.getConfig().node("connection", "port"), "25565"));

            // Set encryption
            EncryptionMode encryptionMode;
            try {
                encryptionMode = EncryptionMode.valueOf(
                        globalConfig.getOrSetDefault(
                                globalConfig.getConfig().node("connection", "mode"),
                                "online"
                        ).toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                logger.warn("Cannot read encryption mode from config, falling back to ONLINE");
                encryptionMode = EncryptionMode.ONLINE;
            }

            // Server Initialization
            MinecraftServer minecraftServer = MinecraftServer.init();

            // Add global events
            MinecraftServer.getGlobalEventHandler().addChild(globalEvents);

            // Init block handlers
            for (Map.Entry<Key, Supplier<BlockHandler>> entry : blockHandlers.entrySet()) {
                MinecraftServer.getBlockManager().registerHandler(entry.getKey(), entry.getValue());
                logger.debug("Added a block handler for " + entry.getKey());
            }

            initEncryption(encryptionMode);

            // Start server
            minecraftServer.start("0.0.0.0", port);
            logger.info("Started server on port " + port + " with " + encryptionMode + " encryption");

            // Set player provider
            MinecraftServer.getConnectionManager().setPlayerProvider(EndercubePlayer::new);
            logger.debug("Set player provider");
        }

        enum EncryptionMode {
            ONLINE,
            OFFLINE,
            VELOCITY
        }

        private void initEncryption(EncryptionMode mode) {
            switch (mode) {
                case ONLINE -> MojangAuth.init();
                case OFFLINE -> {
                }
                case VELOCITY -> {
                    String velocitySecret = getVelocitySecret();

                    if (!Objects.equals(velocitySecret, "")) {
                        VelocityProxy.enable(velocitySecret);
                        logger.debug("Velocity enabled: " + VelocityProxy.isEnabled());
                    } else {
                        logger.error("Velocity is enabled but no secret is specified. Stopping server");
                        MinecraftServer.stopCleanly();
                    }
                }
            }
        }

        /**
         * Sets the logback logging level
         *
         * @param level The level to set to, INFO by default
         */
        private static void setLoggingLevel(Level level) {
            // https://stackoverflow.com/a/9787965/13247146
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(level);
        }


        public EndercubeServer startServer() {
            // Init config
            globalConfig = new ConfigFile("globalConfig", "This file is for stuff that didn't fit anywhere else");

            // Create world directories
            if (!Files.exists(Paths.get("./config/worlds/"))) {
                logger.info("Creating configuration files");

                try {
                    Files.createDirectories(Paths.get("./config/worlds/"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Make logging level configurable
            Level logLevel = Level.toLevel(
                    globalConfig.getOrSetDefault(
                            globalConfig.getConfig().node("logLevel"),
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
