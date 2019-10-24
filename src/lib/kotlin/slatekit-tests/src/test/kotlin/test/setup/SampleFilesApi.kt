package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.common.content.*


@Api(area = "samples", name = "files", desc = "sample api to test other features")
class SampleFiles3Api {

    @Action(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getContentCsv(): Content = Content.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getContentHtml(): Content = Content.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @Action(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getContentText(): Content = Content.text("user: kishore")


    @Action(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getContentXml(): Content = Content.xml("<user><name>kishore</name></user>")


    @Action(desc = "test getting Doc as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getDocCsv(): Doc = Doc.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting Doc as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun getDocHtml(): Doc = Doc.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @Action(desc = "test getting Doc as xml", roles = "", verb = "@parent", protocol = "@parent")
    fun getDocText(): Doc = Doc.text("file1.txt", "user: kishore")


    @Action(desc = "test getting Doc as xml", roles = "", verb = "@parent", protocol = "@parent")
    fun getDocXml(): Doc = Doc.xml("file1.xml", "<user><name>kishore</name></user>")
}
