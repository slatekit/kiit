/**
 <kiit_header>
url: www.kiit.dev
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
import kiit.common.ids.ULIDs


class IdentityTests {

    @Test fun can_build_via_helper() {
        val id1 = Identity.app("app 1", "signup", "alerts", EnvMode.Qat)
        Assert.assertEquals( "app_1", id1.company )
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "latest", id1.version )
        Assert.assertEquals( "app_12.signup.alerts.app", id1.name )
        Assert.assertEquals( "app_1.signup.alerts.app.qat.latest", id1.full )
    }


    @Test fun can_create_manually() {
        val id1 = Identity.of("app 1", "signup", "alerts", Agent.App, EnvMode.Qat, version = "1.2")
        Assert.assertEquals( "app_1", id1.company )
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "1.2", id1.version )
        Assert.assertEquals( "app_1.signup.alerts", id1.name )
        Assert.assertEquals( "app_1.signup.alerts.app.qat.1_2", id1.full )
    }


    @Test fun can_create_with_id() {
        val id = ULIDs.create()
        val id1 = Identity.of("app_1","signup", "alerts", Agent.App, EnvMode.Qat, version = "1.2", instance= id.value)
        Assert.assertEquals( "signup", id1.area )
        Assert.assertEquals( "alerts", id1.service )
        Assert.assertEquals( Agent.App, id1.agent )
        Assert.assertEquals( EnvMode.Qat.name, id1.env )
        Assert.assertEquals( "1.2", id1.version )
        Assert.assertEquals( "app_1.signup.alerts.app", id1.name )
        Assert.assertEquals( "app_1.signup.alerts.app.qat.1_2.${id.value}", id1.id )
    }
}