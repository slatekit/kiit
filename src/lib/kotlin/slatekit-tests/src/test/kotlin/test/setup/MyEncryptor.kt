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
package test.setup

import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor

object MyEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8) {

}