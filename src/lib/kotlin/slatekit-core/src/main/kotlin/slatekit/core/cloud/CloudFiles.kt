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

import slatekit.results.Try

/**
 * Abstraction for cloud base file storage and retrieval.
 */
interface CloudFiles : CloudSupport {

    val defaultFolder:String
    val createDefaultFolder:Boolean


    /**
     * hook for any initialization
     */
    fun init() {
    }

    fun create(name: String, content: String) {
        create(defaultFolder, name, content)
    }

    fun createFromPath(name: String, filePath: String) {

        val content = CloudUtils.loadFromFile(filePath)
        create(defaultFolder, name, content)
    }

    fun createFromPath(folder: String, name: String, filePath: String): Try<String> {
        // val content = "simulating from file : " + filePath
        val content = CloudUtils.loadFromFile(filePath)
        return create(folder, name, content)
    }

    fun delete(name: String) {
        delete(defaultFolder, name)
    }

    fun getAsText(name: String): Try<String> = getAsText(defaultFolder, name)

    fun download(name: String, localFolder: String): Try<String> {
        return download(defaultFolder, name, localFolder)
    }

    fun downloadToFile(name: String, localFilePath: String): Try<String> {
        return downloadToFile(defaultFolder, name, localFilePath)
    }

    fun update(name: String, content: String) {
        update(defaultFolder, name, content)
    }

    fun updateFromPath(name: String, filePath: String): Try<String> {
        val content = "simulating from file : " + filePath; // loadFromFile(filePath)
        return update(defaultFolder, name, content)
    }

    fun updateFromPath(folder: String, name: String, filePath: String): Try<String> {
        val content = CloudUtils.loadFromFile(filePath)
        return update(folder, name, content)
    }

    fun createRootFolder(rootFolder: String): Unit

    fun create(folder: String, name: String, content: String): Try<String>

    fun update(folder: String, name: String, content: String): Try<String>

    fun delete(folder: String, name: String): Try<String>

    fun getAsText(folder: String, name: String): Try<String>

    fun download(folder: String, name: String, localFolder: String): Try<String>

    fun downloadToFile(folder: String, name: String, filePath: String): Try<String>

}
