package test.core

//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.*
import org.junit.Test

class SerializationTests {

    @Test
    fun can_build_sendgrid() {
//        val json = """{ "a" : 1, "b": true, "c": null } """
//        val jsonObject: JsonObject = Json.decodeFromString(json)
//        val user = Json.decodeFromString<User>(json)
//        println(user)
//        println(jsonObject)
//        println("s")
    }

    //@Serializable
    data class User(val a:Int, val b:Boolean, val c:String?)
}