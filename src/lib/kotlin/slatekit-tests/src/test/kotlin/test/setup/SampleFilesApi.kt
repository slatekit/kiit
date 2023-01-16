package test.setup

import kiit.apis.Api
import kiit.apis.Action
import kiit.common.types.Content
import kiit.common.types.ContentFile
import kiit.common.types.ContentFiles
import kiit.common.types.Contents


@Api(area = "samples", name = "files", desc = "sample api to test other features")
class SampleFiles3Api {

    @Action(desc = "test getting content as xml")
    fun getContentCsv(): Content = Contents.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting content as xml")
    fun getContentHtml(): Content = Contents.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @Action(desc = "test getting content as xml")
    fun getContentText(): Content = Contents.text("user: kishore")


    @Action(desc = "test getting content as xml")
    fun getContentXml(): Content = Contents.xml("<user><name>kishore</name></user>")


    @Action(desc = "test getting Doc as xml")
    fun getDocCsv(): ContentFile = ContentFiles.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting Doc as xml")
    fun getDocHtml(): ContentFile = ContentFiles.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @Action(desc = "test getting Doc as xml")
    fun getDocText(): ContentFile = ContentFiles.text("file1.txt", "user: kishore")
}
