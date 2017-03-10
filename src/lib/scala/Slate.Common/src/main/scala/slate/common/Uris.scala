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

package slate.common

import java.io.File
import slate.common.Files._


object Uris {
  /**
   * Interprets the path URI to support references to various locations:
   * 1. user dir: user://{folder} ( user home directory for os  )
   * 2. temp dir: temp://{folder} ( temp files directory for os )
   * 3. file dir: file://{path}   ( absolution file location    )
   * @param uri
   * @return
   */
  def interpret(uri:String): Option[String] = {
    val pathParts = Strings.substring(uri, "://")
    pathParts.fold(Option(uri))( parts => {
      parts._1 match {
        case "user://" => Option(new File(System.getProperty("user.home"), parts._2).toString)
        case "temp://" => Option(new File(System.getProperty("java.io.tmpdir"), parts._2).toString)
        case "file://" => Option(new File(parts._2).toString)
        case _         => Option(uri)
      }
    })
  }


  /**
   * Reads the text file represented by uri after first interpreting the path.
   * 1. user dir: user://{folder} ( user home directory for os  )
   * 2. temp dir: temp://{folder} ( temp files directory for os )
   * 3. file dir: file://{path}   ( absolution file location    )
   * @param uri
   * @return
   */
  def readText(uri:String): Option[String] = {
    val pathParts = Strings.substring(uri, "://")
    pathParts.fold(Option(readAllText(new File(uri).toString)))( parts => {
      val userDir = System.getProperty("user.home")
      val tempDir = System.getProperty("java.io.tmpdir")
      parts._1 match {
        case "user://" => Option(readAllText(new File(userDir, parts._2).toString))
        case "temp://" => Option(readAllText(new File(tempDir, parts._2).toString))
        case "file://" => Option(readAllText(new File(parts._2).toString))
        case "http://" => Option(readAllText(http.HttpUtility.get(parts._2).toString))
        case _         => Option(readAllText(new File(parts._2).toString))
      }
    })
  }


  /**
   * Reads the text file represented by uri after first interpreting the path.
   * 1. user dir: user://{folder} ( user home directory for os  )
   * 2. temp dir: temp://{folder} ( temp files directory for os )
   * 3. file dir: file://{path}   ( absolution file location    )
   * @param uri
   * @return
   */
  def readDoc(uri:String): Option[Doc] = {
    val pathParts = Strings.substring(uri, "://")
    pathParts.fold(Option(buildDoc(new File(uri))))( parts => {
      val userDir = System.getProperty("user.home")
      val tempDir = System.getProperty("java.io.tmpdir")
      val content = parts._1 match {
        case "user://" => Option(buildDoc(new File(userDir, parts._2)))
        case "temp://" => Option(buildDoc(new File(tempDir, parts._2)))
        case "file://" => Option(buildDoc(new File(parts._2)))
        case "http://" => Option(new Doc(parts._2, readAllText(http.HttpUtility.get(parts._2)), "", 0))
        case _         => Option(buildDoc(new File(parts._2)))
      }
      content
    })
  }


  def buildDoc(file:File):Doc = {
    val content = readAllText(file.toString())
    new Doc(file.getName, content, getFileExtension(file), file.getTotalSpace)
  }
}
