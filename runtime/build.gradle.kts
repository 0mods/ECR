import groovy.json.JsonOutput
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("earth.terrarium.cloche")
    id("team.0mods.yaml2json")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
    archivesName = rootProject.base.archivesName.get()
}

val mcVersion = rootProject.providers.gradleProperty("libs.minecraft").get()
val modIdValue = providers.gradleProperty("mod.id").get()
val modGroupValue = providers.gradleProperty("mod.group").get()
val modNameValue = providers.gradleProperty("mod.name").get()
val modLicenseValue = providers.gradleProperty("mod.license").get()
val kotlinVersion = providers.gradleProperty("libs.kotlin").get()
val kotlinRuntimeVersion = providers.gradleProperty("libs.kotlin_runtime").get()
val kotlinSerializationVersion = libs.versions.kotlin.serialization.get()
val kotlinCoroutinesVersion = libs.versions.kotlin.coroutines.get()
val fabricApiBaseVersion = libs.versions.fabric.api.get().substringBefore('+')
val klfVersion = project.property("mod.depend.klf_version").toString()
val klfLoaderVersion = project.property("mod.depend.klf_loader_version").toString()

repositories {
    cloche {
        mavenNeoforged()
        mavenNeoforgedMeta()
        mavenFabric()
        librariesMinecraft()
        main()
    }

    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.nyon.dev/releases")
}

dependencies {
    implementation(project(":api"))
    testImplementation(kotlin("test"))
}

cloche {
    minecraftVersion.set(mcVersion)

    metadata {
        modId.set(modIdValue)
        name.set(providers.gradleProperty("mod.name"))
        description.set(providers.gradleProperty("mod.description"))
        license.set(providers.gradleProperty("mod.license"))
        icon.set("ecr.png")
        author("AlgorithmLX")
    }

    val commonTarget = common {
        dependencies {
            compileOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion")
            compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
            compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
            compileOnly("org.spongepowered:mixin:0.8.7")
        }
    }

    fabric {
        dependsOn(commonTarget)
        loaderVersion.set(libs.versions.fabric.loader)
        includedClient()

        dependencies {
            implementation(project(":api"))
            fabricApi(fabricApiBaseVersion)
            modImplementation(libs.fabric.language.kotlin)
        }

        runs {
            client()
        }
    }

    neoforge {
        dependsOn(commonTarget)
        loaderVersion.set(libs.versions.neoforge)

        dependencies {
            implementation(project(":api"))
            modImplementation("dev.nyon:KotlinLangForge:$klfVersion-k$kotlinRuntimeVersion-$klfLoaderVersion+neoforge")
        }

        metadata {
            modLoader.set("klf")
            loaderVersion(klfVersion)
        }

        runs {
            client()
        }
    }
}

yaml2json {
    inputDir.set(layout.projectDirectory.dir("src/common/main/resources"))
    flatJsonMarker.set($$"#$json_flat")
}

val modMetadata = mapOf(
    "modId" to modIdValue,
    "modVersion" to providers.gradleProperty("mod.version").get(),
    "modName" to providers.gradleProperty("mod.name").get(),
    "modDesc" to providers.gradleProperty("mod.description").get(),
    "modAuthors" to "AlgorithmLX",
    "modLicense" to providers.gradleProperty("mod.license").get(),
    "fabricLoaderVersion" to libs.versions.fabric.loader.get(),
    "neoforgeVersion" to libs.versions.neoforge.get(),
    "minecraftVersion" to mcVersion,
    "klfVersion" to klfVersion
)

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    inputs.properties(modMetadata)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand(modMetadata)
    }
}

val apiJar = project(":api").tasks.named<Jar>("jar")
val fabricDistributionJar = tasks.named<Jar>("fabricIncludeJar")
val neoforgeDistributionJar = tasks.named<Jar>("neoforgeIncludeJar")

listOf("fabricJar", "neoforgeJar").forEach { taskName ->
    tasks.named<Jar>(taskName) {
        dependsOn(apiJar)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(apiJar.map { zipTree(it.archiveFile) })
    }
}

val universalMetadataDirectory = layout.buildDirectory.dir("generated/universalMetadata")
val generateUniversalMetadata = tasks.register("generateUniversalMetadata") {
    val fabricJarName = fabricDistributionJar.flatMap { it.archiveFileName }
    val neoforgeJarName = neoforgeDistributionJar.flatMap { it.archiveFileName }
    val wrapperVersion = project.version.toString()

    inputs.property("fabricJarName", fabricJarName)
    inputs.property("neoforgeJarName", neoforgeJarName)
    inputs.property("wrapperVersion", wrapperVersion)
    inputs.property("modId", modIdValue)
    inputs.property("modGroup", modGroupValue)
    inputs.property("modName", modNameValue)
    inputs.property("modLicense", modLicenseValue)
    outputs.dir(universalMetadataDirectory)

    doLast {
        val outputDirectory = universalMetadataDirectory.get().asFile
        val jarJarDirectory = outputDirectory.resolve("META-INF/jarjar").apply { mkdirs() }
        val fabricJarPath = "META-INF/jarjar/${fabricJarName.get()}"
        val neoforgeJarPath = "META-INF/jarjar/${neoforgeJarName.get()}"

        outputDirectory.resolve("fabric.mod.json").writeText(
            JsonOutput.prettyPrint(
                JsonOutput.toJson(
                    mapOf(
                        "schemaVersion" to 1,
                        "id" to "${modIdValue}_loader",
                        "version" to wrapperVersion,
                        "name" to "$modNameValue Wrapper",
                        "jars" to listOf(mapOf("file" to fabricJarPath))
                    )
                )
            )
        )

        jarJarDirectory.resolve("metadata.json").writeText(
            JsonOutput.prettyPrint(
                JsonOutput.toJson(
                    mapOf(
                        "jars" to listOf(
                            mapOf(
                                "identifier" to mapOf(
                                    "group" to modGroupValue,
                                    "artifact" to "${modIdValue}-neoforge"
                                ),
                                "version" to mapOf(
                                    "range" to "[$wrapperVersion,)",
                                    "artifactVersion" to wrapperVersion
                                ),
                                "path" to neoforgeJarPath,
                                "isObfuscated" to false
                            )
                        )
                    )
                )
            )
        )

        outputDirectory.resolve("META-INF/neoforge.mods.toml").writeText(
            """
            modLoader="javafml"
            loaderVersion="[1,)"
            license="$modLicenseValue"

            [[mods]]
            modId="${modIdValue}_loader"
            version="$wrapperVersion"
            displayName="$modNameValue Wrapper"
            description='''Universal loader wrapper for $modNameValue.'''
            """.trimIndent()
        )
    }
}

val universalJar = tasks.register<Jar>("universalJar") {
    group = "build"
    description = "Assembles the universal Fabric/NeoForge jar-in-jar archive."
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(generateUniversalMetadata, fabricDistributionJar, neoforgeDistributionJar)
    from(universalMetadataDirectory)
    from(fabricDistributionJar.flatMap { it.archiveFile }) {
        into("META-INF/jarjar")
    }
    from(neoforgeDistributionJar.flatMap { it.archiveFile }) {
        into("META-INF/jarjar")
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named("assemble") {
    dependsOn(universalJar)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_24)

        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xexpect-actual-classes",
            "-Xannotation-target-all",
            "-Xnullability-annotations=@org.jspecify.annotations:warn"
        )
    }
}
