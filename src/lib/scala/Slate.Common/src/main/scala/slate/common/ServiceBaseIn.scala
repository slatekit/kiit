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

package slate.common

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
