plugins {
    base
    idea
    id("com.gradleup.shadow") apply false
    id("me.modmuss50.mod-publish-plugin")
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
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
