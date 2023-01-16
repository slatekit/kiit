package slatekit.generator

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import kiit.common.io.Uris
import java.io.File


object Templates {

    const val TEMPLATE_NAME = "template.json"


    /**
     * Loads a template from and its dependencies
     * Assuming this is the structure of the templates folder:
     * ~ slate-kit
     *      - templates
     *          - slatekit-prebuilt
     *              - app
     *              - api
     *              - cli
     *              - job
     *
     *          - my-company
     *              - app
     *              - api
     *              - job
     * @param templateRootPath: Path to the template root directory  e.g. "~/slate-kit/templates"
     * @param templatePath: Path the specific named template         e.g. "my-company/app"
     */
    fun load( templateRootPath:String, templatePath:String):Template {
        val canonical = Uris.interpret(templateRootPath) ?: templateRootPath
        val root = File(canonical)
        val paths = templatePath.split("/")
        val parentDir = File(root, paths[0])
        val templateName = paths[1]
        return load(root, parentDir, templateName)
    }


    /**
     * Loads a template from the root/parent/name provided
     * @param root        : File representing the template root directory      e.g. "~/slate-kit/templates"
     * @param parentDir   : File representing the company directory under root e.g. "my-company"
     * @param templateName: Name of the template                               e.g. "app"
     */
    fun load( root:File, parentDir:File, templateName:String):Template {
        val templateDir = File(parentDir, templateName)
        val templateFile = File(templateDir, TEMPLATE_NAME)
        val templateJson = templateFile.readText()
        val template = load(root, parentDir, templateDir, templateName, templateFile, templateJson)
        return template
    }


    /**
     * Converts a JSON action into a typed action:
     * @param root    : File representing the template root directory      e.g. "~/slate-kit/templates"
     * @param parent  : File representing the company directory under root e.g. "my-company"
     * @param name    : Name of the template                               e.g. "app"
     *
     *   { "type": "copy", "doc": "Build", "source": "/templates/app/build.txt"   , "target": "/build.gradle"    },
     *   { "type": "copy", "doc": "Build", "source": "/templates/app/settings.txt", "target": "/settings.gradle" },
     */
    fun load(root:File, parent:File, dir:File, templateName:String, templateFile:File, jsonRaw: String): Template {
        val parser = JSONParser()
        val doc = parser.parse(jsonRaw)
        val jsonRoot = doc as JSONObject
        val name = jsonRoot.get("name") as String? ?: ""
        val version = jsonRoot.get("version") as String? ?: ""
        val desc = jsonRoot.get("desc") as String? ?: ""
        val type = jsonRoot.get("type") as String? ?: ""
        val jsonActions = jsonRoot.get("actions") as JSONArray
        val actions = iterateList(jsonActions, ::toAction)
        val jsonDependencies = jsonRoot.get("dependencies") as JSONArray
        val dependencies = iterateList(jsonDependencies) { _, item ->
            val key = item.keys.first().toString()
            val template = load(root, parent, key)
            template
        }
        val template = Template(root, parent, dir, templateFile, name, version, desc, type, actions, dependencies)
        return template
    }


    private fun toAction(ndx:Int, jsonAction:JSONObject):Action {
        val name = jsonAction.get("name") as String?
        val action = when (name) {
            "mkdir" -> Action.MkDir(jsonAction.get("path") as String? ?: "")
            "copy" -> Action.Copy(
                    FileType.parse(jsonAction.get("type") as String? ?: ""),
                    jsonAction.get("source") as String? ?: "",
                    jsonAction.get("target") as String? ?: ""
            )
            else   -> throw Exception("Unexpected action type: $name")
        }
        return action
    }


    private fun <T> iterateList(jsonArray: JSONArray, op:(Int, JSONObject) -> T):List<T> {
        val converted = (0 until jsonArray.size).map { ndx ->
            val jsonObj = jsonArray.get(ndx) as JSONObject
            val result = op(ndx, jsonObj)
            result
        }
        return converted
    }


    private fun <T> iterateMap(jsonObj: JSONObject, op:(Int, String, Any?) -> T):List<T> {
        val keys = jsonObj.keys.map { it?.toString() }.filterNotNull()
        val converted = keys.mapIndexed { ndx, key ->
            val obj = jsonObj.get(key)
            op(ndx, key, obj)
        }
        return converted
    }
}