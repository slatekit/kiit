package slatekit.setup


data class Dir   (val path:String, val root:Boolean = false) : SetupAction
data class Build (val path:String, val source:String, val replace:Boolean = true) : SetupAction
data class Conf  (val path:String, val source:String, val replace:Boolean = true) : SetupAction
data class Code  (val path:String, val source:String, val replace:Boolean = true) : SetupAction


object SetupTemplates {

    fun app():List<SetupAction> {
        return listOf(
                // Gradle
                Build("/build.gradle"   , "/templates/app/build.txt"),
                Build("/settings.gradle", "/templates/app/settings.txt"),

                // Directories
                Dir("/src"),
                Dir("/src/main"),
                Dir("/src/main/resources"),
                Dir("/src/main/kotlin", root = true),

                // Conf
                Conf("/src/main/resources/env.conf"    , "/templates/app/conf/env.conf"),
                Conf("/src/main/resources/env.loc.conf", "/templates/app/conf/env.loc.conf"),
                Conf("/src/main/resources/env.dev.conf", "/templates/app/conf/env.dev.conf"),
                Conf("/src/main/resources/env.qat.conf", "/templates/app/conf/env.qat.conf"),
                Conf("/src/main/resources/env.pro.conf", "/templates/app/conf/env.pro.conf"),

                // Files
                Code("/src/main/kotlin/@app.package/Run.kt", "/templates/app/Run.txt"),
                Code("/src/main/kotlin/@app.package/App.kt", "/templates/app/App.txt")
        )
    }
}