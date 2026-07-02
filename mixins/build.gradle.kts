plugins {
    id("dev.architectury.loom-no-remap")
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
    implementation(project(":runtime"))
}
