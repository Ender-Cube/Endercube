plugins {
    `java-library`
}

dependencies {
    // Jedis (Redis lib)
    implementation("redis.clients:jedis:5.1.0")
}

group = "net.endercube"
version = "1.0-SNAPSHOT"