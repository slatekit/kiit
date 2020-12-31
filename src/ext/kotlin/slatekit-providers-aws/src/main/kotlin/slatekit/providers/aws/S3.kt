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

package slatekit.providers.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import slatekit.common.info.ApiLogin
import slatekit.common.io.Uris
import slatekit.core.files.CloudFiles
import slatekit.core.common.FileUtils
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.getOrElse
import java.io.File

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

    /**
     * creates a file with the supplied folder name, file name, and content
     *
     * @param folder
     * @param name
     * @param content
     */
    override suspend fun create(folder: String, name: String, content: String): Try<String> {
        return put("create", folder, name, content)
    }

    /**
     * updates a file with the supplied folder name, file name, and content
     *
     * @param folder
     * @param name
     * @param content
     */
    override suspend fun update(folder: String, name: String, content: String): Try<String> {
        return put("update", folder, name, content)
    }

    /**
     * deletes a file with the supplied folder name, file name
     *
     * @param folder
     * @param name
     */
    override suspend fun delete(folder: String, name: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult(SOURCE, "delete", data = fullName, call = {
            s3.deleteObject(rootFolder, fullName)
            fullName
        })
    }

    /**
     * gets the file specified by folder and name, as text content
     *
     * @param folder
     * @param name
     * @return
     */
    override suspend fun getAsText(folder: String, name: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult(SOURCE, "getAsText", data = fullName, call = {

            val obj = s3.getObject(GetObjectRequest(rootFolder, fullName))
            val content = FileUtils.toString(obj.getObjectContent())
            // val content = "simulating download of " + fullName
            content
        })
    }

    /**
     * downloads the file specified by folder and name to the local folder specified.
     *
     * @param folder
     * @param name
     * @param localFolder
     * @return
     */
    override suspend fun download(folder: String, name: String, localFolder: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "download", data = fullName, call = {
            val content = getAsText(folder, name)
            val finalFolder = Uris.interpret(localFolder)
            val localFile = File(finalFolder, name)
            val localFileName = localFile.absolutePath
            File(localFileName).writeText(content.getOrElse { "" })
            localFileName
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
        return executeResult(SOURCE, "download", data = fullName, call = {
            val content = getAsText(folder, name)
            val localFile = Uris.interpret(filePath)
            val localFileName = localFile ?: name
            File(localFileName).writeText(content.getOrElse { "" })
            localFileName
        })
    }

    /**
     * uploads the file to the datasource using the supplied folder, filename, and content
     *
     * @param folder
     * @param name
     * @param content
     * @return
     */
    suspend fun put(action: String, folder: String, name: String, content: String): Try<String> {
        // full name of the file is folder + name
        val fullName = getName(folder, name)

        return executeResult(SOURCE, action, data = fullName, call = {

            s3.putObject(rootFolder, fullName, FileUtils.toInputStream(content), ObjectMetadata())
            fullName
        })
    }

    private fun getName(folder: String, name: String): String {
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
