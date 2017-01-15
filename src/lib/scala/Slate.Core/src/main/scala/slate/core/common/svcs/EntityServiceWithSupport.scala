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

package slate.core.common.svcs

import slate.common.encrypt.{Encryptor, EncryptSupportIn}
import slate.entities.core.{EntityService, IEntity}
import slate.common.i18n.{I18nSupportIn}
import slate.common.logging.{LoggerBase, LogSupportIn}
import slate.common.results.ResultSupportIn
import slate.core.common.AppContext

class EntityServiceWithSupport[T >: Null <: IEntity]
  extends EntityService[T]
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{

  // This must be supplied during startup!
  var context:AppContext = null


  def initContext():Unit = {
    _log = Option(context.log)
    _enc = context.enc
    _res = context.res
  }
}
