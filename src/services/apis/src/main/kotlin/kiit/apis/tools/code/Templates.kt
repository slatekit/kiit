package kiit.apis.tools.code

import slatekit.common.io.Uris
import slatekit.common.pascalCase
import java.io.File

/**
Wrapper for the 3 code generation templates for creating the APIs and DTOs
 */
class Templates(val templates: List<CodeGenTemplate>) {

    val api = templates.first { it.name.startsWith("api.") }

    val method = templates.first { it.name.startsWith("method.") }

    val dto = templates.first { it.name.startsWith("model.") }

    companion object {
        fun load(path: String, lang: Language): Templates {
            val templates = listOf(
                 getContent(path, "api.${lang.ext}"),
                 getContent(path, "method.${lang.ext}"),
                 getContent(path, "model.${lang.ext}")
            ).map { CodeGenTemplate(it.first, it.second, it.third) }
            return Templates(templates)
        }


        private fun getContent(folderPath: String, path: String): Triple<String, String, String> {
            val pathToFile = folderPath + File.separator + path
            val content = Uris.readText(pathToFile) ?: ""
            return Triple(folderPath, path, content)
        }
    }
}


class CodeGenTemplate(val path:String, val name:String, val raw: String) {

    /**
     * Generates a file using the template, variables provided and file name
     */
    fun generate(vars: Map<String, String>, targetFolder:String, targetName:String, lang: Language) {
        val replacements = vars.map { "@{${it.key}}" to it.value }
        val template = replacements.fold(raw){ acc, next ->
            acc.replace(next.first, next.second)
        }
        File(targetFolder, targetName.pascalCase() + ".${lang.ext}").writeText(template)
    }
}
