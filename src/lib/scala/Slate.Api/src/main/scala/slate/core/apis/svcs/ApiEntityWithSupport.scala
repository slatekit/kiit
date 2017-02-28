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

import scala.reflect.runtime.universe.{Type}
import slate.common.encrypt.{EncryptSupportIn}
import slate.common.results.ResultSupportIn
import slate.core.common.{AppContext, AppContextSupport}
import slate.entities.core.{Entity}
import slate.common.i18n.{I18nSupportIn}
import slate.common.logging.{LogSupportIn}
import slate.core.apis.{ApiBaseEntity}



class ApiEntityWithSupport[T >: Null <: Entity, TSvc >: Null]
(
  context:AppContext, tpe:Type
)
  extends ApiBaseEntity[T](context, tpe)
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{
  def service: TSvc = _service.asInstanceOf[TSvc]


  override protected def log() = Option(context.log)
  override protected def enc() = context.enc
  override protected def res() = context.res
}
