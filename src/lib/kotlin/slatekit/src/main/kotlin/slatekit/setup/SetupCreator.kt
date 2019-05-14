package slatekit.setup

import io.ktor.util.combineSafe
import slatekit.SlateKit
import slatekit.common.Uris
import slatekit.common.toId
import java.io.File

class SetupCreator(val template: SetupTemplate) {

    val ctx:SetupContext = template.context


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
    fun dir(root: File, action:Dir) {
        log("Dir : " + action.path)
        val target = root.combineSafe(action.path)
        createDir(target)
    }


    /**
     * Creates the file from the root directory supplied
     */
    fun build(root: File, action:Build) {
        log("File: " + action.path)
        val content = read(action.source)
        val target = root.combineSafe(action.path)
        createFile(target, content)
    }


    /**
     * Creates the configuration file from the root directory supplied
     */
    fun conf(root: File, action:Conf) {
        log("File: " + action.path)
        val content = read(action.source)
        val target = root.combineSafe(action.path)
        createFile(target, content)
    }


    /**
     * Creates the source code from the root directory supplied
     */
    fun code(root: File, action:Conf) {
        log("File: " + action.path)
        val content = read(action.source)
        val target = root.combineSafe(action.path)
        createFile(target, content)
    }


    /**
     * Reads a file from resources
     */
    fun read(path:String):String {
        val url = SlateKit::class.java.getResource(path)
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