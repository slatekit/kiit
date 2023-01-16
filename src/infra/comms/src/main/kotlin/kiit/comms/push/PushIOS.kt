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

package kiit.comms.push

import okhttp3.Request
import kiit.http.HttpRPC
import kiit.comms.common.Sender
import kiit.results.Outcome

class PushIOS(override val client: HttpRPC = HttpRPC()) : Sender<PushMessage> {

    override fun validate(model: PushMessage): Outcome<PushMessage> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun build(model: PushMessage): Outcome<Request> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
