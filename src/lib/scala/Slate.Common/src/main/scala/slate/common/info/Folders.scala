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
package slate.common.info

import java.io.File
import java.nio.file.Path

import slate.common.Files
import slate.common.app.AppRunConst

/**
  * Represents folder locations for an application
  * Folders locations are organized by company/app and stored in the user home directory.
  * e.g.
  *
  * - user                   ( e.g /usr/kreddy or c:/users/kreddy )
  *   - rootOrCompany        ( ? name of company or name of root folder )
 *                           ( recommended to use root folder containing 1 or more apps )
  *     - group              ( ? parent folder for all apps for group )
  *       - product1.console ( individual apps go here )
  *       - product1.shell
  *       - product1.server
  *         - conf
  *         - cache
  *         - logs
  *         - inputs
  *         - outputs
  *
  * @param location: location of where the folders reside ( local (to app) | programs | user.home )
  * @param root    : optional name of root folder or company name
  * @param group   : optional name of group folder that holds all apps
  * @param app     : name of the application folder for this app
  * @param cache   : name of cache folder for the application
  * @param inputs  : name of input folder for the application
  * @param logs    : name of logs folder for the application
  * @param outputs : name of output folder for the application
  */
case class Folders(
                    location : Int    = 0,
                    root     : Option[String] = None,
                    group    : Option[String] = None,
                    app      : String = "app",
                    cache    : String = "cache",
                    inputs   : String = "input",
                    logs     : String = "logs",
                    outputs  : String = "output",
                    conf     : String = "conf"
                  )
{

  def log( callback:(String,String) => Unit) : Unit = {
    callback("root"    , root.getOrElse("")    )
    callback("group"   , group.getOrElse("")   )
    callback("app"     , app                   )
    callback("cache"   , cache                 )
    callback("inputs"  , app                   )
    callback("logs"    , logs                  )
    callback("outputs" , outputs               )
  }


  def pathToCache   :String = { this.pathToApp + File.separator + s"${cache}"   }
  def pathToInputs  :String = { this.pathToApp + File.separator + s"${inputs}"  }
  def pathToLogs    :String = { this.pathToApp + File.separator + s"${logs}"    }
  def pathToOutputs :String = { this.pathToApp + File.separator + s"${outputs}" }



  def buildPath(part:String):String = {
    val userHome = System.getProperty("user.home")
    val path = userHome + File.separator +
               root     + File.separator +
               group    + File.separator +
               part.replaceAllLiterally(" ", "")
    path
  }


  def pathToApp(): String = {
    val sep = File.separator
    var finalPath = System.getProperty("user.home")
    finalPath = root.fold[String](finalPath)( folder => finalPath + sep + folder )
    finalPath = group.fold[String](finalPath)( folder => finalPath +sep + folder )
    finalPath = finalPath + sep + app
    finalPath
  }


  def create(): String = {
    val userHome = System.getProperty("user.home")

    var path = Files.mkDir(userHome, root)
    path     = Files.mkDir(path, group)
    path     = Files.mkDir(path, app)
    Files.mkDir(path, cache)
    Files.mkDir(path, inputs)
    Files.mkDir(path, logs)
    Files.mkDir(path, outputs)
    Files.mkDir(path, conf)
  }
}


object Folders {

  val none = new Folders(
    root    = None,
    group   = None,
    app     = "",
    cache   = "",
    inputs  = "",
    logs    = "",
    outputs = ""
  )


  val default = new Folders(
    root    = Some("slatekit"),
    group   = Some("samples" ),
    app     = "app"     ,
    cache   = "cache"   ,
    inputs  = "input"   ,
    logs    = "logs"    ,
    outputs = "output"  ,
    conf    = "conf"
  )
}