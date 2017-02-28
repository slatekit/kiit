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

package slate.core.common

import slate.common.encrypt.EncryptSupportIn
import slate.common.i18n.I18nSupportIn
import slate.common.logging.LogSupportIn
import slate.common.results.ResultSupportIn
import slate.entities.core.{EntityRepo, EntityService, Entity}

/**
 * Entity Service wrapper with support for encryption, logging, results, and application context
 * @param context
 * @param repo
 * @tparam T
 */
class EntityServiceWithSupport[T >: Null <: Entity](val context:AppContext, repo:EntityRepo[T])
  extends EntityService[T](repo)
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{

  def initContext():Unit = {
  }


  override protected def log() = Option(context.log)
  override protected def enc() = context.enc
  override protected def res() = context.res

}
