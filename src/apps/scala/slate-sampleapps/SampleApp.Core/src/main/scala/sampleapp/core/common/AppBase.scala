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
package sampleapp.core.common

import sampleapp.core.models.User
import sampleapp.core.services.UserService
import slate.common.app.AppMeta
import slate.common.databases.DbLookup
import slate.common.info.{Lang, Host}
import slate.common.logging.LoggerConsole
import slate.core.app.AppProcess
import slate.core.common.AppContext
import slate.entities.core.Entities
import slate.entities.repos.EntityRepoInMemory
import scala.reflect.runtime.universe.typeOf

class AppBase extends AppProcess
{
  /**
    * Initialize app
    *
    * NOTES:
    * 1. Base class parses the raw command line args and builds the Args object
    * 2. Base class has command line args Array[String] iniitally supplied as rawArgs
    */
  override def onInit(): Unit =
  {
    // Initialize the context with common app info
    // The database can be set up in the "env.conf" shared inherited config or
    // overridden in the environment specific e.g. "env.qa.conf"
    ctx = new AppContext (
      env  = env,
      cfg  = conf,
      log  = new LoggerConsole(getLogLevel()),
      ent  = new Entities(Option(dbs())),
      inf  = aboutApp(),
      dbs  = Option(dbs()),
      enc  = Some(AppEncryptor)
    )

    // 4. Setup the User entity services
    // NOTE(s):
    // 1. See the ORM documentation for more info.
    // 2. The entity services uses a Generic Service/Repository pattern for ORM functionality.
    // 3. The services support CRUD operations out of the box for single-table mapped entities.
    // 4. This uses an In-Memory repository for demo but you can use EntityRepoMySql for MySql
    ctx.ent.register[User](isSqlRepo= false, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new EntityRepoInMemory[User](typeOf[User]))
  }
}
