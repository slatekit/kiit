package test.setup

import kiit.apis.Action
import kiit.apis.Api
import kiit.common.DateTime
import kiit.requests.Request


@Api(area = "samples", name = "types2", desc = "sample api to test sending different types")
class SampleTypes2Api {

    @Action()
    fun loadBasicTypes(s: String, b: Boolean, i: Int, d: DateTime): String = "$s, $b, $i, $d"

    @Action()
    fun loadNumbers(s: Short, i: Int, l: Long, d: Double): String = "$s, $i, $l, $d"

    @Action()
    fun loadRequest(req: Request): String = "raw request with path: ${req.path}"

    @Action()
    fun loadObject(movie: Movie): Movie = movie

    @Action()
    fun loadObjectlist(movies:List<Movie>): List<Movie> = movies

    @Action()
    fun loadListString(items:List<String>): String = items.fold("", { acc, curr -> acc + "," + curr } )

    @Action()
    fun loadListInt(items:List<Int>): String = items.fold("", { acc, curr -> acc + "," + curr.toString() } )

    @Action()
    fun loadMapInt(items:Map<String,Int>): String {
        val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
        return delimited
    }
}
