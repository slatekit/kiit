/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis

import kiit.apis.support.FileSupport
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.context.Context


/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context : The context of the application ( logger, config, encryptor, etc )
 */
abstract class ApiBase(override val context: Context) : FileSupport {
    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()
}
