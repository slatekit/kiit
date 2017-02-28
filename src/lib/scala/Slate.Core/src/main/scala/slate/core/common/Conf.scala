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
package slate.core.common


import java.io.File

import com.typesafe.config._
import slate.common._
import slate.common.results.ResultSupportIn
import slate.common.conf.{Configs, ConfigBase}
import slate.common.databases.{DbCon, DbConString}
import slate.common.encrypt.Encryptor

/**
 *  Conf is a wrapper around the typesafe config with additional support for:
 *
      *1. RESOURCES
        *Use a prefix for indicating where to load config from.
        *"jars://"  for the resources directory in the jar.
        *"user://" for the user.home directory.
        *"file://" for an explicit path to the file
        *"file://" for relative path to the file from working directory

        *Examples:
          *- jar://env.qa.conf
          *- user://${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
          *- file://c:/slatekit/${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
          *- file://./conf/env.qa.conf


      *2. LOADING
        *Use a short-hand syntax for specifying the files to merge and fallback with
        *Examples:
          *- primary
          *- primary, fallback1
          *- primary, fallback1, fallback2


      *3. FUNCTIONS:
        *Use functions in the config to resolve values dynamically.
        *You can use the resolveString method to resolve the value dynamically.
        *Note: This uses the slate.common.subs component.
        *Examples: ( props inside your .conf file )
          *- tag      : "@{today.yyyymmdd-hhmmss}"


      *4. DEFAULTS:
        *Use optional default values for getting strings, int, doubles, bools.
        *Examples:
          *- getStringOrElse
          *- getIntOrElse
          *- getBoolOrElse
          *- getDoubleOrElse
          *- getDateOrElse


      *5. MAPPING
        *Map slatekit objects automatically from the conf settings. Built in mappers for the
        *Examples:
          *- "env"   : Environment selection ( dev, qa, stg, prod ) etc.
          *- "login" : slate.common.Credentials object
          *- "db"    : database connection strings
          *- "api"   : standardized api keys
 *

 * @param fileName
 * @param enc
 * @param config
 */
class Conf( fileName:Option[String] = None,
            enc:Option[Encryptor] = None,
            config:Option[Config] = None
          )
  extends ConfigBase(enc) {


  private val _config:Config = config.getOrElse(
    Conf.loadTypeSafeConfig(fileName)
  )


  override def raw:Any = _config


  /// <summary>
  override def getValue(key: String): AnyVal = {
    val cfval = _config.getAnyRef(key)
    if (cfval.isInstanceOf[Integer]){
      cfval.asInstanceOf[Integer].intValue()
    }
    else if (cfval.isInstanceOf[Double]){
      cfval.asInstanceOf[Double]
    }
    else if (cfval.isInstanceOf[Boolean]){
      cfval.asInstanceOf[Boolean]
    }
    else
      0
  }


  /// <summary>
  override def containsKey(key: String): Boolean = {
    _config.hasPath(key)
  }


  /// <summary>
  override def getObject(key: String): AnyRef = {
    _config.getAnyRef(key)
  }


  override def size(): Int = {
    // Todo: can not get size from typesafe config
    // Maybe not even needed ?!!
    1000
  }


  override def loadFrom(file:Option[String]): Option[ConfigBase] = {
    Some(Conf.load(file, enc))
  }
}


object Conf extends ResultSupportIn {


  /**
    * loads the db info from the location specified
    *
    * @param fileName    : the name of the file e.g "db-local.conf"
    * @param enc         : the encryptor for decrypting info from the settings
    * @param sectionName : the name of the section in the file representing the settings
    * @return
    */
  def readDbCon(fileName:Option[String] = None, enc:Option[Encryptor] = None,
                sectionName:Option[String] = Some("db")) : Option[DbCon] = {
    load(fileName, enc).dbCon(sectionName.getOrElse("db"))
  }


  /**
    * loads the login info from the location specified
    *
    * @param fileName    : the name of the file e.g "login.conf"
    * @param enc         : the encryptor for decrypting info from the settings
    * @param sectionName : the name of the section in the file representing the settings
    * @return
    */
  def readLogin(fileName:Option[String] = None, enc:Option[Encryptor] = None,
                sectionName:Option[String] = Some("login")) : Option[Credentials] = {
    load(fileName, enc).login()
  }


  /**
    * loads the api key info from the location specified
    *
    * @param fileName    : the name of the file e.g "aws.conf"
    * @param sectionName : the name of the section in the file representing the settings
    * @param enc         : the encryptor for decrypting info from the settings
    * @return
    */
  def readApiKey(fileName:Option[String] = None, enc:Option[Encryptor] = None,
                 sectionName:Option[String] = Some("api")) : Option[ApiCredentials] = {
    load(fileName, enc).apiKey( sectionName.getOrElse("api") )
  }


  /**
    * Loads a config file using the source/location supplied.
    *
    * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
    * @param enc         : the encryptor for decrypting config settings.
    * @return
    */
  def load( fileName:Option[String] = None, enc:Option[Encryptor] = None ): ConfigBase = {
    new Conf(fileName, enc)
  }


  /**
    *
    * @param primaryFilePath
    * @param parentFilePath
    * @return
    */
  def loadWithFallback( primaryFilePath: String,
                        parentFilePath : String,
                        enc:Option[Encryptor] = None): ConfigBase = {

     val parent =  load(Some(parentFilePath ),  enc)
     loadWithFallbackConfig(Some(primaryFilePath), parent, enc)
  }


  /**
   * loads the config with primary and the parent
   *
   * @return
   */
  def loadWithFallbackConfig(fileName:Option[String],
                       parent:ConfigBase,
                       enc:Option[Encryptor] = None): ConfigBase = {

    val primaryRaw = loadTypeSafeConfig(fileName)
    val parentRaw = parent.raw.asInstanceOf[Config]
    val confRaw = primaryRaw.withFallback(parentRaw).resolve()
    val conf = new Conf(fileName, enc, Some(confRaw))
    conf
  }


  /**
    * Loads a config file from the resources directory
    *
    * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
    * @param enc         : the encryptor for decrypting config settings.
    * @return
    */
  def loadFromJars( fileName:String, enc:Option[Encryptor] = None ): ConfigBase = {
    new Conf(Some("jars://" + fileName), enc)
  }


  /**
    * Loads a config file from the app config directory in the user directory
    * e.g. user/company/apps/app/conf
    *
    * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
    * @param enc         : the encryptor for decrypting config settings.
    * @return
    */
  def loadFromUserHome( fileName:String, enc:Option[Encryptor] = None ): ConfigBase = {
    new Conf(Some("user://" + fileName), enc)
  }


  /**
    * Loads a config file using the source/location supplied.
    *
    * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
    * @param enc         : the encryptor for decrypting config settings.
    * @return
    */
  def loadFromFile( fileName:String, enc:Option[Encryptor] = None ): ConfigBase = {
    new Conf(Some("file://" + fileName), enc)
  }


  /**
    * Loads the typesafe config from the filename can be prefixed with a uri to indicate location,
    * such as:
    * 1. "jars://" to indicate loading from resources directory inside jar
    * 2. "user://" to indicate loading from user.home directory
    * 3. "file://" to indicate loading from file system
    *
    * e.g.
    *  - jars://env.qa.conf
    *  - user://${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
    *  - file://c:/slatekit/${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
    *  - file://./conf/env.qa.conf
    *
    * @param fileName    : name of file e.g. email.conf
    * @return
    */
  def loadTypeSafeConfig(fileName:Option[String]): Config = {

    // This is here to debug loading app conf
    val getDefaultConf = (name:Option[String]) => {
      val finalName = name.getOrElse("application.conf")
      val defaultConf = ConfigFactory.load(finalName)
      defaultConf
    }

    // CASE 1: No name supplied ( default to application.conf )
    fileName.fold[Config]( getDefaultConf(None) )( name => {

      // Check for uri : ( "jar://" | "user://" | "file://" )
      val parts = Strings.substring(name, "://")

      // CASE 2: No uri supplied ( use just the name of the file
      parts.fold[Config]( getDefaultConf(Option(name)) ) ( parts => {

        // CASE 3: URI Supplied
        val filePath = new File(System.getProperty("user.home"), parts._2)
        parts._1 match {
          case "jars://" => ConfigFactory.load( parts._2 )
          case "user://" => ConfigFactory.parseFile(new File(System.getProperty("user.home"), parts._2))
          case "file://" => ConfigFactory.parseFile(new File(parts._2))
          case _         => ConfigFactory.load( parts._2 )
        }
      })
    })
  }
}


