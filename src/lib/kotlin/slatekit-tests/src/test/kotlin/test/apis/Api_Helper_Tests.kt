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

import org.junit.Assert
import org.junit.Test
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.core.Roles

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Helper_Tests : ApiTestsBase() {


    @Test fun is_api_action_not_authed() {
        Assert.assertTrue(ApiHelper.isActionNotAuthed(Roles.none))
        Assert.assertTrue(ApiHelper.isActionNotAuthed(Roles.guest))
        Assert.assertTrue(!ApiHelper.isActionNotAuthed(Roles.parent))
    }


    @Test fun is_api_not_authed() {
        Assert.assertTrue(ApiHelper.isApiNotAuthed("@parent", "?"))
        Assert.assertTrue(ApiHelper.isApiNotAuthed(Roles.parent, Roles.guest))
        Assert.assertTrue(ApiHelper.isApiNotAuthed("@parent", ""))
        Assert.assertTrue(ApiHelper.isApiNotAuthed(Roles.parent, Roles.none))
    }
}
