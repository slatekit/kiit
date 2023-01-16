package test.setup

import slatekit.apis.Api
import kiit.common.DateTime
import kiit.requests.Request


@Api(area = "samples", name = "types2", desc = "sample api to test sending different types")
class SampleTypes2Api {

    fun loadBasicTypes(s: String, b: Boolean, i: Int, d: DateTime): String = "$s, $b, $i, $d"

    fun loadNumbers(s: Short, i: Int, l: Long, d: Double): String = "$s, $i, $l, $d"

    fun loadRequest(req: Request): String = "raw request with path: ${req.path}"

    fun loadObject(movie: Movie): Movie = movie

    fun loadObjectlist(movies:List<Movie>): List<Movie> = movies

    fun loadListString(items:List<String>): String = items.fold("", { acc, curr -> acc + "," + curr } )

    fun loadListInt(items:List<Int>): String = items.fold("", { acc, curr -> acc + "," + curr.toString() } )

    fun loadMapInt(items:Map<String,Int>): String {
        val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
        return delimited
    }
}
