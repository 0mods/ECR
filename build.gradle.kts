import com.google.gson.Gson

plugins {
    base
    idea
    id("com.gradleup.shadow") apply false
    id("me.modmuss50.mod-publish-plugin")
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
}

base {
    version = "${providers.gradleProperty("mod.version").get()}+mc${providers.gradleProperty("libs.minecraft").get()}"
}

subprojects {
    plugins.apply("idea")

    idea {
        module {
            inheritOutputDirs = false
            outputDir = layout.buildDirectory.dir("idea/classes/main").get().asFile
            testOutputDir = layout.buildDirectory.dir("idea/classes/test").get().asFile
        }
    }
}

val generatedMetaDir = layout.buildDirectory.dir("generated-meta")
val generateModMetadata = tasks.register("generateModMetadata") {
    outputs.dir(generatedMetaDir)

    val jarNameProviders = subprojects.associate { sub ->
        val provider = sub.tasks.named<Jar>("jar").flatMap { it.archiveFileName }
        inputs.property("${sub.name}JarName", provider)
        sub.name to provider
    }

    doLast {
        val baseDir = generatedMetaDir.get().asFile
        val gson = Gson()

        val fabricJars = mutableListOf<Map<String, String>>()
        val neoForgeJars = mutableListOf<Map<String, Any>>()

        subprojects.forEach { sub ->
            val jarName = jarNameProviders[sub.name]?.get() ?: return@forEach
            val path = "META-INF/jarjar/$jarName"

            if (sub.name.contains("fabric")) {
                fabricJars.add(mapOf("file" to path))
            } else if (sub.name.contains("neoforge")) {
                neoForgeJars.add(mapOf(
                    "identifier" to mapOf("group" to sub.group.toString(), "artifact" to sub.name),
                    "version" to mapOf("range" to "[${sub.version},)", "artifactVersion" to sub.version.toString()),
                    "path" to path,
                    "isObfuscated" to false
                ))
            } else {
                fabricJars.add(mapOf("file" to path))
                neoForgeJars.add(mapOf(
                    "identifier" to mapOf("group" to sub.group.toString(), "artifact" to sub.name),
                    "version" to mapOf("range" to "[${sub.version},)", "artifactVersion" to sub.version.toString()),
                    "path" to path,
                    "isObfuscated" to false
                ))
            }
        }

        val fabMeta = File(baseDir, "fabric.mod.json")
        fabMeta.parentFile.mkdirs()
        fabMeta.writeText(gson.toJson(mapOf(
            "schemaVersion" to 1,
            "id" to "${providers.gradleProperty("mod.id").get()}_loader",
            "version" to project.version.toString(),
            "name" to "${base.archivesName.get()} Wrapper",
            "jars" to fabricJars
        )))
    }
}

val buildUniversalFatJar = tasks.register<Jar>("buildUniversalFatJar") {
    destinationDirectory.set(layout.buildDirectory.dir("libs"))

    dependsOn(generateModMetadata)
    from(generateModMetadata)

    subprojects.forEach { sub ->
        val jarTask = sub.tasks.named<Jar>("jar")
        dependsOn(jarTask)
        from(jarTask.map { it.archiveFile }) {
            into("META-INF/jarjar")
        }
    }
}

tasks.assemble {
    dependsOn(buildUniversalFatJar)
}
