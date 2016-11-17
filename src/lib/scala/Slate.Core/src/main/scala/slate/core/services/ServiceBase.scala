/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
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
