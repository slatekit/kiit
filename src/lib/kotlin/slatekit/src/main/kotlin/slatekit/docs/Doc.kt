package slatekit.docs

data class Doc(
        val name     : String,
        val proj     : String,
        val source   : String,
        val version  : String,
        val example  : String,
        val available: Boolean,
        val multi    : Boolean,
        val readme   : Boolean,
        val group    : String,
        val folder   : String,
        val jar      : String,
        val depends  : String,
        val desc     : String
)
{

    fun namespace():String  {
        //"slate.common.args.Args"
        return source.substring(0, source.lastIndexOf("."))
    }


    fun sourceFolder(files: DocFiles):String
    {
        //"slate.common.args.Args"
        val path = namespace().replace(".", "/")
        val proj = path.split('/')[0]
        val nsfolder = path.split('/').last()

        // Adjust for file in root namespace ( e.g. slatekit.common.Result.kt )
        val finalNSFolder = if(nsfolder.endsWith("common")) "" else nsfolder
        val sourceFolderPath = "src/lib/${files.lang}/${proj}/" + files.buildSourceFolder(proj, finalNSFolder)
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