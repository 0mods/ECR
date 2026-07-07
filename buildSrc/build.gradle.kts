plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(localGroovy())

    implementation("org.yaml:snakeyaml:2.6")
}


