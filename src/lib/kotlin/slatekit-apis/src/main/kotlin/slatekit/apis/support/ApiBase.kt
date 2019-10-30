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

package slatekit.apis.support

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

interface Api

/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context : The context of the application ( logger, config, encryptor, etc )
 */
abstract class ApiBase(override val context: Context) : FileSupport, HooksSupport {
    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    override suspend fun onBefore(req: ApiRequest) {
    }

    override suspend fun onFilter(req: ApiRequest): Outcome<ApiRequest> {
        return Outcomes.success(req)
    }

    override suspend fun onAfter(raw: ApiRequest, req: Outcome<ApiRequest>, res: Outcome<ApiResult>) {
    }
}
