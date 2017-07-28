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

package slatekit.apis.containers

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.apis.CliProtocol
import slatekit.apis.core.Auth
import slatekit.apis.core.Errors
import slatekit.apis.middleware.Filter
import slatekit.apis.middleware.Handler
import slatekit.apis.middleware.Hook
import slatekit.apis.middleware.Rewriter
import slatekit.common.Context
import slatekit.common.Namer


/**
 * A thin wrapper on the ApiContainer that only extends the base implementation by handling
 * requests for help/docs on the Apis/Actions for the CLI ( Command Line Interface )
 */
class ApiContainerCLI(ctx: Context,
                      auth: Auth? = null,
                      apis: List<ApiReg>? = null,
                      errors: Errors? = null,
                      namer: Namer? = null,
                      rewrites: List<Rewriter>? = null,
                      hooks: List<Hook>? = null,
                      filters: List<Filter>? = null,
                      controls: List<Handler>? = null,
                      docKey:String? = null,
                      allowIO: Boolean = true)
    : ApiContainer(ctx, allowIO, auth, CliProtocol, apis, errors, namer, rewrites, docKey = docKey)
{
}