package slatekit.generator

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File


object Templates {

    val gradleProps = "gradle-wrapper.properties"


    fun load( templateDirPath:String, templateName:String):Template {
        val templateDir = File(templateDirPath, templateName)
        val templateJson = File(templateDir, "package.json").readText()
        val template = Templates.load(templateJson)
        return template
    }


    /**
     * Converts a JSON action into a typed action:
     *
     *   { "type": "copy", "doc": "Build", "source": "/templates/app/build.txt"   , "target": "/build.gradle"    },
     *   { "type": "copy", "doc": "Build", "source": "/templates/app/settings.txt", "target": "/settings.gradle" },
     */
    fun load(jsonRaw: String): Template {
        val parser = JSONParser()
        val doc = parser.parse(jsonRaw)
        val jsonRoot = doc as JSONObject
        val name = jsonRoot.get("name") as String? ?: ""
        val version = jsonRoot.get("version") as String? ?: ""
        val desc = jsonRoot.get("desc") as String? ?: ""
        val type = jsonRoot.get("type") as String? ?: ""
        val jsonActions = jsonRoot.get("actions") as JSONArray
        val actions = (0..jsonActions.size).map { ndx ->
            val jsonObj = jsonActions.get(ndx) as JSONObject
            val type = jsonObj.get("type") as String?
            val action = when (type) {
                "mkdir" -> Action.MkDir(jsonObj.get("path") as String? ?: "")
                "copy" -> Action.Copy(
                        FileType.parse(jsonObj.get("doc") as String? ?: ""),
                        jsonObj.get("source") as String? ?: "",
                        jsonObj.get("target") as String? ?: ""
                        )
                else   -> throw Exception("Unexpected action type: $type")
            }
            action
        }
        val template = Template(name, version, desc, type, actions)
        return template
    }
}