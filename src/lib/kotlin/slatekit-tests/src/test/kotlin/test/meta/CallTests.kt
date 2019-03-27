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
package test.meta

import org.junit.Assert
import org.junit.Test
import slatekit.common.Conversions
import slatekit.common.DateTimes
import slatekit.common.requests.InputArgs
import slatekit.common.CommonRequest
import slatekit.common.requests.Source
import slatekit.meta.Reflector
import slatekit.meta.Deserializer
import test.setup.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */
class CallTests {

//    @Test fun test_class_vs_type() {
//        val t = Boolean::class.createType()
//        val method = Reflector.getMethod(UserApi::class, "activate")
//        val p = method!!.parameters[3]
//        println(p.type)
//        println(t)
//        Assert.assertTrue(p.type == t)
//    }
//
//
//    @Test fun test_can_handle_lists(){
//        val method = Reflector.getMethod(UserApi::class, "argTypeMapInt") //"argTypeListInt")
//        val listParam = method!!.parameters[1]
//
//        println(listParam.type.toString())
//        println(listParam.type.arguments)
//        println(listParam.type.arguments[0].type.toString())
//
//        val listtype = List::class.createType()
//        if(listParam.type is ParameterizedType){
//            val ptype = listParam.type as ParameterizedType
//            println(ptype.rawType)
//        }
//
//        val isListType = listParam.type.isSubtypeOf(listtype)
//        Assert.assertTrue(isListType)
//        val tp = listParam.type.arguments[0]
//        Assert.assertTrue( tp.type!! == Reflector.IntType)
//    }


    @Test fun can_handle_string(){
        Assert.assertTrue(Conversions.handleString("null") == "")
        Assert.assertTrue(Conversions.handleString("") == "")
        Assert.assertTrue(Conversions.handleString("''") == "''")
        Assert.assertTrue(Conversions.handleString("abc") == "abc")
    }


    @Test fun can_handle_vars(){
        val vars1 = Conversions.toVars("null")
        Assert.assertTrue(vars1.size == 0 )

        val vars2 = Conversions.toVars("")
        Assert.assertTrue(vars2.size == 0 )

        val vars3 = Conversions.toVars("a=1,b=2,c=3")
        Assert.assertTrue(vars3.size == 3 )
        Assert.assertTrue(vars3["a"] == "1" )
        Assert.assertTrue(vars3["b"] == "2" )
        Assert.assertTrue(vars3["c"] == "3" )
    }


    @Test fun can_handle_types(){

        fun ensureTypes(inputs: InputArgs):Unit {
            val req = CommonRequest("app.users.testTypes", listOf("app", "users", "testTypes"), Source.CLI, "post", inputs, InputArgs(mapOf()))
            val deserializer = Deserializer(req)
            val method = Reflector.getMethod(UserApi::class, "testTypes")
            val args = deserializer.deserialize(method!!.parameters.drop(1))

            Assert.assertTrue(args.size == 8)
            Assert.assertTrue(args[0] == "123456789")
            Assert.assertTrue(args[1] == true)
            Assert.assertTrue(args[2] == 123.toShort())
            Assert.assertTrue(args[3] == 98765)
            Assert.assertTrue(args[4] == 123456.toLong())
            Assert.assertTrue(args[5] == 2.5f)
            Assert.assertTrue(args[6] == 900.99)
            Assert.assertTrue(args[7] == DateTimes.of(2017, 5, 27))
        }
        ensureTypes(InputArgs(mapOf<String, Any>(
                "phone" to "123456789",
                "current" to "true",
                "code" to "123",
                "zip" to "98765",
                "id" to "123456",
                "rating" to "2.5",
                "value" to "900.99",
                "date" to "20170527"
        )))
        ensureTypes(InputArgs(mapOf<String, Any>(
                "phone" to "123456789",
                "current" to true,
                "code" to 123,
                "zip" to 98765,
                "id" to 123456,
                "rating" to 2.5,
                "value" to 900.99,
                "date" to "20170527"
        )))
    }


    @Test fun can_handle_lists(){

        fun ensureList(inputs: InputArgs, expected:List<Int>):Unit {
            val name = "argTypeListInt"
            val req = CommonRequest("app.users.$name", listOf("app", "users", name), Source.CLI, "post", inputs, InputArgs(mapOf()))
            val deserializer = Deserializer(req)
            val method = Reflector.getMethod(UserApi::class, name)
            val args = deserializer.deserialize(method!!.parameters.drop(1))

            Assert.assertTrue(args.size == 1)
            for(ndx in 0..expected.size -1 ) {
                val actual = (args[0] as List<Int>)[ndx]
                val expected = expected[ndx]
                Assert.assertTrue( expected == actual)
            }
        }
        ensureList(InputArgs(mapOf<String, Any>("items" to "null")), listOf<Int>())
        ensureList(InputArgs(mapOf<String, Any>("items" to "1,2,3,4")), listOf<Int>(1,2,3,4))
        ensureList(InputArgs(mapOf<String, Any>("items" to listOf(1, 2, 3, 4))), listOf<Int>(1,2,3,4))
    }


    @Test fun can_handle_map(){

        fun ensureMap(inputs: InputArgs, expected:Map<String,Int>):Unit {
            val name = "argTypeMapInt"
            val req = CommonRequest("app.users.$name", listOf("app", "users", name), Source.CLI, "post", inputs, InputArgs(mapOf()))
            val deserializer = Deserializer(req)
            val method = Reflector.getMethod(UserApi::class, name)
            val args = deserializer.deserialize(method!!.parameters.drop(1))

            Assert.assertTrue(args.size == 1)
            for(key in expected.keys ) {
                val actual = (args[0] as Map<String,Int>)[key]
                val expected = expected[key]
                Assert.assertTrue( expected == actual)
            }
        }
        val sample = mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4)
        ensureMap(InputArgs(mapOf<String, Any>("items" to "null")), mapOf<String,Int>())
        ensureMap(InputArgs(mapOf<String, Any>("items" to "a=1,b=2,c=3,d=4")), mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4))
        ensureMap(InputArgs(mapOf<String, Any>("items" to sample)), mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4))
    }
}