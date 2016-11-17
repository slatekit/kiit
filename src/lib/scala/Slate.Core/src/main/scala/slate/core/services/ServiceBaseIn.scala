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

import slate.common.encrypt.EncryptSupportIn
import slate.common.i18n.I18nSupportIn
import slate.common.logging.LogSupportIn
import slate.common.results.ResultSupportIn

class ServiceBaseIn
  extends ResultSupportIn
  with LogSupportIn
  with EncryptSupportIn
  with I18nSupportIn
{
}
