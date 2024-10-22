val modId: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val modVersion: String by project
val imguiVersion: String by project

val authData = if (project.file("auth.data").exists()) project.file("auth.data").readText().trim() else ""

plugins {
    java
    idea
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("me.fallenbreath.yamlang") version "1.4.0"
}

java.withSourcesJar()

sourceSets {
    create("api") {
        kotlin.srcDirs("src/api/java", "src/api/kotlin")
        java.srcDirs("src/api/java")
        compileClasspath += main.get().compileClasspath
    }

    main {
        compileClasspath += sourceSets["api"].output
        runtimeClasspath += sourceSets["api"].output
        resources.srcDir(file("src/generated"))
    }
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
            if (authData.isNotEmpty()) {
                val lines = authData.lines()
                if (lines.isNotEmpty()) {
                    println("UUID: ${lines[0]}")
                    println("USERNAME: ${lines[1]}")
                    programArgs("--uuid", lines[0], "--username", lines[1], /*"-XX:+AllowEnhancedClassRedefinition"*/)
                }
            }

            mods {
                create(modId) {
                    sourceSet(sourceSets["api"])
                    sourceSet(sourceSets.main.get())
                }
            }
        }

        named("server") {
            server()
            mods {
                create(modId) {
                    sourceSet(sourceSets["api"])
                    sourceSet(sourceSets.main.get())
                }
            }
        }

        create("data") {
            data()
            programArgs("--all", "--mod", modId)
            programArgs("--output", file("src/generated").absolutePath)
        }
    }
}

version = "$minecraftVersion-$modVersion"

base {
    archivesName = "archivesName".fromProperties
}

repositories {
    mavenCentral()
    maven("https://maven.0mods.team/releases") // Kotlin Extras
    maven("https://maven.minecraftforge.net/") // MinecraftForge
    maven("https://maven.architectury.dev/") // Architectury API
    maven("https://maven.fabricmc.net/") // Loom
    maven("https://maven.parchmentmc.org") // Mappings
    maven("https://maven.blamejared.com/") // CT
    maven("https://modmaven.dev") // JEI
    maven("https://maven.tterrag.com/") // CTM
    maven("https://repo.spongepowered.org/repository/maven-public/") // Mixins
    maven("https://maven.saps.dev/releases") // Kubejs
    maven("https://api.modrinth.com/maven") // Modrinth maven for some mods
    maven("https://maven.terraformersmc.com/") // Mixin Extras
    flatDir { dir("libs") }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${"parchmentVersion".fromProperties}@zip")
    })

    compileOnly("org.spongepowered:mixin:0.8")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")

    forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    // required library
    modImplementation("ru.hollowhorizon:HollowCore-forge-$minecraftVersion:${"hc_version".fromProperties}")

    // Include libs
    //ModInclude
    minecraftRuntimeLibraries(include("team.chisel.ctm:CTM:${minecraftVersion}-${"ctm_version".fromProperties}")) {}
    implementation(include("io.github.llamalad7:mixinextras-forge:0.4.1")) {}

    // kotlin runtime & compile
    implementation(minecraftRuntimeLibraries(kotlin("stdlib", "2.0.10"))) {}
    implementation(minecraftRuntimeLibraries("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")) {}
    implementation(minecraftRuntimeLibraries("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:+")) {}
    implementation(minecraftRuntimeLibraries("org.jetbrains.kotlinx:kotlinx-serialization-core:+")) {}
    implementation(minecraftRuntimeLibraries("org.jetbrains.kotlinx:kotlinx-serialization-json:+")) {}

    modImplementation("mezz.jei:jei-${minecraftVersion}-forge:${"jei_version".fromProperties}")
    modImplementation("com.blamejared.crafttweaker:CraftTweaker-forge-1.19.2:${"ct_version".fromProperties}")
    modImplementation("maven.modrinth:jade:${"jade_version".fromProperties}")
    modImplementation("maven.modrinth:mystical-agriculture:${"ma_version".fromProperties}")
    modCompileOnly("dev.latvian.mods:kubejs-forge:${"kubejs_version".fromProperties}")

    // Runtime libs for test
    modRuntimeOnly("maven.modrinth:cucumber:${"cucumber_version".fromProperties}")
    prepareHCDeps()

    // Annotation processors
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("com.blamejared.crafttweaker:Crafttweaker_Annotation_Processors:${"ct_annot_version".fromProperties}")
}

tasks {
    jar {
        from(sourceSets["api"].output)
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
            "modLicense" to "modLicense".fromProperties, "hcVersionRange" to "[${"hc_version".fromProperties},)"
        )

        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta", "*.mixins.json")) {
            expand(replacement)
        }

        inputs.properties(replacement)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 17
    }

    val jt = task<Jar>("apiJar") {
        archiveClassifier = "api"
        from(sourceSets["api"].output)
    }

    val sourcesAPI = task<Jar>("apiSources") {
        archiveClassifier = "api-sources"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets["api"].allSource)
        dependsOn("apiJar")
    }

    named("sourcesJar").get().dependsOn("apiSources")

    artifacts {
        archives(jt)
        archives(sourcesAPI)
    }
}

kotlin {
    jvmToolchain(17)
}

yamlang {
    targetSourceSets = listOf(sourceSets.main.get())
    inputDir = "assets/$modId/lang"
}

val String.fromProperties
    get() = project.properties[this].toString()

fun DependencyHandlerScope.prepareHCDeps() {
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-app:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-binding:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-lwjgl3:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-binding-natives:$imguiVersion")) {}

    minecraftRuntimeLibraries("com.akuleshov7:ktoml-core-jvm:0.5.1")
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-app:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-binding:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-lwjgl3:$imguiVersion")) {}
    minecraftRuntimeLibraries(compileOnly("team.0mods:imgui-binding-natives:$imguiVersion")) {}
    minecraftRuntimeLibraries("com.tianscar.imageio:imageio-apng:1.0.1")
    minecraftRuntimeLibraries("org.joml:joml:1.10.8")

    minecraftRuntimeLibraries("org.jetbrains.kotlin:kotlin-reflect:2.0.0") { exclude("org.jetbrains.kotlin") }
}
