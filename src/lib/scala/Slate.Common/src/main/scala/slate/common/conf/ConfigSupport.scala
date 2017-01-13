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

package slate.common.conf

import slate.common.databases.DbConString
import slate.common.envs.EnvItem
import slate.common.{Strings, ApiCredentials, Credentials}

trait ConfigSupport {

  def config:ConfigBase = ???


  /**
   * gets the environment specified in the config via "env.name,env.mode"
   *
   * @return
   */
  def env() : Option[EnvItem] = {

    map[EnvItem]( "env", conf => {

      val name = config.getString( "env.name" )
      val env  = config.getString( "env.mode" )
      new EnvItem(name, env, s"${env} : ${name}")
    })
  }


  /**
   * Gets user credentials from the config.
   * This is used for the CLI ( command line interface ) shell.
    *
    * @return
   */
  def login(): Option[Credentials] = {

    map[Credentials]( "login", conf => {

      new Credentials(
        conf.getStringEnc( "login.id"     ),
        conf.getStringEnc( "login.name"   ),
        conf.getStringEnc( "login.email"  ),
        conf.getStringEnc( "login.region" ),
        conf.getStringEnc( "login.key"    ),
        conf.getStringEnc( "login.env"    )
      )
    })
  }


  /**
    * Gets user credentials from the config.
    * This is used for the CLI ( command line interface ) shell.
    *
    * @return
    */
  def apiKey(name:String): Option[ApiCredentials] = {

    map[ApiCredentials]( name, conf => {

      new ApiCredentials(
        conf.getStringEnc( name + ".account" ),
        conf.getStringEnc( name + ".key"     ),
        conf.getStringEnc( name + ".pass"    ),
        conf.getStringEnc( name + ".env"     ),
        conf.getStringEnc( name + ".tag"     )
      )
    })
  }


  /**
   * connection string from the config
    *
    * @param prefix
   * @return
   */
  def dbCon(prefix:String = "db") : Option[DbConString] = {

    map[DbConString]( prefix, conf => {

      new DbConString(
        conf.getString   ( prefix + ".driver"   ),
        conf.getStringEnc( prefix + ".url"      ),
        conf.getStringEnc( prefix + ".user"     ),
        conf.getStringEnc( prefix + ".pswd"     )
      )
    })
  }


  protected def map[T](key:String, mapper: (ConfigBase) => T ):Option[T] = {

    // Section not present!
    if(config.containsKey(key)) {
      // Location specified ?
      val locationKey = key + ".location"
      if (config.containsKey(locationKey)) {

        // 1. "@{resource}/sms.conf"
        // 2. "@{company.dir}/sms.conf"
        // 3. "@{app.dir}/sms.conf"
        // 3. "/conf/sms.conf"
        val location = config.getString(locationKey)
        val conf = config.loadFrom(Some(location))
        Some(mapper(conf.get))
      }
      else
        Some(mapper(config))
    }
    else
      None
  }
}
