/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
*/

package slate.common


import java.io.{File, BufferedWriter, FileWriter}
import slate.common.conf.Config
import slate.common.databases.DbConString
import slate.common.encrypt.Encryptor

import scala.io.BufferedSource
import scala.collection.mutable.{ListBuffer, Map}


object Files {


  def readAllText(path:String):String =
  {
    var content = ""
    read(path, (res) => content = res.mkString)
    content
  }


  def readAllTextFromUserFolder(directory:String, fileName:String):String =
  {
    val file = loadUserAppFile(directory, fileName)
    readAllText(file.getAbsolutePath)
  }


  def readAllLines(path:String):List[String] =
  {
    val lines = new ListBuffer[String]()
    read(path, (res) =>
    {
      for(line <- res.getLines())
        lines.append(line)
    })
    lines.toList
  }


  def readAllLinesFromUserFolder(directory:String, fileName:String):List[String] =
  {
    val file = loadUserAppFile(directory, fileName)
    readAllLines(file.getAbsolutePath)
  }


  def readConfigFromUserFolder(directory:String, fileName:String):Option[Config] =
  {
    val file = loadUserAppFile(directory, fileName)
    val lines = readAllLines(file.getAbsolutePath)
    if(lines == null || lines.size == 0)
      return None

    val config = new Config(Map[String,String]())
    for(line <- lines )
    {
      var lineClean = line.trim
      if(!lineClean.startsWith("#")) {
        val ndxColon = line.indexOf(":")
        if (ndxColon > 0) {
          val key = line.substring(0, ndxColon)
          val value = line.substring(ndxColon + 1)
          config(key) = value.trim
        }
      }
    }
    Some(config)
  }


  def read(path:String, callback:(BufferedSource) => Unit):Unit =
  {
    var res:BufferedSource = null
    try {
      res = scala.io.Source.fromFile(path)
      callback(res)
    }
    finally {
      if (res != null) {
        res.close()
      }
    }
  }


  def writeAllText(path:String, content:String):Unit =
  {
    write(path, (bw) => bw.write(content))
  }


  def writeAllLines(path:String, lines: List[String]):Unit =
  {
    write(path, (bw) =>
    {
      for(line <- lines)
        bw.write(line + Strings.newline())
    })
  }


  def write(path:String,  callback:(BufferedWriter) => Unit):Unit =
  {
    var bw:BufferedWriter = null
    try {
      val file = new File(path)
      bw = new BufferedWriter(new FileWriter(file))
      callback(bw)
    }
    finally {
      if (bw != null) {
        bw.close()
      }
    }
  }


  /**
   * writes a file to a date based directory inside an app directory inside the users home directory
   *
   * e.g. {user.home}/{appName}/{directory}/{date-today}/{datetime}.txt
   *      c:/users/kreddy/myapp/logs/2016-03-20/2016-03-20-09-30-45.txt
   *
   * @param directory : The name of the sub directory in the app
   * @param content   : The content to write
   * @return
   */
  def writeFileForDateAsTimeStamp(directory:String, content:String):String =
  {
    val fileName = DateTime.now().toStringLong().replaceAllLiterally(":", "-")
      .replaceAllLiterally(" ", "-") + ".txt"
    writeDatedFile(directory, fileName, content)
  }


  /**
    * writes a file to a date based directory inside an app directory inside the users home directory
    *
    * e.g. {user.home}/{appName}/{directory}/{date-today}/{datetime}.txt
    *      c:/users/kreddy/myapp/logs/2016-03-20/2016-03-20-09-30-45.txt
    *
    * @param directory : The name of the sub directory in the app
    * @param content   : The content to write
    * @return
    */
  def writeDatedFile(directory:String, fileName:String, content:String)
    :String =
  {
    val dirInfo = createDirectoryForDate(directory)
    val dirName = dirInfo._1
    val dirPath = dirInfo._2
    val filePath = dirPath + File.separator + fileName
    writeAllText(filePath, content)
    filePath
  }


  def getFolderNameForDate():String = {
    val now = DateTime.now()
    now.toStringNumeric()
  }


  /**
   * creates a folder inside the app directory of the user home path
   * e.g. {user.home}/{appName}
   *      c:/users/kreddy/myapp/logs
 *
   * @param dir   : The name of the app directory
   * @return
   */
  def mkUserDir(dir:String):String =
  {
    val userHome = System.getProperty("user.home")
    Ensure.isNotNull(userHome, "Unable to load user directory from 'user.home' system property")
    mkDir(userHome, dir)
  }


  def mkDir(parent:String, child:String):String = {
    val dir = new File(parent, child)
    if (!dir.exists() )
    {
      dir.mkdir()
    }
    dir.getAbsolutePath
  }


  def mkDir(parent:String, child:Option[String]):String = {
    child.fold[String](parent)( folder => mkDir(parent, folder))
  }


  /**
   * creates a subdirectory inside the app directory of the user home path
   * e.g. {user.home}/{appName}/{directory}
   *      c:/users/kreddy/myapp/logs
 *
   * @param appName   : The name of the app directory
   * @param directory : The name of the sub directory in the app
   * @return
   */
  def createUserAppSubDirectory(appName:String, directory:String):(String,String) =
  {
    mkUserDir(appName)

    val userHome = System.getProperty("user.home")
    val appDirPath = userHome + File.separator + appName
    val baseDir = new File(appDirPath, directory)
    if(!baseDir.exists())
    {
      baseDir.mkdir()
    }
    (baseDir.getName, baseDir.getAbsolutePath)
  }


  /**
   * creates a folder inside the app directory of the user home path
   * e.g. {user.home}/{appName}/{directory}/{date}/
   *      c:/users/kreddy/myapp/logs/2016-03-20/
 *
   * @param directory : The name of the sub directory in the app
   * @return
   */
  def createDirectoryForDate(directory:String):(String,String) =
  {
    // Now create {user.home}/{appName}/{directory}/{date}
    val dateName = getFolderNameForDate()
    val dateDir = new File(directory, dateName)
    if(!dateDir.exists())
    {
      dateDir.mkdir()
    }
    (dateDir.getName, dateDir.getAbsolutePath)
  }


  def existsInUserDir(directory:String, fileName:String): Boolean = {
    val userHome = System.getProperty("user.home")
    if(Strings.isNullOrEmpty(userHome)) {
      return false
    }
    val dir = new File(userHome, directory)
    val file   = new File(dir, fileName)
    val isFound   = file.exists() && file.isFile()
    isFound
  }


  def loadUserAppFile(appName:String, fileName:String):File =
  {
    val userHome = System.getProperty("user.home")
    Ensure.isNotNull(userHome, "Unable to load user directory from 'user.home' system property")

    val dir = new File(userHome, appName)
    val file   = new File(dir, fileName)
    val isFound   = file.exists() && file.isFile()
    if (!isFound) {
      val msg = s"Unable to load file from user directory: $appName, $fileName"
      throw new IllegalArgumentException(msg)
    }
    file
  }


  def loadUserAppFile(rootFolder:String, appFolder:String, fileName:String):File =
  {
    val userHome = System.getProperty("user.home")
    Ensure.isNotNull(userHome, "Unable to load user directory from 'user.home' system property")

    val root = new File(userHome, rootFolder)
    val app  = new File(root, appFolder)
    val file = new File(app, fileName)
    val isFound   = file.exists() && file.isFile()
    if (!isFound) {
      val msg = s"Unable to load file from user directory: $rootFolder, $fileName"
      throw new IllegalArgumentException(msg)
    }
    file
  }


  def loadUserAppDirectory(appName:String, directory:String):File =
  {
    val userHome = System.getProperty("user.home")
    if (userHome == null) {
      throw new IllegalArgumentException("Unable to load file from user directory: "
        + "'user.home' System property is not set.")
    }

    val appDir = appName + File.separator + directory
    val dir = new File(userHome, appDir)
    val isFound   = dir.exists() && dir.isDirectory
    if (!isFound) {
      throw new IllegalArgumentException(s"Unable to load file from user directory: $appDir")
    }
    dir
  }


  def loadUserAppDirectoryPath(appName:String, directory:String):String = {
    loadUserAppDirectory(appName, directory).getAbsolutePath
  }
}
