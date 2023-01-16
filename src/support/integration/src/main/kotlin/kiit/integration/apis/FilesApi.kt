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

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.common.Sources
import kiit.common.types.ContentFile
import kiit.common.crypto.Encryptor
import kiit.common.io.Uris
import kiit.common.log.Logger
import kiit.context.Context
import kiit.core.files.CloudFiles
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try
import kiit.results.getOrElse

@Api(area = "cloud", name = "files", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class FilesApi(val files: CloudFiles, override val context: Context) : kiit.apis.support.FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    @Action(desc = "creates the root folder/bucket")
    suspend fun createRootFolder(rootFolder: String):Try<String> {
        return files.createRootFolder(rootFolder)
    }

    @Action(desc = "creates a file with the supplied folder name, file name, and content")
    suspend fun create(folder: String, name: String, content: String) {
        files.create(folder, name, content)
    }

    @Action(desc = "creates a file with the supplied folder name, file name, and content from file path")
    suspend fun createFromPath(folder: String, name: String, filePath: String): Try<String> {
        return files.createFromPath(folder, name, Uris.interpret(filePath) ?: filePath)
    }

    @Action(desc = "creates a file with the supplied folder name, file name, and content from doc")
    suspend fun createFromDoc(folder: String, name: String, doc: ContentFile): Try<String> {
        return files.create(folder, name, doc.data)
    }

    @Action(desc = "updates a file with the supplied folder name, file name, and content")
    suspend fun update(folder: String, name: String, content: String): Try<String> {
        return files.update(folder, name, content)
    }

    @Action(desc = "updates a file with the supplied folder name, file name, and content from file path")
    suspend fun updateFromPath(folder: String, name: String, filePath: String): Try<String> {
        return files.updateFromPath(folder, name, interpretUri(filePath) ?: filePath)
    }

    @Action(desc = "updates a file with the supplied folder name, file name, and content from doc")
    suspend fun updateFromDoc(folder: String, name: String, doc: ContentFile): Try<String> {
        return files.updateFromPath(folder, name, String(doc.data))
    }

    @Action(desc = "deletes a file with the supplied folder name, file name")
    suspend fun delete(folder: String, name: String): Try<String> {
        return files.delete(folder, name)
    }

    @Action(desc = "get file as text")
    suspend fun getFileText(folder: String, name: String):Try<String> {
        return files.getFileText(folder, name)
    }

    @Action(desc = "downloads the file specified by folder and name to the local folder specified.")
    suspend fun download(folder: String, name: String, localFolder: String, display: Boolean): Try<String> {
        return show(files.download(folder, name, interpretUri(localFolder) ?: localFolder), display)
    }

    @Action(desc = "downloads the file specified by folder and name, as text content to file supplied")
    suspend fun downloadToFile(folder: String, name: String, filePath: String, display: Boolean): Try<String> {
        return show(files.downloadToFile(folder, name, Uris.interpret(filePath) ?: filePath), display)
    }

    private fun show(result: Try<String>, display: Boolean): Try<String> {
        val path = result.getOrElse { "" }
        val output = if (display) {
            val text = java.io.File(path).readText()
            "PATH   : " + path + kiit.common.newline +
                    "CONTENT: " + text
        } else
            "PATH   : " + path
        return result.fold( { Success(output) }, { Failure(result.desc) }).toTry()
    }
}
