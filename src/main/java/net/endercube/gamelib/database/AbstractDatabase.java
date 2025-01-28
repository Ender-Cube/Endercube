package net.endercube.gamelib.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

public abstract class AbstractDatabase {

    protected final Logger logger;
    protected final JedisPooled jedis;
    protected final String nameSpace;

    /**
     * An Endercube database
     *
     * @param jedis     A {@code JedisPooled} to get jedis instances from
     * @param nameSpace The prefix for all keys, does not need a colon on the end
     */
    public AbstractDatabase(JedisPooled jedis, String nameSpace) {
        this.jedis = jedis;
        this.nameSpace = nameSpace + ":";
        this.logger = LoggerFactory.getLogger(AbstractDatabase.class);
    }
}
