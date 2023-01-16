package test.meta

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert
import org.junit.Test
import kiit.common.convert.Converter
import kiit.common.DateTimes
import kiit.common.ext.toStringUtc
import kiit.meta.InputsJSON
import test.setup.Movie
import kotlin.reflect.full.createType

class ConverterTests {

    val movie = Movie(123, "Dark Knight", "Action", false, 10, 4.5, DateTimes.of(2012, 7, 4, 16, 0, 0))


    @Test
    fun convertJSON(){
        val converter = MovieConverter()
        val json = converter.convert(movie)
        Assert.assertNotNull(json)
        Assert.assertEquals(json?.get("id")       , movie.id       )
        Assert.assertEquals(json?.get("title")    , movie.title    )
        Assert.assertEquals(json?.get("category") , movie.category )
        Assert.assertEquals(json?.get("playing")  , movie.playing  )
        Assert.assertEquals(json?.get("cost")     , movie.cost     )
        Assert.assertEquals(json?.get("rating")   , movie.rating   )
        Assert.assertEquals(json?.get("released") , movie.released.toStringUtc())
    }

    @Test
    fun restoreJSON(){
        val json = """
            {
                "id": 123,
                "title": "Dark Knight",
                "category": "Action",
                "playing": false,
                "cost": 10,
                "rating": 4.5,
                "released": "2012-07-04T16:00:00Z"
            }
        """.trimIndent()
        val jsonObject = JSONParser().parse(json) as JSONObject
        val converter = MovieConverter()
        val item = converter.restore(jsonObject)
        Assert.assertNotNull(json)
        Assert.assertEquals( movie.id       , item?.id       )
        Assert.assertEquals( movie.title    , item?.title    )
        Assert.assertEquals( movie.category , item?.category )
        Assert.assertEquals( movie.playing  , item?.playing  )
        Assert.assertEquals( movie.cost     , item?.cost     )
        Assert.assertEquals( movie.rating   , item?.rating   )
        Assert.assertEquals( movie.released.toStringUtc() , item?.released?.toStringUtc() )
    }
}


class MovieConverter : Converter<Movie, JSONObject> {
    val type = Movie::class.createType()

    override val cls = Movie::class.java


    override fun convert(input: Movie?): JSONObject? {
        return input?.let {
            val root = JSONObject()
            root["id"]       = it.id
            root["title"]    = it.title
            root["category"] = it.category
            root["playing"]  = it.playing
            root["cost"]     = it.cost
            root["rating"]   = it.rating
            root["released"] = it.released.toStringUtc()
            root
        }
    }

    override fun restore(output: JSONObject?): Movie? {
        return output?.let {
            val doc = output
            val inputs = InputsJSON(doc, null, doc)
            val movie = Movie(
                    id       = inputs.getLong("id"),
                    title    = inputs.getString("title"),
                    category = inputs.getString("category"),
                    playing  = inputs.getBool("playing"),
                    cost     = inputs.getInt("cost"),
                    rating   = inputs.getDouble("rating"),
                    released = inputs.getDateTime("released")
            )
            movie
        }
    }
}