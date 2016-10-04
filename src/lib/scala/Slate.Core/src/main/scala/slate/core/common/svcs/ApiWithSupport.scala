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
package slate.core.common.svcs

import slate.common.encrypt.{EncryptSupportIn}
import slate.common.i18n.{I18nSupportIn}
import slate.common.logging.{LogSupportIn}
import slate.common.results.ResultSupportIn
import slate.core.apis.ApiBase

class ApiWithSupport extends ApiBase
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
  with AppContextSupport
{

  protected def initContext():Unit =
  {
    _log = Option(context.log)
    _enc = context.enc
    _res = context.res
  }
}
