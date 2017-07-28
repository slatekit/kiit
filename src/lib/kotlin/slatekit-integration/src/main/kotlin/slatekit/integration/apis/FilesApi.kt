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

package slatekit.integration.apis


@slatekit.apis.Api(area = "infra", name = "files", desc = "api info about the application and host", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class FilesApi(val files: slatekit.core.cloud.CloudFilesBase, override val context: slatekit.core.common.AppContext) : slatekit.apis.support.ApiWithSupport {


    @ApiAction(desc = "creates the root folder/bucket",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun createRootFolder(rootFolder: String): Unit {
        return files.createRootFolder(rootFolder)
    }


    @ApiAction(desc = "creates a file with the supplied folder name, file name, and content",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun create(folder: String, name: String, content: String): Unit {
        files.create(folder, name, content)
    }


    @ApiAction(desc = "creates a file with the supplied folder name, file name, and content from file path",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun createFromPath(folder: String, name: String, filePath: String): slatekit.common.Result<String> {
        return files.createFromPath(folder, name, slatekit.common.Uris.interpret(filePath) ?: filePath)
    }


    @ApiAction(desc = "creates a file with the supplied folder name, file name, and content from doc",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun createFromDoc(folder: String, name: String, doc: slatekit.common.Doc): slatekit.common.Result<String> {
        return files.create(folder, name, doc.content)
    }


    @ApiAction(desc = "updates a file with the supplied folder name, file name, and content",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun update(folder: String, name: String, content: String): slatekit.common.Result<String> {
        return files.update(folder, name, content)
    }


    @ApiAction(desc = "updates a file with the supplied folder name, file name, and content from file path",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun updateFromPath(folder: String, name: String, filePath: String): slatekit.common.Result<String> {
        return files.updateFromPath(folder, name, interpretUri(filePath) ?: filePath)
    }


    @ApiAction(desc = "updates a file with the supplied folder name, file name, and content from doc",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun updateFromDoc(folder: String, name: String, doc: slatekit.common.Doc): slatekit.common.Result<String> {
        return files.updateFromPath(folder, name, doc.content)
    }


    @ApiAction(desc = "deletes a file with the supplied folder name, file name",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun delete(folder: String, name: String): slatekit.common.Result<String> {
        return files.delete(folder, name)
    }


    @ApiAction(desc = "downloads the file specified by folder and name to the local folder specified.",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun download(folder: String, name: String, localFolder: String, display: Boolean): slatekit.common.Result<String> {
        return show(files.download(folder, name, interpretUri(localFolder) ?: localFolder), display)
    }


    @ApiAction(desc = "downloads the file specified by folder and name, as text content to file supplied",
            roles = "@parent", verb = "@parent", protocol = "@parent")
    fun downloadToFile(folder: String, name: String, filePath: String, display: Boolean): slatekit.common.Result<String> {
        return show(files.downloadToFile(folder, name, slatekit.common.Uris.interpret(filePath) ?: filePath), display)
    }


    private fun show(result: slatekit.common.Result<String>, display: Boolean): slatekit.common.Result<String> {
        val path = result.value ?: ""
        val output = if (display) {
            val text = java.io.File(path).readText()
            "PATH   : " + path + slatekit.common.newline +
                    "CONTENT: " + text
        }
        else
            "PATH   : " + path
        return slatekit.common.results.ResultFuncs.successOrError(result.success, output, result.msg, result.tag, result.ref)
    }
}
