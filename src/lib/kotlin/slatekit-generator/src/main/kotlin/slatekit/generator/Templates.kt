package slatekit.generator


object Templates {

    val gradleProps = "gradle-wrapper.properties"


    /**
     * Template for generating a Slate Kit Application
     */
    fun app(): Template {

        val actions = listOf(
                // Directories
                Action.MkDir("/gradle"),
                Action.MkDir("/gradle/wrapper"),
                Action.MkDir("/src"),
                Action.MkDir("/src/main"),
                Action.MkDir("/src/test"),
                Action.MkDir("/src/main/resources"),
                Action.MkDir("/src/main/java"),
                Action.MkDir("/src/main/kotlin", root = true),

                Action.Doc("/README.md", "/templates/common/README.md"),

                // Gradle
                Action.Build("/build.gradle", "/templates/app/build.txt"),
                Action.Build("/settings.gradle", "/templates/app/settings.txt"),
                Action.Build("/gradlew", "/templates/common/gradlew"),
                Action.Build("/gradlew.bat", "/templates/common/gradlew.bat"),
                Action.Build("/gradle.properties", "/templates/common/gradle.properties"),
                Action.Build("/gradle/wrapper/$gradleProps", "/templates/common/gradle-wrapper.properties"),

                // Conf
                Action.Conf("/src/main/resources/env.conf", "/templates/app/conf/env.conf"),
                Action.Conf("/src/main/resources/env.loc.conf", "/templates/app/conf/env.loc.conf"),
                Action.Conf("/src/main/resources/env.dev.conf", "/templates/app/conf/env.dev.conf"),
                Action.Conf("/src/main/resources/env.qat.conf", "/templates/app/conf/env.qat.conf"),
                Action.Conf("/src/main/resources/env.pro.conf", "/templates/app/conf/env.pro.conf"),
                Action.Conf("/src/main/resources/logback.xml", "/templates/app/conf/logback.txt"),

                // Code
                Action.Code("/src/main/kotlin/@app.package/Run.kt", "/templates/app/Run.txt"),
                Action.Code("/src/main/kotlin/@app.package/App.kt", "/templates/app/App.txt")
        )
        return Template("App", TemplateType.App, actions)
    }


    /**
     * Template for generating a Slate Kit Application
     */
    fun cli(): Template {

        val actions = listOf(
                // Directories
                Action.MkDir("/gradle"),
                Action.MkDir("/gradle/wrapper"),
                Action.MkDir("/src"),
                Action.MkDir("/src/main"),
                Action.MkDir("/src/test"),
                Action.MkDir("/src/main/resources"),
                Action.MkDir("/src/main/java"),
                Action.MkDir("/src/main/kotlin", root = true),

                Action.Doc("/README.md", "/templates/common/README.md"),

                // Gradle
                Action.Build("/build.gradle", "/templates/app/build.txt"),
                Action.Build("/settings.gradle", "/templates/app/settings.txt"),
                Action.Build("/gradlew", "/templates/common/gradlew"),
                Action.Build("/gradlew.bat", "/templates/common/gradlew.bat"),
                Action.Build("/gradle.properties", "/templates/common/gradle.properties"),
                Action.Build("/gradle/wrapper/$gradleProps", "/templates/common/gradle-wrapper.properties"),

                // Conf
                Action.Conf("/src/main/resources/env.conf", "/templates/app/conf/env.conf"),
                Action.Conf("/src/main/resources/env.loc.conf", "/templates/app/conf/env.loc.conf"),
                Action.Conf("/src/main/resources/env.dev.conf", "/templates/app/conf/env.dev.conf"),
                Action.Conf("/src/main/resources/env.qat.conf", "/templates/app/conf/env.qat.conf"),
                Action.Conf("/src/main/resources/env.pro.conf", "/templates/app/conf/env.pro.conf"),
                Action.Conf("/src/main/resources/logback.xml", "/templates/app/conf/logback.txt"),

                // Code
                Action.Code("/src/main/kotlin/@app.package/Run.kt", "/templates/app/Run.txt"),
                Action.Code("/src/main/kotlin/@app.package/App.kt", "/templates/app/App.txt")
        )
        return Template("App", TemplateType.App, actions)
    }


    /**
     * Template for generating a Slate Kit Application
     */
    fun lib(): Template {

        val actions = listOf(
                // Directories
                Action.MkDir("/gradle"),
                Action.MkDir("/gradle/wrapper"),
                Action.MkDir("/src"),
                Action.MkDir("/src/main"),
                Action.MkDir("/src/test"),
                Action.MkDir("/src/main/resources"),
                Action.MkDir("/src/main/java"),
                Action.MkDir("/src/main/kotlin", root = true),

                Action.Doc("/README.md", "/templates/lib/README.md"),

                // Gradle
                Action.Build("/build.gradle", "/templates/lib/build.txt"),
                Action.Build("/settings.gradle", "/templates/lib/settings.txt"),
                Action.Build("/gradlew", "/templates/common/gradlew"),
                Action.Build("/gradlew.bat", "/templates/common/gradlew.bat"),
                Action.Build("/gradle.properties", "/templates/common/gradle.properties"),
                Action.Build("/gradle/wrapper/$gradleProps", "/templates/common/gradle-wrapper.properties"),

                // Code
                Action.MkDir("/src/main/kotlin/@app.package/ext"),
                Action.MkDir("/src/main/kotlin/@app.package/models"),
                Action.MkDir("/src/main/kotlin/@app.package/services"),
                Action.MkDir("/src/main/kotlin/@app.package/types"),
                Action.MkDir("/src/main/kotlin/@app.package/utils"),
                Action.MkDir("/src/main/kotlin/@app.package/values")
        )
        return Template("App", TemplateType.App, actions)
    }


    /**
     * Template for generating a Slate Kit Application
     */
    fun srv(): Template {

        val actions = listOf(
                // Directories
                Action.MkDir("/gradle"),
                Action.MkDir("/gradle/wrapper"),
                Action.MkDir("/src"),
                Action.MkDir("/src/main"),
                Action.MkDir("/src/test"),
                Action.MkDir("/src/main/resources"),
                Action.MkDir("/src/main/java"),
                Action.MkDir("/src/main/kotlin", root = true),
                Action.MkDir("/src/main/kotlin/@app.package/apis"),
                Action.MkDir("/src/main/kotlin/@app.package/auth"),
                Action.MkDir("/src/main/kotlin/@app.package/models"),

                Action.Doc("/README.md", "/templates/common/README.md"),

                // Gradle
                Action.Build("/build.gradle", "/templates/srv/build.txt"),
                Action.Build("/settings.gradle", "/templates/srv/settings.txt"),
                Action.Build("/gradlew", "/templates/common/gradlew"),
                Action.Build("/gradlew.bat", "/templates/common/gradlew.bat"),
                Action.Build("/gradle.properties", "/templates/common/gradle.properties"),
                Action.Build("/gradle/wrapper/$gradleProps", "/templates/common/gradle-wrapper.properties"),

                // Conf
                Action.Conf("/src/main/resources/env.conf", "/templates/srv/conf/env.conf"),
                Action.Conf("/src/main/resources/env.loc.conf", "/templates/srv/conf/env.loc.conf"),
                Action.Conf("/src/main/resources/env.dev.conf", "/templates/srv/conf/env.dev.conf"),
                Action.Conf("/src/main/resources/env.qat.conf", "/templates/srv/conf/env.qat.conf"),
                Action.Conf("/src/main/resources/env.pro.conf", "/templates/srv/conf/env.pro.conf"),
                Action.Conf("/src/main/resources/logback.xml", "/templates/srv/conf/logback.txt"),

                // Code
                Action.Code("/src/main/kotlin/@app.package/Run.kt", "/templates/srv/Run.txt"),
                Action.Code("/src/main/kotlin/@app.package/App.kt", "/templates/srv/App.txt"),
                Action.Code("/src/main/kotlin/@app.package/apis/SampleAPI.kt", "/templates/srv/SampleAPI.txt"),
                Action.Code("/src/main/kotlin/@app.package/auth/SampleAuth.kt", "/templates/srv/SampleAuth.txt"),
                Action.Code("/src/main/kotlin/@app.package/models/SampleModel.kt", "/templates/srv/SampleModel.txt"),
                Action.Code("/src/main/kotlin/@app.package/Server.kt", "/templates/srv/Server.txt")
        )
        return Template("App", TemplateType.App, actions)
    }
}