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
package slate.core.mods

import slate.entities.core.{EntityRepo, EntityService}
import slate.common.results.{ResultSupportIn}

class ModService(repo:EntityRepo[Mod]) extends EntityService[Mod](repo) with ResultSupportIn {

}
