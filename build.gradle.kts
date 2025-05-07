val modId: String by project
val modName: String by project
val modVersion: String by project
val kotlinVersion: String by project
val license = "modLicense".fromProperties

plugins {
    java
    idea
    `maven-publish`
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("me.fallenbreath.yamlang")
    kotlin("jvm")
    kotlin("plugin.serialization")
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

val container = ModContainer(
    minecraftVersion = stonecutter.current.project.substringBeforeLast('-'),
    modPlatform = stonecutter.current.project.substringAfterLast('-'),
    modId = modId,
    modName = modName,
    license = license,
    modVersion = modVersion,
    credits = if (stonecutter.current.project.substringAfterLast('-') == "fabric") "modCredits".fromProperties.multilineJson else "modCredits".fromProperties,
    description = "modDesc".fromProperties,
    author = if (stonecutter.current.project.substringAfterLast('-') == "fabric") "modAuthors".fromProperties.multilineJson else "modAuthors".fromProperties
)

val String.fromProperties
    get() = project.properties[this].toString()

group = "modGroupId".fromProperties
version = modVersion
base.archivesName = "${"archivesName".fromProperties}-${container.modPlatform}-${container.minecraftVersion}"

setupEnvironment(
    container,
    defaultPlatformVersion,
    defaultMappingsVersion,
    kotlinVersion,
    libs.versions.kotlin.coroutines.get(),
    libs.versions.kotlin.serialization.get()
)

setupResources(sourceSets, container, mapOf("hcVersion" to libs.versions.hc.get()))

repositories {
    maven("https://maven.blamejared.com/") // CT
    maven("https://maven.latvian.dev/releases") // Kubejs
    maven("https://api.modrinth.com/maven") // Modrinth maven for some mods
    maven("https://maven.shedaniel.me/") // REI
    maven("https://maven.architectury.dev/")
}

dependencies {
    modImplementation(libs.hollowcore.mcReplace(container.minecraftVersion).replace("platform", container.modPlatform))
    prepareHCDeps()

    compileOnly(libs.mixinextras.common)
    install(libs.mixinextras.asProvider().replace("platform", container.modPlatform), true)

    modImplementation(libs.jei.mcReplace(container.minecraftVersion).replace("platform", container.modPlatform))
    modImplementation(libs.crafttweaker.asProvider().mcReplace(container.minecraftVersion).replace("platform", container.modPlatform))
    modImplementation(libs.jade.replace("platform", container.modPlatform))
    modImplementation(libs.kubejs.replace("platform", container.modPlatform))

    modCompileOnly(libs.rei.api.replace("platform", container.modPlatform))
    modCompileOnly(libs.rei.default.plugin.replace("platform", container.modPlatform))

    modRuntimeOnly(libs.rei.asProvider().replace("platform", container.modPlatform))

    platformModImplementation(Platform.FORGE, container.modPlatform, libs.mysticalAgriculture.replace("ma_version", "7.0.17"))
    platformModImplementation(Platform.FORGE, container.modPlatform, libs.cucumber.replace("cucumber_version", "7.0.13"))

    annotationProcessor(libs.mixinextras.common)
    annotationProcessor(libs.crafttweaker.annotationProcessor)
}

fun DependencyHandlerScope.prepareHCDeps() {
    minecraftRuntimeLibraries(libs.imageio.apng)
    minecraftRuntimeLibraries(libs.joml)
    minecraftRuntimeLibraries(libs.kotgl.matrix)

    compileOnlyMinecraft(libs.kool.editor)
    compileOnlyMinecraft(libs.kool.editor.model)
    compileOnlyMinecraft(libs.kool.core)
    compileOnlyMinecraft(libs.ktoml.core.jvm)

    platformImplementation(Platform.FABRIC, container.modPlatform, "io.github.classgraph:classgraph:4.8.173")
}

tasks {
    jar {
        from(sourceSets["api"].output)
    }

    task("removeOldJar") {
        val buildDirs = listOf(
            rootProject.layout.buildDirectory.file("libs").get().asFile,
            project.layout.buildDirectory.file("libs").get().asFile,
            rootProject.file("merged")
        )

        buildDirs.forEach {
            if (it.exists() && it.isDirectory) {
                it.listFiles()?.forEach { file -> file.delete() }
            }
        }
    }

    val apiJar = task<Jar>("apiJar") {
        archiveClassifier = "api"
        from(sourceSets["api"].output)
        dependsOn("removeOldJar")
    }

    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        archiveClassifier = "api-sources"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets["api"].allSource)
        dependsOn("apiJar")
    }

    named("sourcesJar").get().dependsOn(apiSourcesJar)

    artifacts {
        archives(apiJar)
        archives(apiSourcesJar)
    }
}
