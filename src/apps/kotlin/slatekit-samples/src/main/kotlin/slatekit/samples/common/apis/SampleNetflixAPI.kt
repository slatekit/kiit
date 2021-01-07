package slatekit.samples.common.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.ApiBase
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.EnumLike
import slatekit.common.requests.Request
import slatekit.context.Context
import slatekit.common.Sources
import slatekit.common.ext.toId
import slatekit.common.ext.toStringUtc
import slatekit.common.info.About
import slatekit.common.types.Doc
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.samples.common.models.SampleMovie


@Api(area = "netflix", name = "search", desc = "sample to test features of Slate Kit APIs", auth = AuthModes.NONE, verb = Verbs.AUTO, sources = [Sources.WEB])
class SampleNetflixApi(context: Context) : ApiBase(context) {

    val listings = listOf(
            Series("Breaking bad"    , "drama", 2008,5),
            Series("Better call saul", "drama", 2012, 5),
            Movie("Star Trek"    , "scifi" , 2012),
            Movie("Batman Begins", "action", 2002),
            Movie("Contact"      , "scifi" , 1997),
            Movie("Indiana Jones", "action", 1988)
    )


    @Action(desc = "Get all movies in the supplied category e.g. action, scifi")
    fun movies(category:String):List<Show> {
        val categoryClean = category.toLowerCase().trim()
        return listings.filter { it is Movie && it.category == categoryClean }
    }


    @Action(desc = "Get all listings by most recent year ( published )")
    fun recent(limit:Int):List<Show> {
        return listings.sortedBy { it.year }.take(limit)
    }


    @Action(desc = "test post")
    fun create(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test put")
    fun update(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test post")
    fun process(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test delete")
    fun delete(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test patch")
    fun patch(greeting: String): String {
        return "$greeting back"
    }
}


interface Show {
    val name:String
    val category:String
    val year:Int
}

class Series(override val name:String, override val category: String, override val year:Int, val seasons:Int) : Show
class Movie (override val name:String, override val category: String, override val year:Int) : Show
