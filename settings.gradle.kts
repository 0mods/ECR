pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }

    val kotlinVersion = providers.gradleProperty("libs.kotlin").get()
    val architecturyLoom = providers.gradleProperty("plugins.architectury_loom").get()
    val modPublishPlugin = providers.gradleProperty("plugins.mod_publish_plugin").get()

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("dev.architectury.loom-no-remap") version architecturyLoom
        id("me.modmuss50.mod-publish-plugin") version modPublishPlugin
        id("team.0mods.yaml2json") version "1.0.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val archivesName = providers.gradleProperty("archives.name").get()
rootProject.name = archivesName

include(
    "api",
    "bootstrap-fabric",
    "bootstrap-neoforge",
    "runtime"
)
