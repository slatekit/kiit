package test.setup

import slatekit.apis.*
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.ApiBase
import kiit.context.Context
import kiit.common.Sources
import slatekit.connectors.entities.AppEntContext


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
    auth = AuthModes.TOKEN, roles= ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class SampleApi(context: AppEntContext): ApiBase(context) {

    @Action(desc = "accepts supplied basic data types from send")
    fun defaultAnnotationValues(string1: String): String {
        return "$string1"
    }


    @Action(desc = "test partial override", auth = AuthModes.KEYED, roles= ["user"])
    fun overridePartial(string1: String): String {
        return "$string1"
    }


    @Action(desc = "test overrides", auth = AuthModes.KEYED, roles= ["user"], sources = [Sources.CLI], access = AccessLevel.INTERNAL)
    fun overrideFull(string1: String): String {
        return "$string1"
    }
}



@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", roles= ["admin"])
class SampleApi1(val context: Context) {

    @Action(desc = "test simple action with inputs")
    @Documented(path = "docs/apis", key = "actions.tests.repeat")
    fun repeat(word: String, count:Int): String {
        return (0 until count).map { word }.joinToString(" ")
    }
}



@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", roles= ["admin"],
        auth = AuthModes.TOKEN, verb = Verbs.AUTO, access = AccessLevel.PUBLIC, sources = [Sources.ALL])
class SampleApi2(val context: Context) {

    @Action(desc = "test simple action with inputs")
    @Input(name = "word" , desc = "word to return back", required = true, examples = ["hello"])
    @Input(name = "count", desc = "number of times to repeat", required = true, examples = ["3"])
    fun repeat(word: String, count:Int): String {
        return (0 until count).map { word }.joinToString(" ")
    }
}


