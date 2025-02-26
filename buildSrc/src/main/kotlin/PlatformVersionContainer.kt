class PlatformVersionContainer(
    // first - mc version
    // second - application version
    val fabricLoader: String = "0.16.10",
    val fabric: Map<String, String> = mapOf(),
    val forge: Map<String, String> = mapOf(),
    val neoforge: Map<String, String> = mapOf(),
)