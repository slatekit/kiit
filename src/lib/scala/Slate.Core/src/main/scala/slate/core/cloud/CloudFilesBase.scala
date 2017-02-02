/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.cloud

import java.io.{InputStreamReader, BufferedReader, InputStream, ByteArrayInputStream}

import slate.common.{IO, Result, Files}

/**
  * Abstraction for cloud base file storage and retrieval.
  */
abstract class CloudFilesBase (val _defaultFolder:String, val _createDefaultFolder:Boolean) extends CloudActions {

  def connect(args:Any): Unit = { }


  def connectWith(key:String, pass:String, tag:String):Unit = { }


  def create(name:String, content:String): Unit =
  {
    create(_defaultFolder, name, content)
  }


  def createFromPath(name:String, filePath:String): Unit =
  {
    val content = loadFromFile(filePath)
    create(_defaultFolder, name, content)
  }


  def createFromPath(folder:String, name:String, filePath:String): Result[String] =
  {
    //val content = "simulating from file : " + filePath
    val content = loadFromFile(filePath)
    create(folder, name, content)
  }


  def delete(name:String): Unit =
  {
    delete(_defaultFolder, name)
  }


  def getAsText(name:String): Result[String] =
  {
    getAsText(_defaultFolder, name)
  }


  def download(name:String, localFolder:String): Unit =
  {
    download(_defaultFolder, name, localFolder)
  }


  def update(name:String, content:String): Unit =
  {
    update(_defaultFolder, name, content)
  }


  def updateFromPath(name:String, filePath:String): Result[String] =
  {
    val content = "simulating from file : " + filePath; //loadFromFile(filePath)
    update(_defaultFolder, name, content)
  }


  def updateFromPath(folder:String, name:String, filePath:String): Result[String] =
  {
    val content = loadFromFile(filePath)
    update(folder, name, content)
  }


  def createRootFolder(rootFolder:String):Unit


  def create(folder:String, name:String, content:String):Result[String]


  def update(folder:String, name:String, content:String): Result[String]


  def delete(folder:String, name:String):Result[String]


  def getAsText(folder:String, name:String):Result[String]


  def download(folder:String, name:String, localFolder:String): Result[String]


  def downloadToFile(folder:String, name:String, filePath:String):Result[String]


  protected def loadFromFile(filePath:String):String =
  {
    Files.readAllText(filePath)
  }


  protected def toInputStream(content:String):InputStream =
  {
    new ByteArrayInputStream(content.getBytes())
  }


  protected def toString(input: InputStream): String = {
    scala.io.Source.fromInputStream(input).getLines().mkString
  }
}
