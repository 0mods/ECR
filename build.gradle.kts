val modId: String by project
val modVersion: String by project
val minecraftVersion = libs.versions.minecraft.get()
val forgeVersion = libs.versions.forge.get()
val hcVersion = libs.versions.hc.get()

val authData = if (project.file("auth.data").exists()) project.file("auth.data").readText().trim() else ""

plugins {
    java
    idea
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.yamlang)
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
    maven("https://jitpack.io")
    flatDir { dir("libs") }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${libs.versions.parchment.get()}@zip")
    })

    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)

    forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    // required library
    modImplementation(libs.hollowcore.forge.mcReplace)

    // Include libs
    //ModInclude
    equalDepend(libs.mixinextras.forge)

    // kotlin runtime & compile
    compileOnlyMinecraft(libs.kotlin.stdlib)

    compileOnlyMinecraft(libs.kotlin.coroutines)
    compileOnlyMinecraft(libs.kotlin.coroutines.jvm)
    compileOnlyMinecraft(libs.kotlin.serialization)
    compileOnlyMinecraft(libs.kotlin.serialization.json)

    modImplementation(libs.jei.mcReplace)
    modImplementation(libs.crafttweaker.forge.mcReplace)
    modImplementation(libs.jade.forge)
    modImplementation(libs.mysticalAgriculture)
    modCompileOnly(libs.kubejs.forge)

    // Runtime libs for test
    modRuntimeOnly(libs.cucumber)
    prepareHCDeps()

    // Annotation processors
    annotationProcessor(libs.mixinextras.common)
    annotationProcessor(libs.crafttweaker.annotationProcessor)
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
            "modDesc" to "modDesc".fromProperties, "forgeVersionRange" to forgeVersion.range,
            "minecraftVersionRange" to minecraftVersion.mcRange, "loaderVersionRange" to forgeVersion.range,
            "modLicense" to "modLicense".fromProperties, "hcVersionRange" to "[$hcVersion,)"
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
    minecraftRuntimeLibraries(libs.ktoml.core.jvm)
    compileOnlyMinecraft(libs.imgui.app)
    compileOnlyMinecraft(libs.imgui.binding)
    compileOnlyMinecraft(libs.imgui.lwjgl)
    compileOnlyMinecraft(libs.imgui.binding.natives)
    minecraftRuntimeLibraries(libs.imageio.apng)
    minecraftRuntimeLibraries(libs.joml)
    minecraftRuntimeLibraries(libs.kotgl.matrix)

    minecraftRuntimeLibraries(libs.kotlin.reflect) { exclude("org.jetbrains.kotlin") }
}

fun DependencyHandlerScope.compileOnlyMinecraft(dependency: Any) {
    compileOnly(dependency)
    minecraftRuntimeLibraries(dependency)
}

fun DependencyHandlerScope.equalDepend(dependency: Any) {
    implementation(dependency)
    include(dependency)
}

val String.range: String
    get() {
        val ver = this.split('.')[0]
        return "[$ver,)"
    }

val String.mcRange: String
    get() {
        val ver = this.split('.').last()
        val buildedVersion = "${this.split('.').first()}.${this.split('.')[1]}.${ver.toInt() + 1}"
        return "[$this,$buildedVersion)"
   }

val Provider<MinimalExternalModuleDependency>.mcReplace: String
    get() {
        val group = this.get().module.group
        val name = this.get().module.name
        val version = this.get().version
        return "${group}:${name.replace("mc_version", minecraftVersion)}:$version"
    }
