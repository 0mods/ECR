import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    base
    idea
    id("dev.architectury.loom-no-remap") apply false
    id("earth.terrarium.cloche") apply false
    id("me.modmuss50.mod-publish-plugin")
    id("team.0mods.yaml2json") apply false
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
}

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
}

subprojects {
    plugins.apply("idea")

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_24.toString()
        targetCompatibility = JavaVersion.VERSION_24.toString()
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_24)
    }

    idea {
        module {
            inheritOutputDirs = false
            outputDir = layout.buildDirectory.dir("idea/classes/main").get().asFile
            testOutputDir = layout.buildDirectory.dir("idea/classes/test").get().asFile
        }
    }
}

tasks.assemble {
    dependsOn(":runtime:universalJar")
}
