/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package test.setup

import slatekit.common.utils.B64Java8
import slatekit.common.encrypt.Encryptor

object MyEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8) {

}