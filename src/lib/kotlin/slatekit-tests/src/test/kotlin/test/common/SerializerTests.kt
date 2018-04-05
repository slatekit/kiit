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

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.ext.tail
import slatekit.common.newline
import slatekit.meta.Serialization
import test.setup.User

/**
 * Created by kishorereddy on 6/14/17.
 */

class SerializerTests {


    @Test fun can_serialize_string() {
        val serializer = Serialization.json()
        assert( serializer.serialize(null)       == "null")
        assert( serializer.serialize("")         == "\"\"")
        assert( serializer.serialize("abc")      == "\"abc\"")
        assert( serializer.serialize('a')        == "\"a\"")

        val s = serializer.serialize("a\\b\"c")
        assert( s == "\"a\\\\b\\\"c\"")
    }


    @Test fun can_serialize_numbers() {
        val serializer = Serialization.json()
        assert( serializer.serialize(123)            == "123" )
        assert( serializer.serialize(123L)           == "123" )
        assert( serializer.serialize(123.toShort())  == "123" )
        assert( serializer.serialize(1.23)           == "1.23")
        assert( serializer.serialize(1.23.toFloat()) == "1.23")
    }


    @Test fun can_serialize_bools() {
        val serializer = Serialization.json()
        assert( serializer.serialize(false)          == "false" )
        assert( serializer.serialize(true)           == "true"  )
    }


    @Test fun can_serialize_dates() {
        val serializer = Serialization.json()
        val d1 = serializer.serialize(DateTime.of(2017,6,1))
        val d2 = serializer.serialize(DateTime.of(2017,6,1,9,5,0))
        val d3 = serializer.serialize(DateTime.of(2017,6,1,9,5,5))

        assert( d1 == "\"2017-06-01T00:00:00-04:00\"" )
        assert( d2 == "\"2017-06-01T09:05:00-04:00\"" )
        assert( d3 == "\"2017-06-01T09:05:05-04:00\"" )
    }


    @Test fun can_serialize_list() {
        val serializer = Serialization.json()
        assert( serializer.serialize(listOf("a", "b", "c")) == "[\"a\", \"b\", \"c\"]")
        assert( serializer.serialize(listOf(1, 2, 3)) == "[1, 2, 3]")
        assert( serializer.serialize(listOf(true, false, true)) == "[true, false, true]")
        assert( serializer.serialize(listOf(1.2, 3.4, 5.6)) == "[1.2, 3.4, 5.6]")
        assert( serializer.serialize(listOf("a", 1, 2.3, true, DateTime.of(2017,6,1,9,5,5))) == "[\"a\", 1, 2.3, true, \"2017-06-01T09:05:05-04:00\"]")
    }


    @Test fun can_serialize_json_object() {
        val serializer = Serialization.json()
        val user = User(2, "c@abc.com", "super", "man", true, 35)
        val text = serializer.serialize(user)
        println(text)
        assert( text == "{\"id\" : 2, \"email\" : \"c@abc.com\", \"firstName\" : \"super\", \"lastName\" : \"man\", \"male\" : true, \"age\" : 35}")
    }


    @Test fun can_serialize_json_objects(){
        val serializer = Serialization.json()
        val user1 = User(2, "c@abc.com", "super", "man", true, 35)
        val user2 = User(3, "b@abc.com", "bat", "man", true, 35)
        val users = listOf(user1, user2)
        val text = serializer.serialize(users)

        val expected = "[" +
                "{\"id\" : 2, \"email\" : \"c@abc.com\", \"firstName\" : \"super\", \"lastName\" : \"man\", \"male\" : true, \"age\" : 35}" + ", "
        "{\"id\" : 3, \"email\" : \"b@abc.com\", \"firstName\" : \"bat\", \"lastName\" : \"man\", \"male\" : true, \"age\" : 35}" + "]"
        println(text == expected)
    }


    @Test fun can_serialize_csv_records() {
        val serializer = Serialization.csv()
        val user1 = User(2, "c@abc.com", "super", "man", true, 35)
        val user2 = User(3, "b@abc.com", "bat", "man", true, 35)
        val users = listOf(user1, user2)
        val text = serializer.serialize(users)
        val expected = """2, "c@abc.com", "super", "man", true, 35""" + newline +
                       """3, "b@abc.com", "bat", "man", true, 35""" + newline + newline

        assert(text == expected)
    }


    @Test fun can_serialize_sample_using_parameters() {
        val serializer = Serialization.sampler()
        val parameters = SerializerTests::sampleParams.parameters.tail()
        val text = serializer.serialize(parameters)
        val expected = """["str" : "abc", "bln" : true, "numI" : 10, "numL" : 100, "date" : "2017-08-20T00:00-04:00[America/New_York]", "user" : {"id" : 100, "email" : "abc", "firstName" : "abc", "lastName" : "abc", "male" : true, "age" : 10}]"""
        assert(text == expected)
    }


    fun sampleParams(str:String, bln:Boolean, numI:Int, numL:Long, date:DateTime, user: User):Unit {

    }
}