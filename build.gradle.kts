plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.endercube"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")

    // Adventure dev builds
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {

    // Minestom
    implementation("net.minestom:minestom-snapshots:1_21_5-2398778b46")

    // Polar
    implementation("dev.hollowcube:polar:1.14.0")

    // GoldenStack/window
    implementation("net.goldenstack:window:1.1")

    // Kyori stuff (Adventure)
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    implementation("net.kyori:adventure-text-serializer-ansi:4.21.0")

    // Configuration API
    implementation("org.spongepowered:configurate-hocon:4.2.0")

    // Jedis (Redis lib)
    implementation("redis.clients:jedis:6.0.0")

    // Discord libs
    implementation("club.minnced:discord-webhooks:0.8.4")
    implementation("net.dv8tion:JDA:5.5.1") {
        // Optionally disable audio natives to reduce jar size by excluding `opus-java`
        exclude(module = "opus-java")
    }

    // Logger
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // Apache Commons
    implementation("org.apache.commons:commons-collections4:4.5.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Minestom has a minimum Java version of 21
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.endercube.Main" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}