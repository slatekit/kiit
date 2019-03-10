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

package slatekit.cloud.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import slatekit.common.*
import slatekit.common.info.ApiLogin
import slatekit.core.cloud.CloudFiles
import slatekit.core.cloud.CloudUtils
import slatekit.results.Try
import slatekit.results.getOrElse
import java.io.File

/**
 *
 * @param bucket : Name of the bucket to store files in
 * @param createBucket : Whether or not to create the bucket
 * @param path : Path to aws conf file, e.g. Some("user://myapp/conf/sqs.conf")
 * @param section : Name of section in conf file for api key. e.g. Some("sqs")
 */
class AwsCloudFiles(
    bucket: String,
    createBucket: Boolean,
    creds: AWSCredentials
) : CloudFiles, AwsSupport {

    override val defaultFolder = bucket
    override val createDefaultFolder = createBucket

    private val SOURCE = "aws:s3"
    private val _s3: AmazonS3Client = AwsFuncs.s3(creds)

    constructor(
        bucket: String,
        createBucket: Boolean,
        apiKey: ApiLogin
    ) : this(
                bucket, createBucket, AwsFuncs.credsWithKeySecret(apiKey.key, apiKey.pass)
    )

    constructor(
        bucket: String,
        createBucket: Boolean,
        confPath: String? = null,
        section: String? = null
    ) : this (
            bucket, createBucket, AwsFuncs.creds(confPath, section)
    )

    /**
     * hook for any initialization
     */
    override fun init() {
        if (createDefaultFolder) {
            _s3.createBucket(defaultFolder)
        }
    }

    /**
     * creates a root folder/bucket with the supplied name.
     *
     * @param rootFolder
     */
    override fun createRootFolder(rootFolder: String) {
        if (!rootFolder.isNullOrEmpty() && rootFolder != defaultFolder) {
            _s3.createBucket(rootFolder)
        }
    }

    /**
     * creates a file with the supplied folder name, file name, and content
     *
     * @param folder
     * @param name
     * @param content
     */
    override fun create(folder: String, name: String, content: String): Try<String> {
        return put("create", folder, name, content)
    }

    /**
     * updates a file with the supplied folder name, file name, and content
     *
     * @param folder
     * @param name
     * @param content
     */
    override fun update(folder: String, name: String, content: String): Try<String> {
        return put("update", folder, name, content)
    }

    /**
     * deletes a file with the supplied folder name, file name
     *
     * @param folder
     * @param name
     */
    override fun delete(folder: String, name: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult(SOURCE, "delete", data = fullName, call = {
            _s3.deleteObject(defaultFolder, fullName)
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
    override fun getAsText(folder: String, name: String): Try<String> {
        val fullName = getName(folder, name)
        return executeResult(SOURCE, "getAsText", data = fullName, call = {

            val obj = _s3.getObject(GetObjectRequest(defaultFolder, fullName))
            val content = CloudUtils.toString(obj.getObjectContent())
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
    override fun download(folder: String, name: String, localFolder: String): Try<String> {
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
    override fun downloadToFile(folder: String, name: String, filePath: String): Try<String> {
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
    fun put(action: String, folder: String, name: String, content: String): Try<String> {
        // full name of the file is folder + name
        val fullName = getName(folder, name)

        return executeResult(SOURCE, action, data = fullName, call = {

            _s3.putObject(defaultFolder, fullName, CloudUtils.toInputStream(content), ObjectMetadata())
            fullName
        })
    }

    private fun getName(folder: String, name: String): String {
        // Case 1: no folder supplied, assume in root bucket
        return when {
            folder.isNullOrEmpty() -> name

            // Case 2: folder == root folder
            folder == defaultFolder -> name

            // Case 3: sub-folder
            else -> "$folder-$name"
        }
    }
}
