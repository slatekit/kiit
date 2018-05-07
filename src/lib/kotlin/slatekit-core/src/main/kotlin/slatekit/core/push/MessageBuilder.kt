package slatekit.core.push


object MessageBuilder {


    fun parsePlatform(platform:String?): Platform {
        return when(platform) {
            null, ""          -> OtherPlatform("")
            PlatformIOS.name  -> PlatformIOS
            PlatformAnd.name  -> PlatformAnd
            PlatformWeb.name  -> PlatformWeb
            PlatformNone.name -> PlatformNone
            else              -> OtherPlatform(platform)
        }
    }
}
