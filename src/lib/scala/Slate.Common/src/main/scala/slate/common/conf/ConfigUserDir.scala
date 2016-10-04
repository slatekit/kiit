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
package slate.common.conf

import java.io.File

import slate.common._
import slate.common.databases.DbConString
import slate.common.encrypt.Encryptor

object ConfigUserDir {


  /**
    * creates a api credentials file in the app directory of the user home path
    * e.g. {user.home}/{appName}/{name}.conf
    *
    * @param appName   : The name of the app directory
    * @return
    */
  def createCredentials(appName:String, name:String, creds:ApiCredentials, enc:Option[Encryptor]):String =
  {
    createFile( appName, name + ".conf",
    {
      createSection( name, List[(String,String)]
        (
          key( "account" , creds.account, enc ),
          key( "key"     , creds.key    , enc ),
          key( "pass"    , creds.pass   , enc ),
          key( "env"     , creds.env    , enc ),
          key( "tag"     , creds.tag    , enc )
        )
      )
    })
  }


  /**
    * creates a login file in the app directory of the user home path
    * e.g. {user.home}/{appName}/{name}.conf
    *
    * @param appName   : The name of the app directory
    * @return
    */
  def createLogin(appName:String, name:String, creds:Credentials, enc:Option[Encryptor]):String =
  {
    createFile(appName, name + ".conf",
    {
      createSection( name, List[(String,String)]
        (
          key( "id"     , creds.id     , enc ),
          key( "name"   , creds.name   , enc ),
          key( "email"  , creds.email  , enc ),
          key( "region" , creds.region , enc ),
          key( "key"    , creds.key    , enc ),
          key( "env"    , creds.env    , enc )
        )
      )
    })
  }


  def createDbConfig(appName:String, name:String, con:DbConString, enc:Option[Encryptor]):String = {

    createFile(appName, name + ".conf",
    {
      createSection( name, List[(String,String)]
        (
          key( "driver", con.driver  , None ),
          key( "url"   , con.url     , enc  ),
          key( "user"  , con.user    , enc  ),
          key( "pswd"  , con.password, enc  )
        )
      )
    })
  }


  /**
    * creates a folder inside the app directory of the user home path
    * e.g. {user.home}/{appName}
    *      c:/users/kreddy/myapp/logs
    *
    * @param appName   : The name of the app directory
    * @return
    */
  def createFile(appName:String, name:String, callback: => String):String =
  {
    val userHome = System.getProperty("user.home")
    Ensure.isNotNull(userHome, "Unable to load user directory from 'user.home' system property")

    Files.mkUserDir(appName)

    // {user}/{app/{file}
    val path = userHome + File.separator + appName + File.separator + name
    val file = new File(path)
    val content = callback
    val fullPath = file.getAbsolutePath
    Files.writeAllText(fullPath, content)
    file.getAbsolutePath
  }


  private def createSection(name:String, keys: => List[(String, String)]):String = {
    val newLine = Strings.newline()
    val content =  name + " { " + newLine +
                   keys.foldLeft("") { (v, c) =>  v + "\t" + c._1 + " : \"" + c._2  + "\"" + newLine} + newLine +
                   "}"
    content
  }


  private def key(name:String, value:String, enc:Option[Encryptor]): (String, String) = {
    ( name   , encryptOrGet(value, enc)   )
  }


  private def encryptOrGet(text:String, enc:Option[Encryptor]): String = {
    enc.map( e => e.encrypt(text) ).getOrElse(text)
  }
}
