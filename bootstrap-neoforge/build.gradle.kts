plugins {
    id("dev.architectury.loom-no-remap")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val minecraftVersion = rootProject.providers.gradleProperty("libs.minecraft").get()
val modPlatforms = rootProject.providers
    .gradleProperty("mod.platforms")
    .get()
    .split(',')
    .map(String::trim)
    .toTypedArray()

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
    archivesName = "${rootProject.base.archivesName.get()}-bootstrap-neoforge"
}


repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    neoForge(libs.neoforge)

    implementation(project(":api"))
    implementation(project(":runtime"))
    implementation(project(":mixins-neoforge"))
    implementation(project(":resource"))

    compileOnly(libs.bundles.kotlinx.serialization)
    compileOnly(libs.bundles.kotlinx.coroutines)
}
