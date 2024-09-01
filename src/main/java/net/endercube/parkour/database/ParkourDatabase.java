package net.endercube.parkour.database;


import net.endercube.gamelib.database.AbstractDatabase;
import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.enums.GrindMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.UUID;

/**
 * An abstracted interface to Redis for parkour
 */
public class ParkourDatabase extends AbstractDatabase {


    /**
     * A parkour database
     *
     * @param jedis     A {@code JedisPooled} to get jedis instances from
     * @param nameSpace The prefix for all keys, does not need a colon on the end
     */
    public ParkourDatabase(JedisPooled jedis, String nameSpace) {
        super(jedis, nameSpace);
    }

    /**
     * Adds a time to the database
     *
     * @param player The player the time belongs to
     * @param course The {@link String} id of the course to look up
     * @param time   The time in milliseconds
     * @return true if new pb, false if not new PB
     */
    public boolean addTime(Player player, String course, Long time) {
        String key = nameSpace + course + ":times";
        String uuid = player.getUuid().toString();

        Double oldTime = jedis.zscore(key, uuid);

        if (oldTime != null) {
            if (oldTime <= time) {
                logger.trace("Did not add new time for " + player.getUsername() + " because their current time of " + oldTime + " Is less than than the new time of " + time);
                return false;
            }
        }

        jedis.zadd(key, time, uuid);
        logger.debug("Added run to the database with:");
        logger.debug("    player: " + player.getUsername());
        logger.debug("    course: " + course);
        logger.debug("    time: " + time);
        return true;
    }

    /**
     * Removes a player's times from the leaderboard
     *
     * @param player The player whose times to remove
     * @param course The course to remove times from
     */
    public void removeTime(Player player, String course) {
        jedis.zrem(nameSpace + course + ":times", player.getUuid().toString());
        logger.debug("Removed " + player.getUsername() + "'s times for " + course);
    }

    /**
     * Removes a player's times from the leaderboard
     *
     * @param playerUUID The player's UUID whose times are to be removed
     * @param course     The course to remove times from
     */
    public void removeTime(UUID playerUUID, String course) {
        jedis.zrem(nameSpace + course + ":times", playerUUID.toString());
        logger.debug("Removed " + playerUUID + "'s times for " + course);
    }

    /**
     * @param course   The course to get a leaderboard for
     * @param maxRange an {@code int} for the number of results to return
     * @return A {@code List<Tuple>} containing players and their times
     */
    @Nullable
    public List<Tuple> getLeaderboard(String course, int maxRange) {
        return getLeaderboard(course, 0, maxRange);
    }

    /**
     * @param course   The course to get a leaderboard for
     * @param minRange an {@code int} for the nth minimum result
     * @param maxRange an {@code int} for the nth maximum result
     * @return A {@code List<Tuple>} containing players and their times
     */
    @Nullable
    public List<Tuple> getLeaderboard(String course, int minRange, int maxRange) {
        logger.debug("Getting leaderboard for " + course + " in range " + minRange + " to " + maxRange);
        List<Tuple> databaseTuple = jedis.zrangeWithScores(nameSpace + course + ":times", minRange, maxRange);

        return databaseTuple.stream()
                .map((tuple ->
                                new Tuple(
                                        MojangUtils.fromUuid(tuple.getElement()).get("name").getAsString(),
                                        tuple.getScore())
                        )
                )
                .toList();

    }

    /**
     * Set the grinding mode
     *
     * @param player    The player to set for
     * @param grindMode The grind mode to set
     */
    public void setGrindMode(EndercubePlayer player, @NotNull GrindMode grindMode) {
        jedis.set(nameSpace + "grindMode:" + player.getUuid(), grindMode.name());
    }

    /**
     * Get the player's specified grindMode. HUB if none has been set
     *
     * @param player the player to get for
     * @return The selected grindMode
     */
    @NotNull
    public GrindMode getGrindMode(EndercubePlayer player) {
        @Nullable String stringGrindMode = jedis.get(nameSpace + "grindMode:" + player.getUuid());
        if (stringGrindMode == null) {
            this.setGrindMode(player, GrindMode.HUB);
            return GrindMode.HUB;
        }
        return GrindMode.valueOf(stringGrindMode);
    }
}
