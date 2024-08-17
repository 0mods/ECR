import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val modId: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val modVersion: String by project
val shadowLibrary: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

plugins {
    java
    idea
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java.withSourcesJar()

sourceSets {
    main.get().resources.srcDir(file("src/generated"))
}

loom {
    silentMojangMappingsLicense()

    val awFile = project.file("src/main/resources/$modId.accesswidener")
    if (awFile.exists()) accessWidenerPath = awFile

    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        mixinConfigs("$modId.mixins.json")
    }

    runs {
        named("client") {
            client()
            programArgs(/*"--uuid", "--accessToken", "--userType",*/ "--username", "AlgorithmLX")
        }

        create("data") {
            data()
            programArgs("--all", "--mod", modId)
            programArgs("--output", file("src/generated").absolutePath)
        }
    }
}

version = modVersion

base {
    archivesName = "archivesName".fromProperties
}

repositories {
    mavenCentral()
    maven("https://maven.0mods.team/releases") // Kotlin Extras
    maven("https://maven.minecraftforge.net/") // MinecraftForge
    maven("https://maven.architectury.dev/") // Architectury API
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org") // Mappings
    maven("https://maven.blamejared.com/") // CT
    maven("https://modmaven.dev") // JEI
    maven("https://maven.tterrag.com/") // CTM
    maven("https://repo.spongepowered.org/repository/maven-public/") // Mixins
    maven("https://maven.saps.dev/releases") // Kubejs
    maven("https://api.modrinth.com/maven") // Modrinth maven for some mods
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${"parchmentVersion".fromProperties}@zip")
    })

    compileOnly("org.spongepowered:mixin:0.8")

    forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    fun shadow(dep: Any) {
        include(dep)
        minecraftClientRuntimeLibraries(dep)
    }

    shadowLibrary("team.0mods:KotlinExtras:1.4-noreflect")

    shadow("team.chisel.ctm:CTM:${minecraftVersion}-${"ctm_version".fromProperties}")

    implementation(kotlin("stdlib", "2.0.10")); minecraftRuntimeLibraries(kotlin("stdlib", "2.0.10"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+") { minecraftRuntimeLibraries(this) }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:+") { minecraftRuntimeLibraries(this) }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:+") { minecraftRuntimeLibraries(this) }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:+") { minecraftRuntimeLibraries(this) }

    modApi("mezz.jei:jei-${minecraftVersion}-forge:${"jei_version".fromProperties}")
    modApi("com.blamejared.crafttweaker:CraftTweaker-forge-1.19.2:${"ct_version".fromProperties}")
//    modApi("dev.latvian.mods:kubejs-forge:${"kubejs_version".fromProperties}")
    modApi("maven.modrinth:jade:${"jade_version".fromProperties}")
    modApi("maven.modrinth:mystical-agriculture:${"ma_version".fromProperties}")

//    modRuntimeOnly("dev.latvian.mods:rhino-forge:${"rhino_version".fromProperties}") // KJS lib
//    modRuntimeOnly("dev.architectury:architectury-forge:${"architectury_version".fromProperties}") // KJS lib
    modRuntimeOnly("maven.modrinth:cucumber:${"cucumber_version".fromProperties}")

    minecraftRuntimeLibraries("io.github.llamalad7:mixinextras-common:0.4.0")

    annotationProcessor("com.blamejared.crafttweaker:Crafttweaker_Annotation_Processors:${"ct_annot_version".fromProperties}")
}

tasks {
    shadowJar {
        configurations = listOf(shadowLibrary)
        archiveClassifier = "dev-shadow"

        val relocateLibs = listOf(
            "org.jetbrains", "com.typesafe", "kotlinx",
            "kotlin", "okio", "org.intellij", "_COROUTINE"
        )

        relocateLibs.forEach {
            relocate(it, "ecr_libs.$it")
        }
    }

    compileKotlin {
        useDaemonFallbackStrategy = false
        compilerOptions.freeCompilerArgs.add("-Xjvm-default=all")
    }

    processResources {
        from(project.sourceSets.main.get().resources)

        val replacement = mapOf(
            "modId" to modId, "modVersion" to modVersion, "modName" to "modName".fromProperties,
            "modCredits" to "modCredits".fromProperties, "modAuthors" to "modAuthors".fromProperties,
            "modDesc" to "modDesc".fromProperties, "forgeVersionRange" to "forgeVersionRange".fromProperties,
            "minecraftVersionRange" to "minecraftVersionRange".fromProperties, "loaderVersionRange" to "loaderVersionRange".fromProperties,
            "modLicense" to "modLicense".fromProperties
        )

        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta", "*.mixins.json")) {
            expand(replacement)
        }

        inputs.properties(replacement)
    }

    remapJar {
        inputFile = shadowJar.get().archiveFile
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 17
    }
}

kotlin {
    jvmToolchain(17)
}

val String.fromProperties
    get() = project.properties[this].toString()
