group = "net.endercube"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))

    // Jedis (Redis lib)
    implementation("redis.clients:jedis:5.1.0")

    // Polar
    implementation("dev.hollowcube:polar:1.7.2")
}