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

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import slatekit.common.Result
import slatekit.common.Uris
import slatekit.core.cloud.CloudFilesBase
import java.io.File

/**
 *
 * @param bucket       : Name of the bucket to store files in
 * @param createBucket : Whether or not to create the bucket
 * @param path         : Path to aws conf file, e.g. Some("user://myapp/conf/sqs.conf")
 * @param section      : Name of section in conf file for api key. e.g. Some("sqs")
 */
class AwsCloudFiles(bucket: String,
                    createBucket: Boolean,
                    path: String? = null,
                    section: String? = null)
    : CloudFilesBase(bucket, createBucket), AwsSupport {

    private val SOURCE = "aws:s3"
    private val _s3: AmazonS3Client = AwsFuncs.s3(path, section)


    /**
     * hook for any initialization
     */
    override fun init(): Unit {
        if (_createDefaultFolder) {
            _s3.createBucket(_defaultFolder)
        }
    }


    /**
     * creates a root folder/bucket with the supplied name.
     *
     * @param rootFolder
     */
    override fun createRootFolder(rootFolder: String): Unit {
        if (!rootFolder.isNullOrEmpty() && rootFolder != _defaultFolder) {
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
    override fun create(folder: String, name: String, content: String): Result<String> {
        return put("create", folder, name, content)
    }


    /**
     * updates a file with the supplied folder name, file name, and content
     *
     * @param folder
     * @param name
     * @param content
     */
    override fun update(folder: String, name: String, content: String): Result<String> {
        return put("update", folder, name, content)
    }


    /**
     * deletes a file with the supplied folder name, file name
     *
     * @param folder
     * @param name
     */
    override fun delete(folder: String, name: String): Result<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "delete", data = fullName, call = { ->
            _s3.deleteObject(_defaultFolder, fullName)
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
    override fun getAsText(folder: String, name: String): Result<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "getAsText", data = fullName, call = { ->

            val obj = _s3.getObject(GetObjectRequest(_defaultFolder, fullName))
            val content = toString(obj.getObjectContent())
            //val content = "simulating download of " + fullName
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
    override fun download(folder: String, name: String, localFolder: String): Result<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "download", data = fullName, call = { ->
            val content = getAsText(folder, name)
            val finalFolder = Uris.interpret(localFolder)
            val localFile = File(finalFolder, fullName)
            val localFileName = localFile.absolutePath
            File(localFileName).writeText(content.value ?: "")
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
    override fun downloadToFile(folder: String, name: String, filePath: String): Result<String> {
        val fullName = getName(folder, name)
        return executeResult<String>(SOURCE, "download", data = fullName, call = { ->
            val content = getAsText(folder, name)
            val localFile = Uris.interpret(filePath)
            val localFileName = localFile ?: name
            File(localFileName).writeText(content.value ?: "")
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
    fun put(action: String, folder: String, name: String, content: String): Result<String> {
        // full name of the file is folder + name
        val fullName = getName(folder, name)

        return executeResult<String>(SOURCE, action, data = fullName, call = { ->

            _s3.putObject(_defaultFolder, fullName, toInputStream(content), ObjectMetadata())
            fullName
        })
    }


    private fun getName(folder: String, name: String): String {
        // Case 1: no folder supplied, assume in root bucket
        return if (folder.isNullOrEmpty())
            name

        // Case 2: folder == root folder
        else if (folder == _defaultFolder)
            name

        // Case 3: sub-folder
        else
            folder + "-" + name
    }
}
