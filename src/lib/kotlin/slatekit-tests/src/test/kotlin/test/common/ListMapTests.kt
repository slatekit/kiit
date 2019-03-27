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
package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.utils.ListMap
import slatekit.common.Vars

/**
 * Created by kishorereddy on 6/4/17.
 */
class ListMapTests {

    class User(val id:Int )


    @Test fun can_add() {
        val items =   ListMap(listOf(
            Pair("a", 1),
            Pair("b", 2),
            Pair("c", 3)
        ))
        Assert.assertTrue( items.size == 3)
        Assert.assertTrue( items.contains("a"))
        Assert.assertTrue( items.contains("b"))
        Assert.assertTrue( items.contains("c"))
        Assert.assertTrue( !items.contains("d"))
    }


    @Test fun can_get_by_name() {
        val items =   ListMap(listOf(
                Pair("a", 1),
                Pair("b", 2),
                Pair("c", 3)
        ))
        Assert.assertTrue( items.get("a") == 1)
        Assert.assertTrue( items.get("b") == 2)
        Assert.assertTrue( items.get("c") == 3)
    }


    @Test fun can_get_by_index() {
        val items =   ListMap(listOf(
                Pair("a", 1),
                Pair("b", 2),
                Pair("c", 3)
        ))
        Assert.assertTrue( items.getAt(0) == 1)
        Assert.assertTrue( items.getAt(1) == 2)
        Assert.assertTrue( items.getAt(2) == 3)
    }


    @Test fun can_remove() {
        val items =  ListMap(listOf(
                Pair("a", 1),
                Pair("b", 2),
                Pair("c", 3)
        ))
        Assert.assertTrue( items.size == 3)

        val items2 = items.remove("b")
        Assert.assertTrue( items.contains("a"))
        Assert.assertTrue( items.contains("b"))
        Assert.assertTrue( items.contains("c"))

        Assert.assertTrue( items2.contains("a"))
        Assert.assertTrue( !items2.contains("b"))
        Assert.assertTrue( items2.contains("c"))
        Assert.assertTrue( items2.get("a") == 1)
        Assert.assertTrue( items2.get("c") == 3)
        Assert.assertTrue( items2.getAt(0) == 1)
        Assert.assertTrue( items2.getAt(1) == 3)
    }


    @Test fun can_add_and_remove() {
        val items =   ListMap(listOf(
            Pair(1, User(1)),
            Pair(2, User(2)),
            Pair(3, User(3))
        ))

        val items2 = items.remove(2)
        Assert.assertTrue(items2[1]!!.id == 1)
        Assert.assertTrue(items2[3]!!.id == 3)
        Assert.assertTrue( items2.size == 2)

    }


    @Test fun can_use_vars() {
        val vars = Vars(listOf(
                Pair("user.name" , "john.doe"),
                Pair("app.name"  , "mobile.app1"),
                Pair("app.confirmUrl", "http://myapp1.com/api/confirm?id=123"),
                Pair("user.email", "john.doe@gmail.com")
        ))
        Assert.assertTrue(vars.get("user.name") == "john.doe")
        Assert.assertTrue(vars.get("app.name") == "mobile.app1")
    }
}
