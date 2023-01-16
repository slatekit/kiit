/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package kiit.core.files

import kiit.common.Provider
import kiit.common.io.Uris
import kiit.core.cloud.CloudSupport
import kiit.core.common.FileUtils
import kiit.results.Try
import kiit.results.then
import java.io.File

/**
 * Abstraction for cloud based file storage and retrieval using simplified CRUD operations
 * NOTES:
 * 1. This supports an async model via Coroutines
 * 2. Cloud files implementation in Slate Kit ( AWS S3 ) is Java SDK 1.0 ( synchronous )
 * 3. This is to "future proof" this public API to make it Async
 * 4. The AWS S3 component in kiit.providers.aws will be migrated to Java SDK 2.0 ( async ) version in the future
 */
interface CloudFiles : CloudSupport, Provider {

    val rootFolder:String
    val createRootFolder:Boolean


    /**
     * hook for any initialization
     */
    suspend fun init() {
    }

    suspend fun create(name: String, content: String): Try<String> {
        return create(CloudFileEntry(rootFolder, name, content.toByteArray(), null))
    }

    suspend fun create(name: String, content: ByteArray): Try<String> {
        return create(CloudFileEntry(rootFolder, name, content, null))
    }

    suspend fun create(folder: String, name: String, content: String): Try<String> {
        return create(CloudFileEntry(folder, name, content.toByteArray(), null))
    }

    suspend fun create(folder: String, name: String, content: ByteArray): Try<String> {
        return create(CloudFileEntry(rootFolder, name, content, null))
    }

    suspend fun update(name: String, content: String): Try<String> {
        return update(CloudFileEntry(rootFolder, name, content.toByteArray(), null))
    }

    suspend fun update(name: String, content: ByteArray): Try<String> {
        return update(CloudFileEntry(rootFolder, name, content, null))
    }

    suspend fun update(folder: String, name: String, content: String): Try<String> {
        return update(CloudFileEntry(folder, name, content.toByteArray(), null))
    }

    suspend fun update(folder: String, name: String, content: ByteArray): Try<String> {
        return update(CloudFileEntry(rootFolder, name, content, null))
    }

    suspend fun delete(name: String):Try<String> {
        return delete(rootFolder, name)
    }

    suspend fun delete(folder: String, name: String): Try<String> {
        return delete(CloudFileEntry(folder, name, byteArrayOf()))
    }

    suspend fun createFromPath(name: String, filePath: String):Try<String> {
        return createFromPath(rootFolder, name, filePath)
    }

    suspend fun createFromPath(folder: String, name: String, filePath: String): Try<String> {
        // val content = "simulating from file : " + filePath
        val content = FileUtils.loadFromFile(filePath)
        return create(folder, name, content)
    }

    suspend fun updateFromPath(name: String, filePath: String): Try<String> {
        return updateFromPath(rootFolder, name, filePath)
    }

    suspend fun updateFromPath(folder: String, name: String, filePath: String): Try<String> {
        val content = FileUtils.loadFromFile(filePath)
        return update(folder, name, content)
    }

    suspend fun buildSignedGetUrl(folder: String?, name: String, expiresInSeconds:Int):String
    suspend fun buildSignedPutUrl(folder: String?, name: String, expiresInSeconds:Int):String

    suspend fun getFile(name: String): Try<CloudFile> = getFile(rootFolder, name)
    suspend fun getFile(folder: String, name: String): Try<CloudFile> = getFile(CloudFileEntry(rootFolder, name, byteArrayOf(), null))
    suspend fun getFileBytes(name: String): Try<ByteArray> = getFileBytes(rootFolder, name)
    suspend fun getFileBytes(folder: String, name: String):Try<ByteArray> = getFile(folder, name).map { it.data }
    suspend fun getFileText(name: String): Try<String> = getFileText(rootFolder, name)
    suspend fun getFileText(folder: String, name: String): Try<String> = getFile(folder, name).map { it.textOrEmpty }
    suspend fun download(name: String, localFolder: String): Try<String> = download(rootFolder, name, localFolder)
    suspend fun downloadToFile(name: String, localFilePath: String): Try<String> = downloadToFile(rootFolder, name, localFilePath)
    suspend fun download(folder: String, name: String, localFolder: String): Try<String> {
        val finalFolder = Uris.interpret(localFolder)
        val localFile = File(finalFolder, name)
        val localFilePath = localFile.absolutePath
        return downloadToFile(folder, name, localFilePath)
    }

    suspend fun createRootFolder(rootFolder: String):Try<String>
    suspend fun create(entry: CloudFile): Try<String>
    suspend fun update(entry: CloudFile): Try<String>
    suspend fun delete(entry: CloudFile): Try<String>
    suspend fun getFile(entry:CloudFile): Try<CloudFile>
    suspend fun downloadToFile(folder: String, name: String, filePath: String): Try<String>
}
