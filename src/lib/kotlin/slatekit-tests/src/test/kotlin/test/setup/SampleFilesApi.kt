package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.common.types.Content
import slatekit.common.types.ContentFile


@Api(area = "samples", name = "files", desc = "sample api to test other features")
class SampleFiles3Api {

    @Action(desc = "test getting content as xml")
    fun getContentCsv(): Content = Content.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting content as xml")
    fun getContentHtml(): Content = Content.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @Action(desc = "test getting content as xml")
    fun getContentText(): Content = Content.text("user: kishore")


    @Action(desc = "test getting content as xml")
    fun getContentXml(): Content = Content.xml("<user><name>kishore</name></user>")


    @Action(desc = "test getting Doc as xml")
    fun getDocCsv(): ContentFile = ContentFile.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting Doc as xml")
    fun getDocHtml(): ContentFile = ContentFile.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @Action(desc = "test getting Doc as xml")
    fun getDocText(): ContentFile = ContentFile.text("file1.txt", "user: kishore")
}
