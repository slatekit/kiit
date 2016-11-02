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

package slate.core.services

import slate.common.encrypt.EncryptSupport
import slate.common.i18n.I18nSupport
import slate.common.logging.LogSupport
import slate.common.results.ResultSupportIn

class ServiceBase
  extends ResultSupportIn
  with LogSupport
  with EncryptSupport
  with I18nSupport
{

  /**
   * lifecycle hook for starting up
   */
  def init():Unit = {
  }


  /**
   * lifecycle hook for ending
   */
  def end():Unit = {
  }
}
