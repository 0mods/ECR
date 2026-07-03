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

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
    archivesName = "${rootProject.base.archivesName.get()}-mixins"
}

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    implementation(project(":api"))
    implementation(project(":runtime"))

    implementation("org.spongepowered:mixin:0.8.7")
}
