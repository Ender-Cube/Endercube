plugins {
    java

    // ShadowJar (https://github.com/johnrengelman/shadow/releases)
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.endercube"
version = "1.0.0"

dependencies {
    implementation(project(":common"))
    implementation(project(":discord"))
    implementation(project(":hub"))
    implementation(project(":parkour"))
    implementation(project(":spleef"))

    // Jedis (Redis lib)
    implementation("redis.clients:jedis:5.1.1")
}

// Global stuff
allprojects {
    apply(plugin = "java")

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
        implementation("net.minestom:minestom-snapshots:f1d5940855")

        // Configuration API
        implementation("org.spongepowered:configurate-hocon:4.1.2")

        // Kyori stuff (Adventure)
        implementation("net.kyori:adventure-text-serializer-plain:4.16.0")
        implementation("net.kyori:adventure-text-minimessage:4.16.0")
        implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")

        // Logger
        implementation("ch.qos.logback:logback-classic:1.5.6")

        // Apache Commons Lang
        implementation("org.apache.commons:commons-lang3:3.14.0")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.endercube.endercube.Main"
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