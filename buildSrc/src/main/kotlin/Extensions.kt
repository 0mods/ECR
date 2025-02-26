import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude

var isForgeLike = false

val defaultPlatformVersion = PlatformVersionContainer(
    fabric = mapOf(
        "1.19.2" to "0.77.0",
        "1.20.1" to "0.92.3",
        "1.21" to "0.102.0"
    ),
    forge = mapOf(
        "1.19.2" to "43.4.0",
        "1.20.1" to "47.3.0",
        "1.21" to "51.0.33"
    ),
    neoforge = mapOf(
        "1.21" to "21.0.167"
    )
)

val defaultMappingsVersion = MappingsContainer(
    mapOf(
        "1.19.2" to "2022.11.27",
        "1.20.1" to "2023.09.03",
        "1.21" to "2024.07.28"
    )
)

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

val String.multilineJson: String
    get() = buildString {
        if (this@multilineJson.isEmpty() || this@multilineJson == "null") append("")
        if (!this@multilineJson.contains(",")) append(this@multilineJson)

        val cred = this@multilineJson.split(',')
        cred.forEachIndexed { i, s, ->
            val fixed = s.removePrefix(" ")

            append('"').append(fixed).append('"')

            if (cred.size - 1 != i) append(",")
        }
    }
fun Provider<MinimalExternalModuleDependency>.mcReplace(minecraftVersion: String) =
    this.replace("mc_version", minecraftVersion)

fun Provider<MinimalExternalModuleDependency>.replace(original: String, new: String): String {
    val group = this.get().module.group
    val name = this.get().module.name
    val version = this.get().version
    return "${group}:$name:$version".replace(original, new)
}

fun DependencyHandlerScope.platformImplementation(platform: Platform, modPlatform: String, dependency: String) {
    if (platform.platformName == modPlatform)
        "implementation"(dependency)
}

fun DependencyHandlerScope.platformModImplementation(platform: Platform, modPlatform: String, dependency: String) {
    if (platform.platformName == modPlatform)
        this.modImplementation(dependency)
}

fun DependencyHandlerScope.compileOnlyMinecraft(dependency: Any) {
    "compileOnly"(dependency)
    "minecraftRuntimeLibraries"(dependency)
}

fun DependencyHandlerScope.modImplementation(dependency: String) = "modImplementation"(dependency)

fun DependencyHandlerScope.minecraft(version: String) = "minecraft"("com.mojang:minecraft:$version")

fun DependencyHandlerScope.install(path: String, includeInJar: Boolean = false, isMod: Boolean = false) {
    val dependency = if (isMod) modImplementation(path) else "implementation"(path) {
        exclude("org.jetbrains.kotlin")
        exclude("org.ow2.asm")
        exclude("net.sourceforge.jaad.aac")
        exclude("org.slf4j")
        exclude("commons-logging")
    }

    dependency.takeIf { isForgeLike && !isMod }?.let { "forgeRuntimeLibrary"(it) }
    if (includeInJar) dependency?.let { "include"(it) }
}

@Suppress("UnstableApiUsage")
fun LoomGradleExtensionAPI.setupMappings(version: String, mappings: MappingsContainer): Dependency = layered {
    officialMojangMappings()
    val mappingsVer = mappings.version[version] ?: throw IllegalStateException("Unknown mappings for version $version!")
    parchment("org.parchmentmc.data:parchment-$version:$mappingsVer@zip")
}

fun DependencyHandlerScope.setupLoader(loom: LoomGradleExtensionAPI, loader: String, version: String, mappings: MappingsContainer, platformVersion: PlatformVersionContainer) {
    minecraft(version)
    "mappings"(loom.setupMappings(version, mappings))

    val except = IllegalStateException("Unsupported $loader version $version!")

    when (loader) {
        "fabric" -> {
            modImplementation("net.fabricmc:fabric-loader:${platformVersion.fabricLoader}")
            val loaderVersion = platformVersion.fabric[version] ?: throw except
            modImplementation("net.fabricmc.fabric-api:fabric-api:$loaderVersion+$version")
        }
        "forge" -> {
            val loaderVersion = platformVersion.forge[version] ?: throw except
            "forge"("net.minecraftforge:forge:$version-$loaderVersion")
        }
        "neoforge" -> {
            val loaderVersion = platformVersion.neoforge[version] ?: throw except
            "neoForge"("net.neoforged:neoforge:$loaderVersion")
        }
    }
}
