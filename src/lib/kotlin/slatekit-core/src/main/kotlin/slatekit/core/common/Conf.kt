/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package slatekit.core.common

/*
import java.io.File

import com.typesafe.config.*
import slatekit.common.*
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbCon
import slatekit.common.encrypt.Encryptor

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
class Conf(fileName:String?  = null,
           enc: Encryptor?   = null,
           config:Config?    = null
          )
  : ConfigBase( { raw -> enc?.encrypt(raw) ?: raw } ) {

  private val _fileName = fileName
  private val _enc = enc

  /**
    * Get or load the config object
    */
  private val _config:Config = config ?:  Conf.loadTypeSafeConfig(fileName)


  override fun get(key: String) : Any?             = if (containsKey(key)) get(key) else null
  override fun getObject(key: String): Any?        = if (containsKey(key)) get(key) else null
  override fun containsKey(key: String): Boolean   = _config.hasPath(key)
  override fun size(): Int                         = _config.entrySet().size
  
  
  override fun getString   (key: String) : String   = InputFuncs.decrypt(_config.getString(key), _encryptor)
  override fun getDate     (key: String) : DateTime = InputFuncs.convertDate(_config.getString(key))
  override fun getShort    (key: String) : Short    = _config.getInt(key).toShort()
  override fun getBool     (key: String) : Boolean  = _config.getBoolean(key)
  override fun getInt      (key: String) : Int      = _config.getInt(key)
  override fun getLong     (key: String) : Long     = _config.getLong(key)
  override fun getDouble   (key: String) : Double   = _config.getDouble(key)
  override fun getFloat    (key: String) : Float    = _config.getDouble(key).toFloat()


  /**
    * The reference to the raw underlying config ( TypeSafe config )
    *
    * @return
    */
  override val rawConfig:Any = _config


  /**
   * The origin file path of the config
   * @return
   */
  override fun origin():String = _fileName ?: _config.origin().filename()


  /**
   * Loads config from the file path supplied
   *
   * @param file
   * @return
   */
  override fun loadFrom(file:String?): ConfigBase? = Confs.load(file, _enc)





  companion object Confs  {


    /**
     * loads the db info from the location specified
     *
     * @param fileName    : the name of the file e.g "db-local.conf"
     * @param enc         : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readDbCon(fileName:String? = null, enc: Encryptor? = null,
                  sectionName:String? = "db") :  DbCon? =
            load(fileName, enc).dbCon(sectionName ?: "db" )



    /**
     * loads the login info from the location specified
     *
     * @param fileName    : the name of the file e.g "login.conf"
     * @param enc         : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readLogin(fileName:String? = null, enc: Encryptor? = null,
                  sectionName:String? = "login") : Credentials? =
            load(fileName, enc).login()



    /**
     * loads the api key info from the location specified
     *
     * @param fileName    : the name of the file e.g "aws.conf"
     * @param sectionName : the name of the section in the file representing the settings
     * @param enc         : the encryptor for decrypting info from the settings
     * @return
     */
    fun readApiKey(fileName:String? = null, enc: Encryptor? = null,
                   sectionName:String? = "api") : ApiLogin? =
            load(fileName, enc).apiKey( sectionName ?: "api" )



    /**
     * Loads a config file using the source/location supplied.
     *
     * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
     * @param enc         : the encryptor for decrypting config settings.
     * @return
     */
    fun load( fileName:String? = null, enc: Encryptor? = null ): ConfigBase =
            Conf(fileName, enc)



    /**
     *
     * @param primaryFilePath
     * @param parentFilePath
     * @return
     */
    fun loadWithFallback( primaryFilePath: String,
                          parentFilePath : String,
                          enc: Encryptor? = null): ConfigBase {

      val parent =  load(parentFilePath,  enc)
      return loadWithFallbackConfig(primaryFilePath, parent, enc)
    }


    /**
     * loads the config with primary and the parent
     *
     * @return
     */
    fun loadWithFallbackConfig(fileName:String?,
                               parent: ConfigBase,
                               enc: Encryptor? = null): ConfigBase {

      val primaryRaw = loadTypeSafeConfig(fileName)
      val parentRaw = parent.rawConfig as Config
      val confRaw = primaryRaw.withFallback(parentRaw).resolve()
      val conf = Conf(fileName, enc, confRaw)
      return conf
    }


    /**
     * Loads a config file from the resources directory
     *
     * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
     * @param enc         : the encryptor for decrypting config settings.
     * @return
     */
    fun loadFromJars( fileName:String, enc: Encryptor? = null ): ConfigBase =
            Conf("jars://" + fileName, enc)


    /**
     * Loads a config file from the app config directory in the user directory
     * e.g. user/company/apps/app/conf
     *
     * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
     * @param enc         : the encryptor for decrypting config settings.
     * @return
     */
    fun loadFromUserHome( fileName:String, enc: Encryptor? = null ): ConfigBase =
            Conf("user://" + fileName, enc)


    /**
     * Loads a config file using the source/location supplied.
     *
     * @param fileName    : name of file e.g. email.conf ( defaults to "application.conf" )
     * @param enc         : the encryptor for decrypting config settings.
     * @return
     */
    fun loadFromFile( fileName:String, enc: Encryptor? = null ): ConfigBase =
            Conf("file://" + fileName, enc)


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
    fun loadTypeSafeConfig(fileName:String?): Config {

      // This is here to debug loading app conf
      val getDefaultConf = { name:String? ->
        val finalName = name ?: "application.conf"
        val defaultConf = ConfigFactory.load(finalName)
        defaultConf
      }

      // No name supplied ( default to application.conf )
      val config = fileName?.let { name ->

        // Check for uri : ( "jar://" | "user://" | "file://" )
        val parts = Strings.substring(name, "://")

        parts?.let { (uri, path) ->
          when ( uri ) {
            "jars://" -> ConfigFactory.load( path )
            "user://" -> ConfigFactory.parseFile(File(System.getProperty("user.home"), path))
            "file://" -> ConfigFactory.parseFile(File( path ))
            else      -> ConfigFactory.load( path )
          }
        }
      } ?: getDefaultConf("")
      return config
    }
  }
}
*/
