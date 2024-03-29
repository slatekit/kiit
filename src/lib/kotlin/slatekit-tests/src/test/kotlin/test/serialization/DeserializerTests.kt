package test.serialization

import org.json.simple.JSONObject
import org.junit.Assert
import org.junit.Test
import kiit.common.DateTime
import kiit.requests.InputArgs
import kiit.common.utils.Random
import kiit.common.crypto.EncDouble
import kiit.common.crypto.EncInt
import kiit.common.crypto.EncLong
import kiit.common.crypto.EncString
import test.setup.Movie
import org.threeten.bp.*
import kiit.apis.core.Transformer
import kiit.common.DateTimes
import kiit.requests.CommonRequest
import kiit.common.Source
import kiit.requests.Request
import kiit.meta.*
import kiit.serialization.deserializer.json.JsonDeserializer
import test.setup.MyEncryptor
import test.setup.StatusEnum
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class DeserializerTests {

//    @Test
//    fun testJSON() {
//        val test = """{
//                "tstr": "abc",
//                "tbool": false,
//                "movie": {
//                    "id": 123,
//                    "title": "dark knight",
//                    "category": "action",
//                    "playing": false,
//                    "cost": 15,
//                    "rating": 4.5,
//                    "released": "2012-07-04T18:00:00Z"
//                }
//            }""".trimIndent()
//        val json = org.json.JSONObject(test)
//        val jar = json.getJSONArray("")
//        val json2 = org.json.simple.parser.JSONParser().parse(test) as JSONObject
//        val jar2 = json2.get("") as org.json.simple.JSONArray
//
//    }

    fun test_basic_types(tstr:String, tbool:Boolean, tshort:Short, tint:Int, tlong:Long, tdoub:Double):Unit {}
    @Test fun can_parse_basictypes(){
        val test = """{ "tstr": "abc", "tbool": false, "tshort": 1, "tint": 12, "tlong": 123, "tdoub": 123.45 }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_basic_types.parameters, test)
        Assert.assertTrue(results[0] == "abc")
        Assert.assertTrue(results[1] == false)
        Assert.assertTrue(results[2] == 1.toShort())
        Assert.assertTrue(results[3] == 12)
        Assert.assertTrue(results[4] == 123L)
        Assert.assertTrue(results[5] == 123.45)
    }


    fun test_dates(tdate: LocalDate, ttime: LocalTime, tlocaldatetime: LocalDateTime, tdatetime: DateTime):Unit{}
    @Test fun can_parse_dates(){
        val test = """{ "tdate": "2017-07-06", "ttime": "10:30:45", "tlocaldatetime": "2017-07-06T10:30:45", "tdatetime": "201707061030" }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_dates.parameters, test)
        Assert.assertTrue(results[0] == LocalDate.of(2017, 7, 6))
        Assert.assertTrue(results[1] == LocalTime.of(10,30,45))
        Assert.assertTrue(results[2] == LocalDateTime.of(2017,7,6, 10,30, 45))
        Assert.assertTrue(results[3] == DateTimes.of(2017,7,6, 10,30))
    }


    fun test_uuid(uid: UUID) {}
    @Test fun can_parse_uuids(){
        val uuid = Random.uuid()
        val test = """{ "uid": "$uuid" }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_uuid.parameters, test)
        Assert.assertTrue(results[0] == UUID.fromString(uuid))
    }


    fun test_enum(status: StatusEnum) {}
    @Test fun can_parse_enum(){
        val enumVal = StatusEnum.Active
        val test = """{ "status": ${enumVal.value} }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_enum.parameters, test)
        Assert.assertTrue(results[0] == enumVal)
    }


    fun test_decrypted(decString: EncString, decInt: EncInt, decLong: EncLong, decDouble: EncDouble):Unit {}
    @Test fun can_parse_decrypted(){

        val decStr = MyEncryptor.encrypt("abc123")
        val decInt = MyEncryptor.encrypt("123")
        val decLong = MyEncryptor.encrypt("12345")
        val decDoub = MyEncryptor.encrypt("12345.67")

        val test = """{ "decString": "$decStr", "decInt": "$decInt", "decLong": "$decLong", "decDouble": "$decDoub" }"""
        val deserializer = JsonDeserializer(MyEncryptor)
        val results = deserializer.deserialize(this::test_decrypted.parameters, test)
        Assert.assertTrue((results[0] as EncString).value == "abc123")
        Assert.assertTrue((results[1] as EncInt).value == 123)
        Assert.assertTrue((results[2] as EncLong).value == 12345L)
        Assert.assertTrue((results[3] as EncDouble).value == 12345.67)
    }


    fun test_arrays(strings: List<String>, bools:List<Boolean>, ints:List<Int>, longs:List<Long>, doubles:List<Double>):Unit {}
    @Test fun can_parse_arrays(){
        val test = """{ "strings": ["a", "b", "c"], "bools": [true, false, true], "ints": [1,2,3], "longs": [100,200,300], "doubles": [1.2,3.4,5.6] }"""
        val deserializer = JsonDeserializer(MyEncryptor)
        val results = deserializer.deserialize(this::test_arrays.parameters, test)
        Assert.assertTrue((results[0] as List<String>)[0] == "a")
        Assert.assertTrue((results[0] as List<String>)[1] == "b")
        Assert.assertTrue((results[0] as List<String>)[2] == "c")
        Assert.assertTrue((results[1] as List<Boolean>)[0] == true)
        Assert.assertTrue((results[1] as List<Boolean>)[1] == false)
        Assert.assertTrue((results[1] as List<Boolean>)[2] == true)
        Assert.assertTrue((results[2] as List<Int>)[0] == 1)
        Assert.assertTrue((results[2] as List<Int>)[1] == 2)
        Assert.assertTrue((results[2] as List<Int>)[2] == 3)
        Assert.assertTrue((results[3] as List<Long>)[0] == 100L)
        Assert.assertTrue((results[3] as List<Long>)[1] == 200L)
        Assert.assertTrue((results[3] as List<Long>)[2] == 300L)
        Assert.assertTrue((results[4] as List<Double>)[0] == 1.2)
        Assert.assertTrue((results[4] as List<Double>)[1] == 3.4)
        Assert.assertTrue((results[4] as List<Double>)[2] == 5.6)
    }


    data class SampleObject1(val tstr:String, val tbool:Boolean, val tshort:Short, val tint:Int, val tlong:Long, val tdoub:Double)
    fun test_object(sample1: SampleObject1):Unit{}
    @Test fun can_parse_object(){
        val test = """{ "sample1": { "tstr": "abc", "tbool": false, "tshort": 1, "tint": 12, "tlong": 123, "tdoub": 123.45 } }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_object.parameters, test)
        Assert.assertTrue(results[0] == SampleObject1("abc", false, 1, 12, 123, 123.45))
    }


    fun test_object_list(items:List<SampleObject1>):Unit{}
    @Test fun can_parse_object_lists(){
        val test = """{ "items":
        [
            { "tstr": "abc", "tbool": false, "tshort": 1, "tint": 12, "tlong": 123, "tdoub": 123.45 },
            { "tstr": "def", "tbool": true , "tshort": 2, "tint": 34, "tlong": 456, "tdoub": 678.91 }
        ]}"""
        val deserializer = JsonDeserializer()
        val inputs = deserializer.deserialize(this::test_object_list.parameters, test)
        val results = inputs.get(0) as ArrayList<*>
        println(results)
        Assert.assertTrue(results[0] == SampleObject1("abc", false, 1, 12, 123, 123.45))
        Assert.assertTrue(results[1] == SampleObject1("def", true, 2, 34, 456, 678.91))
    }


    data class NestedObject1(val name:String, val items:List<SampleObject1>)
    fun test_nested_object_list(str:String, item: NestedObject1):Unit{}
    @Test fun can_parse_object_lists_nested(){
        val test = """{
        "str"  : "abc",
        "item":
            {
                "name": "nested_objects",
                "items":
                [
                    { "tstr": "abc", "tbool": false, "tshort": 1, "tint": 12, "tlong": 123, "tdoub": 123.45 },
                    { "tstr": "def", "tbool": true , "tshort": 2, "tint": 34, "tlong": 456, "tdoub": 678.91 }
                ]
            }
        }"""
        val deserializer = JsonDeserializer()
        val results = deserializer.deserialize(this::test_nested_object_list.parameters, test)
        val item = results[1] as NestedObject1
        Assert.assertTrue(results[0] == "abc")
        Assert.assertTrue(item.items[0] == SampleObject1("abc", false, 1, 12, 123, 123.45))
        Assert.assertTrue(item.items[1] == SampleObject1("def", true, 2, 34, 456, 678.91))
    }


    fun test_custom_converter(tstr:String, tbool:Boolean, movie: Movie):Unit {}

    @Test fun can_parse_custom_types_using_lambda_decoder(){
        val test = """{
                "tstr": "abc",
                "tbool": false,
                "movie": {
                    "id": 123,
                    "title": "dark knight",
                    "category": "action",
                    "playing": false,
                    "cost": 15,
                    "rating": 4.5,
                    "released": "2012-07-04T18:00:00Z"
                }
            }""".trimIndent()
        val req = CommonRequest("a.b.c", listOf("a", "b", "c"), Source.CLI, "post",
                InputArgs(mapOf()), InputArgs(mapOf(Pair("movie", "batman"))))

        val decoder = Transformer(Movie::class.java, null) { _, _ ->
            Movie(0L, "batman", cost = 0, rating = 4.0, released = DateTime.now())
        }
        val deserializer = JsonDeserializer(null, mapOf(Pair(Movie::class.qualifiedName!!, decoder)))
        val results = deserializer.deserialize(this::test_custom_converter.parameters, test)
        Assert.assertTrue(results[0] == "abc")
        Assert.assertTrue(results[1] == false)
        Assert.assertTrue(results[2] is Movie )
        Assert.assertTrue((results[2] as Movie ).title == "batman")
    }

    @Test fun can_parse_custom_types_using_simple_decoder(){
        val test = """{
                "tstr": "abc",
                "tbool": false,
                "movie": {
                    "id": 123,
                    "title": "dark knight",
                    "category": "action",
                    "playing": false,
                    "cost": 15,
                    "rating": 4.5,
                    "released": "2012-07-04T18:00:00Z"
                }
            }""".trimIndent()
        val req = CommonRequest("a.b.c", listOf("a", "b", "c"), Source.CLI, "post",
                InputArgs(mapOf()), InputArgs(mapOf(Pair("movie", "batman"))))

        val deserializer = JsonDeserializer(null, mapOf(Pair(Movie::class.qualifiedName!!, MovieDecoder())))
        val results = deserializer.deserialize(this::test_custom_converter.parameters, test)
        Assert.assertTrue(results[0] == "abc")
        Assert.assertTrue(results[1] == false)
        Assert.assertTrue(results[2] is Movie )
        Assert.assertTrue((results[2] as Movie ).title == "dark knight")
        Assert.assertTrue((results[2] as Movie ).category == "action")
        Assert.assertTrue(!(results[2] as Movie ).playing)
        Assert.assertTrue((results[2] as Movie ).cost == 15)
        Assert.assertTrue((results[2] as Movie ).rating == 4.5)
    }


    class MovieDecoder : Transformer<Movie>(Movie::class.java) {

        override fun restore(output: JSONObject?): Movie? {
            return output?.let {
                val doc = output
                val inputs = InputsJSON(doc, null, doc)
                val movie = Movie(
                        id = inputs.getLong("id"),
                        title = inputs.getString("title"),
                        category = inputs.getString("category"),
                        playing = inputs.getBool("playing"),
                        cost = inputs.getInt("cost"),
                        rating = inputs.getDouble("rating"),
                        released = inputs.getDateTime("released")
                )
                movie
            }
        }
    }


    fun test_context_converter(actor: Self, tstr:String, tbool:Boolean):Unit {}


    data class Self(val uuid:String)
    class JWTSelfDecoder : Transformer<Self>(Self::class.java), JSONRestoreWithContext<Self>{

        override fun <T> restore(ctx: T, model: JSONObject?, key:String): Self? {
            if(ctx !is Request) throw Exception("Request not available")
            if(!ctx.meta.containsKey("Authorization")) throw Exception("JWT Not found")

            // Simple extractor of uuid from JWT for test purpose.
            val token = ctx.meta.getString("Authorization")
            val parts = token.split(".")
            val uuidFromToken = parts[1]

            val exists = model?.containsKey(key) ?: false
            val tokenFinal = when(exists) {
                true -> model?.get(key) as String
                false -> uuidFromToken
            }
            return Self(tokenFinal)
        }
    }


    @Test fun can_check_types(){
        val params = this::test_arrays.parameters
        val first = params[0]
        println(first.type)
        val tpe = first.type
        val listCls = tpe.classifier as KClass<*>
        println(listCls == List::class)
        val paramType: KType = tpe.arguments[0].type!!

        val cls = tpe.classifier as KClass<*>
        println(cls.toString())
        println(cls.qualifiedName)
    }


    fun printInfo(item:Any?):Unit {
        println(item)
    }
}
