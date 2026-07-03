plugins {
    id("dev.architectury.loom-no-remap")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
    archivesName = "${rootProject.base.archivesName.get()}-runtime"
}

val minecraftVersion = rootProject.providers.gradleProperty("libs.minecraft").get()
val modPlatforms = rootProject.providers
    .gradleProperty("mod.platforms")
    .get()
    .split(',')
    .map(String::trim)
    .toTypedArray()

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    implementation(project(":api"))

    compileOnly(libs.bundles.kotlinx.serialization)
    compileOnly(libs.bundles.kotlinx.coroutines)
}
