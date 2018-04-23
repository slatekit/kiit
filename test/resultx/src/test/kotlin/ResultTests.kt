import org.junit.Assert
import org.junit.Test
import slatekit.common.*
import slatekit.common.results.FAILURE
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.SUCCESS
import slatekit.common.results.UNEXPECTED_ERROR

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

data class Person(val name:String)

class ResultTests {

    @Test
    fun test_success(){
        val res = success(Person("peter"), msg="created")
        Assert.assertTrue(res.success)
        Assert.assertEquals("created", res.msg)
        Assert.assertEquals(SUCCESS, res.code)
    }


    @Test
    fun test_map_success(){
        val res1 = success(Person("peter"), msg="created")
        val res = res1.map { it -> Pair(it, "spider-man") }

        Assert.assertTrue(res.success)
        Assert.assertEquals("created", res.msg)
        Assert.assertEquals(SUCCESS, res.code)

        val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
        Assert.assertTrue(user.first.name == "peter")
        Assert.assertTrue(user.second == "spider-man")
    }


    @Test
    fun test_map_failure(){
        val res1 = failure<Person>()
        val res = res1.map { it -> Pair(it, "spider-man") }

        Assert.assertFalse(res.success)
        Assert.assertEquals("failure", res.msg)
        Assert.assertEquals(FAILURE, res.code)

        val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
        Assert.assertTrue(user.first.name == "kishore")
        Assert.assertTrue(user.second == "reddy")
    }


    @Test
    fun test_flat_map_success(){
        val res1 = success(Person("peter"), msg="created")
        val res = res1.flatMap { it -> success(Pair(it, "spider-man"), SUCCESS, "alias") }

        Assert.assertTrue(res.success)
        Assert.assertEquals("alias", res.msg)
        Assert.assertEquals(SUCCESS, res.code)

        val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
        Assert.assertTrue(user.first.name == "peter")
        Assert.assertTrue(user.second == "spider-man")
    }


    @Test
    fun test_flat_map_failure(){
        val res1 = failure<Person>("setup")
        val res = res1.flatMap { it -> failure<Pair<Person, String>>() }

        Assert.assertFalse(res.success)
        Assert.assertEquals("setup", res.msg)
        Assert.assertEquals(FAILURE, res.code)

        val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
        Assert.assertTrue(user.first.name == "kishore")
        Assert.assertTrue(user.second == "reddy")
    }


    @Test
    fun test_of_success(){
        val res = Result.of { Person("peter") }

        Assert.assertTrue(res.success)
        Assert.assertEquals("", res.msg)
        Assert.assertEquals(SUCCESS, res.code)
    }


    @Test
    fun test_of_failure(){
        val res = Result.of { throw Exception("Test error") }

        Assert.assertFalse(res.success)
        Assert.assertEquals("Test error", res.msg)
        Assert.assertEquals(FAILURE, res.code)
    }


    @Test
    fun test_attempt_failure(){
        val res = Result.attempt { throw Exception("Test error") }

        Assert.assertFalse(res.success)
        Assert.assertEquals("Test error", res.msg)
        Assert.assertEquals(UNEXPECTED_ERROR, res.code)
    }


    @Test
    fun test_fold(){
        val res = Result.of { Person("peter") }
        val name = res.fold( { it -> "Error Peter"   },
                             { it -> "Success Peter" } )

        Assert.assertTrue(res.success)
        Assert.assertEquals("", res.msg)
        Assert.assertEquals(SUCCESS, res.code)
        Assert.assertEquals("Success Peter", name)
    }


    fun print(res: Result<*, *>) {
        println(res.success)
        println(res.code)
        println(res.msg)
    }
}
