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
package test

import org.junit.Test
import slatekit.apis.core.Call
import slatekit.common.Conversions
import slatekit.common.DateTime
import slatekit.common.InputArgs
import slatekit.common.Request
import slatekit.meta.Reflector
import slatekit.tests.common.UserApi

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
//        assert(p.type == t)
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
//        assert(isListType)
//        val tp = listParam.type.arguments[0]
//        assert( tp.type!! == Reflector.IntType)
//    }


    @Test fun can_handle_string(){
        assert(Conversions.handleString("null") == "")
        assert(Conversions.handleString("") == "")
        assert(Conversions.handleString("''") == "''")
        assert(Conversions.handleString("abc") == "abc")
    }


    @Test fun can_handle_vars(){
        val vars1 = Conversions.toVars("null")
        assert(vars1.size == 0 )

        val vars2 = Conversions.toVars("")
        assert(vars2.size == 0 )

        val vars3 = Conversions.toVars("a=1,b=2,c=3")
        assert(vars3.size == 3 )
        assert(vars3["a"] == "1" )
        assert(vars3["b"] == "2" )
        assert(vars3["c"] == "3" )
    }


    @Test fun can_handle_types(){

        fun ensureTypes(inputs: InputArgs):Unit {
            val call = Call()
            val req = Request("app.users.testTypes", listOf("app", "users", "testTypes"), "cli", "post", inputs, null)
            val method = Reflector.getMethod(UserApi::class, "testTypes")
            val args = call.fillArgsForMethod(method!!, req, req.data!!, false)

            assert(args.size == 8)
            assert(args[0] == "123456789")
            assert(args[1] == true)
            assert(args[2] == 123.toShort())
            assert(args[3] == 98765)
            assert(args[4] == 123456.toLong())
            assert(args[5] == 2.5f)
            assert(args[6] == 900.99)
            assert(args[7] == DateTime.of(2017, 5, 27))
        }
        ensureTypes(InputArgs( mapOf<String,Any>(
                "phone"         to "123456789"          ,
                "current"       to "true"               ,
                "code"          to "123"                ,
                "zip"           to "98765"              ,
                "id"            to "123456"             ,
                "rating"        to "2.5"                ,
                "value"         to "900.99"             ,
                "date"          to "20170527"
        )))
        ensureTypes(InputArgs( mapOf<String,Any>(
                "phone"         to "123456789"          ,
                "current"       to true                 ,
                "code"          to 123                  ,
                "zip"           to 98765                ,
                "id"            to 123456               ,
                "rating"        to 2.5                  ,
                "value"         to 900.99               ,
                "date"          to "20170527"
        )))
    }


    @Test fun can_handle_lists(){

        fun ensureList(inputs:InputArgs, expected:List<Int>):Unit {
            val name = "argTypeListInt"
            val call = Call()
            val req = Request("app.users.$name", listOf("app", "users", name), "cli", "post", inputs, null)
            val method = Reflector.getMethod(UserApi::class, name)
            val args = call.fillArgsForMethod(method!!, req, req.data!!, false)

            assert(args.size == 1)
            for(ndx in 0..expected.size -1 ) {
                val actual = (args[0] as List<Int>)[ndx]
                val expected = expected[ndx]
                assert( expected == actual)
            }
        }
        ensureList(InputArgs( mapOf<String,Any>( "items" to "null" ))        , listOf<Int>())
        ensureList(InputArgs( mapOf<String,Any>( "items" to "1,2,3,4" ))     , listOf<Int>(1,2,3,4))
        ensureList(InputArgs( mapOf<String,Any>( "items" to listOf(1,2,3,4))), listOf<Int>(1,2,3,4))
    }


    @Test fun can_handle_map(){

        fun ensureMap(inputs:InputArgs, expected:Map<String,Int>):Unit {
            val name = "argTypeMapInt"
            val call = Call()
            val req = Request("app.users.$name", listOf("app", "users", name), "cli", "post", inputs, null)
            val method = Reflector.getMethod(UserApi::class, name)
            val args = call.fillArgsForMethod(method!!, req, req.data!!, false)

            assert(args.size == 1)
            for(key in expected.keys ) {
                val actual = (args[0] as Map<String,Int>)[key]
                val expected = expected[key]
                assert( expected == actual)
            }
        }
        val sample = mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4)
        ensureMap(InputArgs( mapOf<String,Any>( "items" to "null" ))            , mapOf<String,Int>())
        ensureMap(InputArgs( mapOf<String,Any>( "items" to "a=1,b=2,c=3,d=4" )) , mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4))
        ensureMap(InputArgs( mapOf<String,Any>( "items" to sample ))            , mapOf<String,Int>( "a" to 1, "b" to 2, "c" to 3, "d" to 4))
    }
}