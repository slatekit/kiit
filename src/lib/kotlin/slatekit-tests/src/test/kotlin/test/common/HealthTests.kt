package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.integration.common.Health
import slatekit.common.info.Build
import slatekit.core.common.AppContext

class HealthTests {


    @Test
    fun can_get_version() {
        val build = Build("1.2.3.4", "abc123", "master", "2018-11-20")
        val health = Health(AppContext.simple("test").copy(build = build))
        val info = health.version()
        Assert.assertEquals("version", info.name)
        Assert.assertEquals("health", info.source)
        Assert.assertEquals(Pair("version", "1.2.3.4"), info.items[0])
        Assert.assertEquals(Pair("commit", "abc123")  , info.items[1])
        Assert.assertEquals(Pair("branch", "master")  , info.items[2])
        Assert.assertEquals(Pair("date", "2018-11-20"), info.items[3])
    }


    @Test
    fun can_get_info() {
        val build = Build("1.2.3.4", "abc123", "master", "2018-11-20")
        val health = Health(AppContext.simple("test").copy(build = build))
        val info = health.info()
        println(info)
    }
}

