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
package slate.core.apis.svcs

import slate.common.encrypt.{EncryptSupportIn}
import slate.common.i18n.{I18nSupportIn}
import slate.common.logging.{LogSupportIn}
import slate.common.results.ResultSupportIn
import slate.core.apis.{ApiContainer, ApiBase}
import slate.core.common.{AppContext, AppContextSupport}

class ApiWithSupport(
                      context:AppContext
                     ) extends ApiBase(context)
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{

  override protected def log() = Option(context.log)
  override protected def enc() = context.enc
  override protected def res() = context.res
}
