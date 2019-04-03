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
package test


import org.junit.Assert
import org.junit.Test
import slatekit.core.cmds.Command


class Command_Tests {


    @Test
    fun can_create_command() {
        val cmd = Command("syncData", "sync data from server")
        Assert.assertEquals(cmd.info.name, "syncData")
        Assert.assertEquals(cmd.info.desc, "sync data from server")
        Assert.assertEquals(cmd.info.area, "")
        Assert.assertEquals(cmd.info.group, "")
        Assert.assertEquals(cmd.info.action, "")
    }


    @Test
    fun can_create_command_with_namespace() {
        val cmd = Command("app.users.syncData", "sync data from server")
        Assert.assertEquals(cmd.info.name, "app.users.syncData")
        Assert.assertEquals(cmd.info.desc, "sync data from server")
        Assert.assertEquals(cmd.info.area, "app")
        Assert.assertEquals(cmd.info.group, "users")
        Assert.assertEquals(cmd.info.action, "syncData")
    }
}
