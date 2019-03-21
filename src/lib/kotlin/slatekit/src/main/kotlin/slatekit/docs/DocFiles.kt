package slatekit.docs

import java.io.File


class DocFiles(val ext: String = "kt", val lang: String = "kotlin") {

    fun buildComponentFolder(root: String, doc: Doc): String {
        // s"${root}\src\lib\kotlin\Slate.Common\src\main\kotlin
        val componentFolder = doc.namespace.replace(".", "/")
        val result = "${root}/src/lib/$lang/${doc.proj}/src/main/$lang/${componentFolder}"
        return result
    }


    fun buildComponentExamplePath(root: String, doc: Doc): String {
        // s"${root}\src\lib\kotlin\Slate.Common\src\main\kotlin
        val result = File(root, buildComponentExamplePathLink(doc))
        return result.absolutePath
    }


    /**
     * Path to the example file
     */
    fun buildComponentExamplePathLink(doc: Doc): String {
        // {root}/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Examples_Args.kt
        val result = "src/lib/$lang/slatekit-examples/src/main/$lang/slatekit/examples/${doc.example}.$ext"
        return result.replace("/", "/")
    }


    fun buildSourceFolder(proj: String, folder: String): String {
        return when (proj) {
            "common" -> "slatekit-common/src/main/${lang}/slatekit/common/${folder}"
            "entities" -> "slatekit-entities/src/main/${lang}/slatekit/entities/${folder}"
            "core" -> "slatekit-core/src/main/${lang}/slatekit/core/${folder}"
            "cloud" -> "slatekit-cloud/src/main/${lang}/slatekit/cloud/${folder}"
            "ext" -> "slatekit-ext/src/main/${lang}/slatekit/ext/${folder}"
            else -> ""
        }
    }
}