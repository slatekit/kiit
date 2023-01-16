/**
 <kiit_header>
url: www.slatekit.com
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.common

import org.junit.Assert
import kiit.common.envs.*

import org.junit.Test
import kiit.common.Agent
import kiit.common.Identity
import kiit.common.SimpleIdentity
import kiit.common.ids.ULIDs


class IdentityTests {

    @Test fun can_build_via_helper() {
        val id1 = Identity.app("signup", "alerts", EnvMode.Qat)
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "LATEST", id1.version )
        Assert.assertEquals( "signup.alerts", id1.name )
        Assert.assertEquals( "signup.alerts.app.qat.LATEST", id1.fullname )
    }


    @Test fun can_create_manually() {
        val id1 = SimpleIdentity("signup", "alerts", Agent.App, EnvMode.Qat.name, version = "1.2")
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "1.2", id1.version )
        Assert.assertEquals( "signup.alerts", id1.name )
        Assert.assertEquals( "signup.alerts.app.qat.1_2", id1.fullname )
    }


    @Test fun can_create_with_id() {
        val id = ULIDs.create()
        val id1 = SimpleIdentity("signup", "alerts", Agent.App, EnvMode.Qat.name, version = "1.2", instance= id.value)
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "1.2", id1.version )
        Assert.assertEquals( "signup.alerts", id1.name )
        Assert.assertEquals( "signup.alerts.app.qat.1_2.${id.value}", id1.id )
    }
}