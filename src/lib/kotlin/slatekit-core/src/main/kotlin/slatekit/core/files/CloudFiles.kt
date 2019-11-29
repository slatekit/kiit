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

package slatekit.core.files

import slatekit.core.cloud.CloudSupport
import slatekit.core.common.FileUtils
import slatekit.results.Try

/**
 * Abstraction for cloud based file storage and retrieval.
 * NOTES:
 * 1. This supports an async model via Coroutines
 * 2. Cloud files implementation in Slate Kit ( AWS S3 ) is Java SDK 1.0 ( synchronous )
 * 3. This is to "future proof" this public API to make it Async
 * 4. The AWS S3 component in slatekit.cloud will be migrated to Java SDK 2.0 ( async ) version in the future
 */
interface CloudFiles : CloudSupport {

    val defaultFolder:String
    val createDefaultFolder:Boolean


    /**
     * hook for any initialization
     */
    suspend fun init() {
    }


    suspend fun create(name: String, content: String) {
        create(defaultFolder, name, content)
    }

    suspend fun createFromPath(name: String, filePath: String) {

        val content = FileUtils.loadFromFile(filePath)
        create(defaultFolder, name, content)
    }

    suspend fun createFromPath(folder: String, name: String, filePath: String): Try<String> {
        // val content = "simulating from file : " + filePath
        val content = FileUtils.loadFromFile(filePath)
        return create(folder, name, content)
    }

    suspend fun delete(name: String) {
        delete(defaultFolder, name)
    }

    suspend fun getAsText(name: String): Try<String> = getAsText(defaultFolder, name)

    suspend fun download(name: String, localFolder: String): Try<String> {
        return download(defaultFolder, name, localFolder)
    }

    suspend fun downloadToFile(name: String, localFilePath: String): Try<String> {
        return downloadToFile(defaultFolder, name, localFilePath)
    }

    suspend fun update(name: String, content: String) {
        update(defaultFolder, name, content)
    }

    suspend fun updateFromPath(name: String, filePath: String): Try<String> {
        val content = "simulating from file : " + filePath; // loadFromFile(filePath)
        return update(defaultFolder, name, content)
    }

    suspend fun updateFromPath(folder: String, name: String, filePath: String): Try<String> {
        val content = FileUtils.loadFromFile(filePath)
        return update(folder, name, content)
    }

    suspend fun createRootFolder(rootFolder: String): Unit

    suspend fun create(folder: String, name: String, content: String): Try<String>

    suspend fun update(folder: String, name: String, content: String): Try<String>

    suspend fun delete(folder: String, name: String): Try<String>

    suspend fun getAsText(folder: String, name: String): Try<String>

    suspend fun download(folder: String, name: String, localFolder: String): Try<String>

    suspend fun downloadToFile(folder: String, name: String, filePath: String): Try<String>

}
