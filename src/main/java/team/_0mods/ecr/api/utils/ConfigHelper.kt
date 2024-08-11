package team._0mods.ecr.api.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.minecraftforge.fml.loading.FMLPaths
import team._0mods.ecr.LOGGER
import java.io.File

inline fun <reified T> T.loadConfig(json: Json, fileName: String): T {
    LOGGER.debug("Loading config '$fileName'")

    val file = FMLPaths.GAMEDIR.get().resolve("config/").toFile().resolve("$fileName.json")

    return if (file.exists()) {
        try {
            decodeCfg(json, file)
        } catch (e: Exception) {
            LOGGER.error("Failed to load config with name ${file.canonicalPath}.")
            LOGGER.warn("Regenerating config... Using defaults.")
            file.delete()
            encodeCfg(json, file)
            this
        }
    } else {
        encodeCfg(json, file)
        this
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> T.encodeCfg(json: Json, file: File) {
    try {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        json.encodeToStream(this, file.outputStream())
    } catch (e: FileSystemException) {
        LOGGER.error("Failed to write config to file '$file'", e)
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeCfg(json: Json, file: File): T = try {
    json.decodeFromStream(file.inputStream())
} catch (e: FileSystemException) {
    LOGGER.error("Failed to read config from file '$file'", e)
    throw e
}