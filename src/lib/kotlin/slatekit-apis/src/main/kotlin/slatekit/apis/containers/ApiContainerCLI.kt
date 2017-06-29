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
import slatekit.apis.ApiProtocolCLI
import slatekit.apis.core.*
import slatekit.apis.ApiReg
import slatekit.core.common.AppContext
import slatekit.core.middleware.*



/**
  * A thin wrapper on the ApiContainer that only extends the base implementation by handling
  * requests for help/docs on the Apis/Actions for the CLI ( Command Line Interface )
  */

class ApiContainerCLI(ctx      : AppContext,
                      auth     : Auth?          = null,
                      apis     : List<ApiReg>?  = null,
                      errors   : Errors?        = null,
                      hooks    : List<Hook>?    = null,
                      filters  : List<Filter>?  = null,
                      controls : List<Handler>? = null,
                      allowIO  : Boolean          = true)
  : ApiContainer(ctx, allowIO, auth, ApiProtocolCLI, apis, errors, hooks, filters, controls)