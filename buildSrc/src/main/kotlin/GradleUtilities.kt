
import dev.architectury.plugin.ArchitectPluginExtension
import dev.kikugie.stonecutter.build.StonecutterBuild
import me.fallenbreath.yamlang.YamlangExtension
import net.fabricmc.loom.extension.LoomGradleExtensionImpl
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val Any.asKtx
    get() = this.toString().replace("kotlin", "kotlinx")

fun Project.setupEnvironment(
    container: ModContainer,
    platformVersion: PlatformVersionContainer,
    mappingsVersion: MappingsContainer,
    kotlinVersion: String,
    kotlinCoroutinesVersion: String,
    kotlinSerializationVersion: String,
    runtimeNickName: String = "",
    includeKotlin: Boolean = false
) {
    container.apply {
        val loom = extensions["loom"] as LoomGradleExtensionImpl
        val architectury = extensions["architectury"] as ArchitectPluginExtension
        val stonecutter = extensions["stonecutter"] as StonecutterBuild
        val java = extensions["java"] as JavaPluginExtension
        val kotlin = extensions["kotlin"] as KotlinJvmProjectExtension
        val sourceSets = extensions["sourceSets"] as SourceSetContainer

        isForgeLike = modPlatform == "forge" || modPlatform == "neoforge"

        setupArchLoom(loom, this, this@setupEnvironment, sourceSets, runtimeNickName, architectury)
        setupStonecutter(stonecutter, loom, this, java, kotlin)

        repositories {
            mavenCentral()
            mavenLocal()

            maven("https://repo.spongepowered.org/repository/maven-public/")
            maven("https://maven.0mods.team/releases")
            maven("https://maven.parchmentmc.org")
            maven("https://maven.architectury.dev/")
            maven("https://maven.neoforged.net/releases")
            maven("https://maven.fabricmc.net/")

            flatDir {
                dirs(rootDir.resolve("libs"))
            }
        }

        dependencies {
            setupLoader(loom, modPlatform, minecraftVersion, mappingsVersion, platformVersion)
            "compileOnly"("org.spongepowered:mixin:0.8.7")

            install(kotlin("stdlib-jdk8", kotlinVersion).toString(), includeKotlin)
            install(kotlin("reflect", kotlinVersion).toString(), includeKotlin)
            install(kotlin("stdlib-jdk7", kotlinVersion).toString(), includeKotlin)
            install(kotlin("stdlib", kotlinVersion).toString(), includeKotlin)
            install(kotlin("serialization-core", kotlinSerializationVersion).asKtx, includeKotlin)
            install(kotlin("serialization-json", kotlinSerializationVersion).asKtx, includeKotlin)
            install(kotlin("coroutines-core", kotlinCoroutinesVersion).asKtx, includeKotlin)
        }
    }
}

private fun Project.setupStonecutter(
    stonecutter: StonecutterBuild,
    loom: LoomGradleExtensionImpl,
    modContainer: ModContainer,
    java: JavaPluginExtension,
    kotlin: KotlinJvmProjectExtension
) {
    afterEvaluate {
        stonecutter.apply {
            val platform = loom.platform.get().id()
            stonecutter.const("fabric", platform == "fabric")
            stonecutter.const("forge", platform == "forge")
            stonecutter.const("neoforge", platform == "neoforge")
        }
    }

    val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
        group = "build"
        from(tasks.named<Jar>("remapJar").get().archiveFile)
        into(rootProject.file("merged"))

        dependsOn("build")
    }

    if (stonecutter.current.isActive) {
        rootProject.tasks.register("buildActive") {
            group = "project"
            dependsOn(buildAndCollect)
        }

        rootProject.tasks.register("runActive") {
            group = "project"
            dependsOn(tasks.named("runClient"))
        }
    }

    stonecutter.apply {
        val j21 = eval(modContainer.minecraftVersion, ">=1.20.5")

        java.apply {
            withSourcesJar()
            sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
            targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17

            toolchain {
                languageVersion.set(JavaLanguageVersion.of(if (j21) 21 else 17))
            }
        }

        kotlin.apply {
            jvmToolchain(if (j21) 21 else 17)
        }
    }
}

fun Project.setupResources(
    sourceSets: SourceSetContainer,
    modContainer: ModContainer,
    additionalReplaces: Map<String, String> = mapOf()
) {
    val yamlang = extensions["yamlang"] as YamlangExtension

    tasks.named<ProcessResources>("processResources") {
        from(sourceSets["main"].resources)
        when (modContainer.modPlatform) {
            "forge" -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
            "neoforge" -> exclude("fabric.mod.json", "META-INF/mods.toml")
            "fabric" -> exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml")
        }

        val replacement = mutableMapOf(
            "modId" to modContainer.modId, "modVersion" to modContainer.modVersion, "modName" to modContainer.modName,
            "modCredits" to modContainer.credits, "modAuthors" to modContainer.author,
            "modDesc" to modContainer.description, "forgeVersionRange" to defaultPlatformVersion.forge[modContainer.minecraftVersion]?.range,
            "minecraftVersion" to modContainer.minecraftVersion,
            "minecraftVersionRange" to modContainer.minecraftVersion.mcRange, "loaderVersionRange" to defaultPlatformVersion.forge[modContainer.minecraftVersion]?.range,
            "modLicense" to modContainer.license
        )

        replacement.putAll(additionalReplaces)

        filesMatching(listOf("META-INF/*.toml", "fabric.mod.json", "pack.mcmeta", "*.mixins.json")) {
            expand(replacement)
        }

        inputs.properties(replacement)
    }

    yamlang.apply {
        targetSourceSets.set(mutableListOf(sourceSets["main"]))
        inputDir.set("assets/${modContainer.modId}/lang")
    }
}

private fun setupArchLoom(
    loom: LoomGradleExtensionImpl,
    modContainer: ModContainer,
    project: Project,
    sourceSets: SourceSetContainer,
    userName: String,
    architectury: ArchitectPluginExtension
) {
    loom.apply {
        silentMojangMappingsLicense()
        if (modContainer.modPlatform == "neoforge") generateSrgTiny = false
        val awFile = project.rootProject.file("src/main/resources/${modContainer.modId}.accesswidener")
        if (awFile.exists()) accessWidenerPath.set(awFile)

        mixin.useLegacyMixinAp.set(true)
        mixin.add(sourceSets["main"], "${modContainer.modId}.refmap.json")

        when (modContainer.modPlatform) {
            "forge" -> forge {
                convertAccessWideners.set(true)
                mixinConfig("${modContainer.modId}.mixins.json")
            }
            "neoforge" -> neoForge {}
        }

        runConfigs.all {
            if (userName.isNotEmpty()) {
                if (environment == "client") programArgs("--username=$userName")
            }
            property("sodium.checks.issue2561", "false")
            runDir("../../run")
        }
    }

    architectury.apply {
        minecraft = modContainer.minecraftVersion
        platformSetupLoomIde()
        if (modContainer.modPlatform == "neoforge") loom.generateSrgTiny = false
        common(modContainer.modPlatform)
        when (modContainer.modPlatform) {
            "fabric" -> fabric()
            "forge" -> forge()
            "neoforge" -> neoForge()
        }
    }
}
