package slatekit.apis.tests

import slatekit.apis.*
import slatekit.common.Context


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", roles= ["admin"])
class SampleApi1(val context: Context) {

    @Action(desc = "test simple action with inputs")
    @Documented(path = "docs/apis", key = "actions.tests.repeat")
    fun repeat(word: String, count:Int): String {
        return (0 until count).map { word }.joinToString(" ")
    }
}



@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", roles= ["admin"],
        auth = AuthModes.Token, verb = Verbs.Auto, access = AccessLevel.Public, protocols = [Protocols.All])
class SampleApi2(val context: Context) {

    @Action(desc = "test simple action with inputs")
    @Input(name = "word" , desc = "word to return back", examples = ["hello"])
    @Input(name = "count", desc = "number of times to repeat", examples = ["3"])
    fun repeat(word: String, count:Int): String {
        return (0 until count).map { word }.joinToString(" ")
    }
}


