package slatekit.core.push

object PushBuilder {

    fun parsePlatform(platform: String?): Platform {
        return when (platform) {
            null, "" -> Platform.PlatformNone
            Platform.PlatformIOS.name -> Platform.PlatformIOS
            Platform.PlatformAnd.name -> Platform.PlatformAnd
            Platform.PlatformWeb.name -> Platform.PlatformWeb
            Platform.PlatformNone.name -> Platform.PlatformNone
            else -> Platform.PlatformNone
        }
    }
}
