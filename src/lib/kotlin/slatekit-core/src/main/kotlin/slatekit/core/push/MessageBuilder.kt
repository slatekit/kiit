package slatekit.core.push


object MessageBuilder {


    fun parsePlatform(platform:String?): Platform {
        return when(platform) {
            null, ""          -> PlatformOther("")
            PlatformIOS.name  -> PlatformIOS
            PlatformAnd.name  -> PlatformAnd
            PlatformWeb.name  -> PlatformWeb
            PlatformNone.name -> PlatformNone
            else              -> PlatformOther(platform)
        }
    }
}
