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

package kiit.providers.aws

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import slatekit.common.info.ApiLogin
import slatekit.common.io.Uris
import slatekit.core.files.CloudFiles
import slatekit.core.common.FileUtils
import slatekit.common.Provider
import slatekit.core.files.CloudFile
import slatekit.core.files.CloudFileEntry
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.getOrElse
import java.io.ByteArrayInputStream
import java.io.File
import java.time.Instant
import kotlin.math.exp

/**
 *
 * @param credentials : The aws credentials
 * @param region : AWS Region e.g. Regions.US_EAST_1
 * @param bucket : Name of the bucket to store files in
 * @param createBucket : Whether or not to create the bucket
 */
class S3(
        credentials: AWSCredentials,
        val region: Regions,
        bucket: String,
        createBucket: Boolean
) : CloudFiles, AwsSupport {

    override val rootFolder = formatBucket(bucket)
    override val createRootFolder = createBucket

    private val SOURCE = "aws:s3"
    private val FOLDER_SEPARATOR = "/"
    private val s3: AmazonS3Client = AwsFuncs.s3(credentials, region)

    override val provider: Any = s3

    /**
     * hook for any initialization
     */
    override suspend fun init() {
        if (createRootFolder) {
            s3.createBucket(rootFolder)
        }
    }

    /**
     * creates a root folder/bucket with the supplied name.
     *
     * @param rootFolder
     */
    override suspend fun createRootFolder(rootFolder: String): Try<String> {
        return if (!rootFolder.isNullOrEmpty() && rootFolder != this.rootFolder) {
            Try.attempt { s3.createBucket(rootFolder).name }
        } else {
            Tries.success("Exists")
        }
    }

    override suspend fun create(entry: CloudFile): Try<String> {
        return put("create", entry)
    }


    override suspend fun update(entry: CloudFile): Try<String> {
        return put("update", entry)
    }


    override suspend fun delete(entry: CloudFile): Try<String> {
        val fullName = getName(entry.path, entry.name)
        return executeResult(SOURCE, "delete", data = fullName, call = {
            s3.deleteObject(rootFolder, fullName)
            fullName
        })
    }


    /**
     * uploads the file to the datasource using the supplied folder, filename, and content
     * @param action: "create" | "update"
     * @param entry : content of file
     * @return
     */
    suspend fun put(action: String, entry:CloudFile): Try<String> {
        // full name of the file is folder + name
        val fullName = getName(entry.path, entry.name)
        return executeResult(SOURCE, action, data = fullName, call = {
            val meta = ObjectMetadata()
            entry.atts?.map { meta.addUserMetadata("x-amz-meta-${it.key}", it.value) }
            s3.putObject(rootFolder, fullName, ByteArrayInputStream(entry.data), meta)
            fullName
        })
    }


    override suspend fun getFile(entry: CloudFile): Try<CloudFile> {
        val fullName = getName(entry.path, entry.name)
        return executeResult(SOURCE, "getfile", data = fullName, call = {
            val obj = s3.getObject(GetObjectRequest(rootFolder, fullName))
            val content = obj.getObjectContent()
            val data = content.readBytes()
            val atts = obj.objectMetadata.userMetadata
            CloudFileEntry(entry.path, entry.name, data, atts)
        })
    }

    /**
     * downloads the file specified by folder and name to the local folder specified.
     *
     * @param folder
     * @param name
     * @param filePath
     * @return
     */
    override suspend fun downloadToFile(folder: String, name: String, filePath: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "download", data = fullName, call = {
            val content = getFile(folder, name)
            val localFile = File(filePath)
            val result = content.map { f ->
                localFile.writeBytes(f.data)
                localFile.absolutePath
            }
            result.getOrElse { "" }
        })
    }

    override suspend fun buildSignedGetUrl(folder: String?, name:String, expiresInSeconds:Int): String {
        return buildSignedUrl(folder, name, HttpMethod.GET, expiresInSeconds)
    }

    override suspend fun buildSignedPutUrl(folder: String?, name:String, expiresInSeconds:Int): String {
        return buildSignedUrl(folder, name, HttpMethod.GET, expiresInSeconds)
    }


    private suspend fun buildSignedUrl(folder: String?, name:String, method:HttpMethod, expiresInSeconds:Int): String {
        val now = Instant.now().toEpochMilli()
        val expires = now + 1000 * expiresInSeconds
        val exp = java.util.Date(expires)
        val finalFolder = folder ?: rootFolder
        val req = GeneratePresignedUrlRequest(finalFolder, name, method).withExpiration(exp)
        val url = s3.generatePresignedUrl(req)
        return url.toString()
    }

    private fun getName(folder: String?, name: String): String {
        // Case 1: no folder supplied, assume in root bucket
        return when {
            folder.isNullOrEmpty() -> name

            // Case 2: folder == root folder
            folder == rootFolder -> name

            // Case 3: sub-folder
            else -> "$folder$FOLDER_SEPARATOR$name"
        }
    }


    companion object {
        fun of(region: String, bucket: String, createBucket: Boolean, apiKey: ApiLogin): Try<S3> {
            return build(region) { regions ->
                S3(AwsFuncs.credsWithKeySecret(apiKey.key, apiKey.pass), regions, bucket, createBucket)
            }
        }

        fun of(cls:Class<*>, region: String, bucket: String, createBucket: Boolean, confPath: String? = null, confSection: String? = null): Try<S3> {
            return build(region) { regions ->
                S3(AwsFuncs.creds(cls, confPath, confSection), regions, bucket, createBucket)
            }
        }
    }
}
