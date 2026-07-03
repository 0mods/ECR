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
    archivesName = "${rootProject.base.archivesName.get()}-bootstrap-fabric"
}

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation(libs.bundles.fabricmc)
    implementation(libs.fabric.language.kotlin)

    implementation(project(":runtime"))
    implementation(project(":mixins-fabric"))
    implementation(project(":resource"))

    compileOnly(libs.bundles.kotlinx.serialization)
    compileOnly(libs.bundles.kotlinx.coroutines)
}
