package slatekit.sampleapp.core.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Verbs
import slatekit.common.Content
import slatekit.common.Doc
import slatekit.common.Request
import slatekit.common.RequestSupport
import slatekit.common.auth.Roles


@Api(area = "samples", name = "files", desc = "sample api to test other features", auth = AuthModes.token, verb = Verbs.auto, roles = Roles.all)
class SampleFiles3Api {

    @ApiAction(desc = "test getting content as xml")
    fun getContentCsv(): Content = Content.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @ApiAction(desc = "test getting content as xml")
    fun getContentHtml(): Content = Content.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @ApiAction(desc = "test getting content as xml")
    fun getContentText(): Content = Content.text("user: kishore")


    @ApiAction(desc = "test getting content as xml")
    fun getContentXml(): Content = Content.xml("<user><name>kishore</name></user>")


    @ApiAction(desc = "test getting Doc as xml")
    fun getDocCsv(): Doc = Doc.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @ApiAction(desc = "test getting Doc as xml")
    fun getDocHtml(): Doc = Doc.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @ApiAction(desc = "test getting Doc as xml", roles = "")
    fun getDocText(): Doc = Doc.text("file1.txt", "user: kishore")


    @ApiAction(desc = "test getting Doc as xml", roles = "", verb = "@parent", protocol = "@parent")
    fun getDocXml(): Doc = Doc.xml("file1.xml", "<user><name>kishore</name></user>")

    @ApiAction(desc = "test uploading a file", roles = "", verb = "post", protocol = "@parent")
    fun uploadFile(req: Request): Doc {
        val raw = req.raw as? RequestSupport
        val doc = raw?.let { r ->
            val uploaded = r.getDoc("uploaded_file")
            uploaded
        } ?: Doc.text("empty.txt","unable to get document")
        return doc
    }
}
