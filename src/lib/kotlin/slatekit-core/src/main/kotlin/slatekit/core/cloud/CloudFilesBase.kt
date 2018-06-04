/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.cloud

import slatekit.common.ResultEx
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * Abstraction for cloud base file storage and retrieval.
 */
abstract class CloudFilesBase(val _defaultFolder: String, val _createDefaultFolder: Boolean) : CloudActions {


    /**
     * hook for any initialization
     */
    open fun init(): Unit {

    }


    fun create(name: String, content: String) {
        create(_defaultFolder, name, content)
    }


    fun createFromPath(name: String, filePath: String) {

        val content = loadFromFile(filePath)
        create(_defaultFolder, name, content)
    }


    fun createFromPath(folder: String, name: String, filePath: String): ResultEx<String> {
        //val content = "simulating from file : " + filePath
        val content = loadFromFile(filePath)
        return create(folder, name, content)
    }


    fun delete(name: String): Unit {
        delete(_defaultFolder, name)
    }


    fun getAsText(name: String): ResultEx<String> = getAsText(_defaultFolder, name)


    fun download(name: String, localFolder: String): ResultEx<String> {
        return download(_defaultFolder, name, localFolder)
    }


    fun downloadToFile(name: String, localFilePath: String): ResultEx<String> {
        return downloadToFile(_defaultFolder, name, localFilePath)
    }


    fun update(name: String, content: String): Unit {
        update(_defaultFolder, name, content)
    }


    fun updateFromPath(name: String, filePath: String): ResultEx<String> {
        val content = "simulating from file : " + filePath; //loadFromFile(filePath)
        return update(_defaultFolder, name, content)
    }


    fun updateFromPath(folder: String, name: String, filePath: String): ResultEx<String> {
        val content = loadFromFile(filePath)
        return update(folder, name, content)
    }


    abstract fun createRootFolder(rootFolder: String): Unit


    abstract fun create(folder: String, name: String, content: String): ResultEx<String>


    abstract fun update(folder: String, name: String, content: String): ResultEx<String>


    abstract fun delete(folder: String, name: String): ResultEx<String>


    abstract fun getAsText(folder: String, name: String): ResultEx<String>


    abstract fun download(folder: String, name: String, localFolder: String): ResultEx<String>


    abstract fun downloadToFile(folder: String, name: String, filePath: String): ResultEx<String>


    protected fun loadFromFile(filePath: String): String {
        return File(filePath).readText()
    }


    protected fun toInputStream(content: String): InputStream {
        return ByteArrayInputStream(content.toByteArray())
    }


    protected fun toString(input: InputStream): String {
        return input.bufferedReader().use { it.readText() }
    }
}
