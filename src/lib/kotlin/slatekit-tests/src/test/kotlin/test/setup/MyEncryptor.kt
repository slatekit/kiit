/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
  *  </kiit_header>
 */
package test.setup

import kiit.common.convert.B64Java8
import kiit.common.crypto.Encryptor

object MyEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8) {

}