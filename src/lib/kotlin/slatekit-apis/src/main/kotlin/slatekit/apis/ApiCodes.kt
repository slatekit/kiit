package slatekit.apis

import slatekit.results.Codes
import slatekit.results.Failed
import slatekit.results.builders.Outcomes

object ApiCodes {
    @JvmField val SUCCESS  = Codes.SUCCESS
    @JvmField val AUTH_NOT_SET  = Failed.Denied("AUTH_NOT_SET",400004, "Authentication not configured")
}
