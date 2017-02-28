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

import slate.common.databases.{DbCon, DbConString}
import slate.common.envs.Env
import slate.common.{ApiCredentials, Credentials}

trait ConfigSupport {

  def config:ConfigBase = ???


  /**
   * gets the environment specified in the config via "env.name,env.mode"
   *
   * @return
   */
  def env() : Option[Env] = {

    mapTo[Env]( "env", conf => {

      val name = config.getString( "env.name" )
      val mode  = config.getString( "env.mode" )
      new Env(name, Env.interpret(mode), s"${mode} : ${name}")
    })
  }


  /**
   * Gets user credentials from the config.
   * This is used for the CLI ( command line interface ) shell.
    *
    * @return
   */
  def login(): Option[Credentials] = {

    mapTo[Credentials]( "login", conf => {

      new Credentials(
        conf.getString( "login.id"     ),
        conf.getString( "login.name"   ),
        conf.getString( "login.email"  ),
        conf.getString( "login.region" ),
        conf.getString( "login.key"    ),
        conf.getString( "login.env"    )
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

    mapTo[ApiCredentials]( name, conf => {

      new ApiCredentials(
        conf.getString( name + ".account" ),
        conf.getString( name + ".key"     ),
        conf.getString( name + ".pass"    ),
        conf.getString( name + ".env"     ),
        conf.getString( name + ".tag"     )
      )
    })
  }


  /**
   * connection string from the config
    *
    * @param prefix
   * @return
   */
  def dbCon(prefix:String = "db") : Option[DbCon] = {

    mapTo[DbCon]( prefix, conf => {

      new DbConString(
        conf.getString( prefix + ".driver"   ),
        conf.getString( prefix + ".url"      ),
        conf.getString( prefix + ".user"     ),
        conf.getString( prefix + ".pswd"     )
      )
    })
  }


  protected def mapTo[T](key:String, mapper: (ConfigBase) => T ):Option[T] = {

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
