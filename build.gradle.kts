plugins {
    java

    // ShadowJar (https://github.com/johnrengelman/shadow/releases)
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.endercube"
version = "1.0-SNAPSHOT"



// Global stuff
allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        // Minestom
        implementation("dev.hollowcube:minestom-ce:010fe985bb")

        // Configuration API
        implementation("org.spongepowered:configurate-hocon:4.1.2")

        // Kyori stuff (Adventure)
        implementation("net.kyori:adventure-text-minimessage:4.13.1")
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