package slatekit.docs

data class Doc(
        val name     : String,
        val proj     : String,
        val namespace: String,
        val source   : String,
        val version  : String,
        val example  : String,
        val available: Boolean,
        val multi    : Boolean,
        val readme   : Boolean,
        val group    : String,
        val jar      : String,
        val depends  : String,
        val desc     : String
)
{

    fun sourceFolder(files: DocFiles):String
    {
        //"slate.common.args.Args"
        val path = namespace.replace(".", "/")

        // Adjust for file in root namespace ( e.g. slatekit.common.Result.kt )
        val sourceFolderPath = "src/lib/${files.lang}/${proj}/src/main/kotlin/${path}"
        return sourceFolderPath
    }


    fun dependsOn():String {
    var items = ""
    val tokens = depends.split(',')
    tokens.forEach { token ->
        when(token) {
            "com"       -> items += " slatekit.common.jar"
            "ent"       -> items += " slatekit.entities.jar"
            "core"      -> items += " slatekit.core.jar"
            "cloud"     -> items += " slatekit.cloud.jar"
            "ext"       -> items += " slatekit.ext.jar"
            "tools"     -> items += " slatekit.tools.jar"
            else        -> {}
        }
    }
    return items
}


    fun layout():String {
        return when (group) {
            "infra" -> "_mods_infra"
            "feat"  -> "_mods_fea"
            "utils" -> "_mods_utils"
            else    -> "_mods_utils"
        }
    }
}