package slatekit.generator


object Templates {

    val gradleProps = "gradle-wrapper.properties"


    /**
     * Template for generating a Slate Kit Application
     */
    fun app():Template {

        val actions = listOf(
                // Directories
                Action.MkDir("/gradle"),
                Action.MkDir("/gradle/wrapper"),
                Action.MkDir("/src"),
                Action.MkDir("/src/main"),
                Action.MkDir("/src/main/resources"),
                Action.MkDir("/src/main/kotlin", root = true),

                Action.Doc  ("/README.md"                       , "/templates/common/README.md"),

                // Gradle
                Action.Build("/build.gradle"                  , "/templates/app/build.txt"),
                Action.Build("/settings.gradle"               , "/templates/app/settings.txt"),
                Action.Build("/gradlew"                       , "/templates/common/gradlew"),
                Action.Build("/gradlew.bat"                   , "/templates/common/gradlew.bat"),
                Action.Build("/gradle/wrapper/$gradleProps"   , "/templates/common/gradle-wrapper.properties"),

                // Conf
                Action.Conf("/src/main/resources/env.conf"    , "/templates/app/conf/env.conf"),
                Action.Conf("/src/main/resources/env.loc.conf", "/templates/app/conf/env.loc.conf"),
                Action.Conf("/src/main/resources/env.dev.conf", "/templates/app/conf/env.dev.conf"),
                Action.Conf("/src/main/resources/env.qat.conf", "/templates/app/conf/env.qat.conf"),
                Action.Conf("/src/main/resources/env.pro.conf", "/templates/app/conf/env.pro.conf"),
                Action.Conf("/src/main/resources/logback.xml" , "/templates/app/conf/logback.txt"),

                // Code
                Action.Code("/src/main/kotlin/@app.package/Run.kt", "/templates/app/Run.txt"),
                Action.Code("/src/main/kotlin/@app.package/App.kt", "/templates/app/App.txt")
        )
        return Template("App", actions)
    }
}