plugins {
    java

    // ShadowJar (https://github.com/johnrengelman/shadow/releases)
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.endercube"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":Common"))
    implementation(project(":Parkour"))
    implementation(project(":Hub"))

    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.7")
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
        implementation("dev.hollowcube:minestom-ce:010fe985bb")

        // Configuration API
        implementation("org.spongepowered:configurate-hocon:4.1.2")

        // Kyori stuff (Adventure)
        implementation("net.kyori:adventure-text-serializer-plain:4.13.1")
        implementation("net.kyori:adventure-text-minimessage:4.13.1")
        implementation("net.kyori:adventure-text-serializer-ansi:4.14.0-SNAPSHOT")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.endercube.Endercube.Main"
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