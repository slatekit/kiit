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

package slatekit.notifications.push

import okhttp3.Request
import slatekit.notifications.common.Sender
import slatekit.results.Outcome

class PushIOS : Sender<PushMessage> {

    override fun validate(model: PushMessage): Outcome<PushMessage> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun build(model: PushMessage): Outcome<Request> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
