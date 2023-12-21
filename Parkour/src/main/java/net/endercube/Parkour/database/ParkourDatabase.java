package net.endercube.Parkour.database;


import net.endercube.Common.database.AbstractDatabase;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.List;

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
     */
    public void addTime(Player player, String course, Long time) {
        jedis.zadd(nameSpace + course + ":times", time, player.getUuid().toString());
        logger.debug("Added run to the database with:");
        logger.debug("    player: " + player.getUsername());
        logger.debug("    course: " + course);
        logger.debug("    time: " + time);
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
        return jedis.zrangeWithScores(nameSpace + course + ":times", minRange, maxRange);

    }
}
