plugins {
    java

    // ShadowJar (https://github.com/johnrengelman/shadow/releases)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.endercube"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    // Minestom
    implementation("net.minestom:minestom-snapshots:dba90a461b")

    // Polar
    implementation("dev.hollowcube:polar:1.11.3")

    // GoldenStack/window
    implementation("com.github.GoldenStack:window:a2feac67eb")


    // Kyori stuff (Adventure)
    implementation("net.kyori:adventure-text-serializer-plain:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")

    // Configuration API
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    // Jedis (Redis lib)
    implementation("redis.clients:jedis:5.2.0")

    // Discord lib
    implementation("club.minnced:discord-webhooks:0.8.4")

    // Logger
    implementation("ch.qos.logback:logback-classic:1.5.12")

    // Apache Commons Lang
    implementation("org.apache.commons:commons-lang3:3.17.0")


}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.endercube.Main"
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix
    }
}