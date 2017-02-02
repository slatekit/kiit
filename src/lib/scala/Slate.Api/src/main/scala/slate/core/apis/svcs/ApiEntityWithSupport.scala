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

package slate.core.apis.svcs

import slate.common.encrypt.{EncryptSupportIn, Encryptor}
import slate.common.results.ResultSupportIn
import slate.core.common.{EntityServiceWithSupport, AppContextSupport}
import slate.entities.core.{EntityService, IEntity}
import slate.common.i18n.{I18nSupportIn, I18nStrings}
import slate.common.logging.{LogSupportIn, LoggerBase}
import slate.core.apis.ApiBaseEntity


class ApiEntityWithSupport[T >: Null <: IEntity, TSvc >: Null]
  extends ApiBaseEntity[T]
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{

  protected def initContext(svc:EntityServiceWithSupport[T]):Unit =
  {
    svc.context = this.context
    svc.initContext()
    _service = svc.asInstanceOf[EntityService[T]]
    _log = Option(context.log)
    _enc = context.enc
    _res = context.res
  }


  def service: TSvc =
  {
    _service.asInstanceOf[TSvc]
  }
}
