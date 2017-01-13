/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.integration

import slate.common.{Doc, Result, Strings, Files}
import slate.common.Files._
import slate.core.apis.{ApiAction, Api}
import slate.core.cloud.CloudFilesBase
import slate.core.common.svcs.ApiWithSupport


@Api(area = "infra", name = "files", desc = "api info about the application and host", roles= "admin", auth="key-roles", verb = "post", protocol = "*")
class FilesApi(val files:CloudFilesBase) extends ApiWithSupport {


  @ApiAction(name = "", desc= "connect to the datasource", roles= "@parent", verb = "@parent", protocol = "@parent")
  def connectWith(key:String, password:String, tag:String):Unit =
  {
    files.connectWith(key, password, tag)
  }


  @ApiAction(name = "", desc= "creates the root folder/bucket",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def createRootFolder(rootFolder:String):Unit = {
    files.createRootFolder(rootFolder)
  }


  @ApiAction(name = "", desc= "creates a file with the supplied folder name, file name, and content",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def create(folder:String, name:String, content:String):Unit = {
    files.create(folder, name, content)
  }


  @ApiAction(name = "", desc= "creates a file with the supplied folder name, file name, and content from file path",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def createFromPath(folder:String, name:String, filePath:String): Result[String] = {
    files.createFromPath(folder, name, interpretUri(filePath).getOrElse(filePath))
  }


  @ApiAction(name = "", desc= "creates a file with the supplied folder name, file name, and content from doc",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def createFromDoc(folder:String, name:String, doc:Doc): Result[String] = {
    files.create(folder, name, doc.content)
  }


  @ApiAction(name = "", desc= "updates a file with the supplied folder name, file name, and content",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def update(folder:String, name:String, content:String): Result[String] = {
    files.update(folder, name, content)
  }


  @ApiAction(name = "", desc= "updates a file with the supplied folder name, file name, and content from file path",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def updateFromPath(folder:String, name:String, filePath:String): Result[String] =
  {
    files.updateFromPath(folder, name, interpretUri(filePath).getOrElse(filePath))
  }


  @ApiAction(name = "", desc= "updates a file with the supplied folder name, file name, and content from doc",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def updateFromDoc(folder:String, name:String, doc:Doc): Result[String] =
  {
    files.updateFromPath(folder, name, doc.content)
  }


  @ApiAction(name = "", desc= "deletes a file with the supplied folder name, file name",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def delete(folder:String, name:String):Result[String] = {
    files.delete(folder, name)
  }


  @ApiAction(name = "", desc= "downloads the file specified by folder and name to the local folder specified.",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def download(folder:String, name:String, localFolder:String, display:Boolean): Result[String] = {
    show(files.download(folder, name, interpretUri(localFolder).getOrElse(localFolder)), display)
  }


  @ApiAction(name = "", desc= "downloads the file specified by folder and name, as text content to file supplied",
    roles= "@parent", verb = "@parent", protocol = "@parent")
  def downloadToFile(folder:String, name:String, filePath:String, display:Boolean):Result[String] = {
    show(files.downloadToFile(folder, name, interpretUri(filePath).getOrElse(filePath)), display)
  }


  private def show(result:Result[String], display:Boolean): Result[String]  = {
    val path = result.getOrElse("")
    val output = if(display){
      val text = Files.readAllText(path)
      "PATH   : " + path + Strings.newline() +
      "CONTENT: " + text
    }
    else
      "PATH   : " + path
    successOrError[String](result.success, output, result.msg, result.tag, result.ref)
  }
}
