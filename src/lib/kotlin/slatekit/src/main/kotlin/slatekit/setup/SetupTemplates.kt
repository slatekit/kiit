package slatekit.setup


data class AddDir   (val path:String, val root:Boolean = false) : SetupAction
data class AddFile  (val path:String, val type:String, val source:String?, val replace:Boolean = true) : SetupAction


object SetupTemplates {

    fun app():List<SetupAction> {
        return listOf(
                // Gradle
                AddFile("/build.gradle"   , "gradle", "templates/app/build.txt"),
                AddFile("/settings.gradle", "gradle", "templates/app/settings.txt"),

                // Directories
                AddDir("/src"),
                AddDir("/src/main"),
                AddDir("/src/main/resources"),
                AddDir("/src/main/kotlin", root = true),

                // Conf
                AddFile("/src/main/resources/env.conf"    , "conf","templates/app/conf/env.conf"),
                AddFile("/src/main/resources/env.loc.conf", "conf","templates/app/conf/env.loc.conf"),
                AddFile("/src/main/resources/env.dev.conf", "conf","templates/app/conf/env.dev.conf"),
                AddFile("/src/main/resources/env.qat.conf", "conf","templates/app/conf/env.qat.conf"),
                AddFile("/src/main/resources/env.pro.conf", "conf","templates/app/conf/env.pro.conf"),

                // Files
                AddFile("/src/main/kotlin/@app.package/Run", "kt", "templates/app/Run.txt"),
                AddFile("/src/main/kotlin/@app.package/App", "kt", "templates/app/App.txt")
        )
    }
}