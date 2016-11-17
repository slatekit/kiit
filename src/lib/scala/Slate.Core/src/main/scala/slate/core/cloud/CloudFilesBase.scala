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

import slate.common.Files

/**
  * Abstraction for cloud base file storage and retrieval.
  */
abstract class CloudFilesBase (val _defaultFolder:String, val _createDefaultFolder:Boolean) extends CloudActions {

  def connect(args:Any): Unit = { }


  def create(name:String, content:String): Unit =
  {
    create(_defaultFolder, name, content)
  }


  def createFromPath(name:String, filePath:String): Unit =
  {
    val content = loadFromFile(filePath)
    create(_defaultFolder, name, content)
  }


  def createFromPath(folder:String, name:String, filePath:String): Unit =
  {
    val content = loadFromFile(filePath)
    create(folder, name, content)
  }


  def delete(name:String): Unit =
  {
    delete(_defaultFolder, name)
  }


  def getAsText(name:String): String =
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


  def updateFromPath(name:String, filePath:String): Unit =
  {
    val content = loadFromFile(filePath)
    update(_defaultFolder, name, content)
  }


  def updateFromPath(folder:String, name:String, filePath:String): Unit =
  {
    val content = loadFromFile(filePath)
    update(folder, name, content)
  }


  def createRootFolder(rootFolder:String):Unit


  def create(folder:String, name:String, content:String):Unit


  def update(folder:String, name:String, content:String): Unit


  def delete(folder:String, name:String):Unit


  def getAsText(folder:String, name:String):String


  def download(folder:String, name:String, localFolder:String): Unit


  protected def loadFromFile(filePath:String):String =
  {
    Files.readAllText(filePath)
  }


  protected def toInputStream(content:String):InputStream =
  {
    new ByteArrayInputStream(content.getBytes())
  }


  protected def toString(input: InputStream): String =
  {
    val reader = new BufferedReader(new InputStreamReader(input))
    val buffer = new StringBuilder()
    var moreData = true
    while (moreData) {
      val line = reader.readLine()
      if (line == null)
      {
        moreData = false
      }
      else
      {
        buffer.append(line)
      }
    }
    val content = buffer.toString()
    content
  }
}
