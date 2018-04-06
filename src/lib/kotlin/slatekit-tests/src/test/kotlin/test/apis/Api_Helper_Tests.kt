/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.apis

import org.junit.Test
import slatekit.apis.ApiConstants
import slatekit.apis.helpers.ApiHelper

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Helper_Tests : ApiTestsBase() {


    @Test fun is_api_action_not_authed() {
        assert(ApiHelper.isActionNotAuthed(ApiConstants.None))
        assert(ApiHelper.isActionNotAuthed(ApiConstants.Unknown))
        assert(!ApiHelper.isActionNotAuthed(ApiConstants.Parent))
    }


    @Test fun is_api_not_authed() {
        assert(ApiHelper.isApiNotAuthed("@parent", "?"))
        assert(ApiHelper.isApiNotAuthed(ApiConstants.Parent, ApiConstants.Unknown))
        assert(ApiHelper.isApiNotAuthed("@parent", ""))
        assert(ApiHelper.isApiNotAuthed(ApiConstants.Parent, ApiConstants.None))
    }
}
