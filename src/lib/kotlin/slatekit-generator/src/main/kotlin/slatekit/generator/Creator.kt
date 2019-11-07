package slatekit.generator

import slatekit.common.Uris
import slatekit.common.toId
import slatekit.common.utils.Props
import java.io.File

/**
 * This processes all the [Action]s supported
 */
class Creator(val ctx: GeneratorContext, val template: Template, val cls:Class<*>) {

    /**
     * Creates the directory after first interpreting the path.
     * For example: "~/git/app1"
     */
    fun create(path:String, interpret:Boolean ): File {
        val finalPath = if(interpret) Uris.interpret(path) else path
        val dest = File( finalPath )
        createDir(dest)
        return dest
    }


    /**
     * Creates the directory
     */
    fun createDir(dest: File): File {
        if(!dest.exists()) {
            log("creating ${dest.absolutePath}")
            dest.mkdir()
        }
        return dest
    }


    /**
     * Creates the directory
     */
    fun createFile(dest: File, content:String): File {
        log("creating ${dest.absolutePath}")
        dest.writeText(content)
        return dest
    }


    /**
     * Creates the directory from the root directory supplied
     */
    fun dir(root: File, action: Action.MkDir) {
        log("Dir : " + action.path)
        val packagePath = ctx.packageName.replace(".", Props.pathSeparator)
        val target = File(root, action.path.replace("@app.package", packagePath))
        createDir(target)
    }


    /**
     * Creates the file from the root directory supplied
     */
    fun copy(root: File, action: Action.Copy) {
        when(action.fileType){
            is FileType.Code -> code(root, action)
            else -> file(root, action)
        }
    }


    /**
     * Creates the file from the root directory supplied
     */
    fun file(root: File, action: Action.Copy) {
        log("${action.fileType}: " + action.target)
        val content = read(action.source)
        val target = File(root, action.target)
        createFile(target, content)
    }


    /**
     * Creates the source code from the root directory supplied
     */
    fun code(root: File, action: Action.Copy) {
        log("Code: " + action.target)
        val content = read(action.source)
        val packagePath = ctx.packageName.replace(".", Props.pathSeparator)
        val target = File(root, action.target.replace("@app.package", packagePath))
        createFile(target, content)
    }


    /**
     * Reads a file from resources
     */
    fun read(path:String):String {
        val url = cls.getResource(path)
        val text = File(url.file).readText()
        val converted = replace(text)
        return converted
    }


    /**
     * Reads a file from resources
     */
    fun replace(content:String):String {
        val converted = content
                .replace("\${app.id}", ctx.name.toId())
                .replace("\${app.name}", ctx.name)
                .replace("\${app.desc}", ctx.desc)
                .replace("\${app.package}", ctx.packageName)
                .replace("\${app.url}", ctx.name)
                .replace("\${app.company}", ctx.company)
        return converted
    }


    private fun log(msg:String){
        println(msg )
    }
}