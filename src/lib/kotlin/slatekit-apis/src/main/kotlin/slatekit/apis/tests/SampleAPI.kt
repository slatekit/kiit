package slatekit.apis.tests

import slatekit.apis.*
import slatekit.apis.setup.*
import slatekit.common.Context


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
        roles= "admin", auth = AuthModes.token, verb = Verbs.auto, protocol = Protocols.all, access = AccessLevel.Public)
class SampleApi(val context: Context) {

    @Action(desc = "accepts supplied basic data types from send")
    @Arg(name = "word" , desc = "word to return back", required = true, defaultVal = "", eg = "hello")
    @Arg(name = "count", desc = "number of times to repeat", required = true, defaultVal = "", eg = "3")
    fun repeat(word: String, count:Int): String {
        return (0 until count).map { word }.joinToString(" ")
    }
}