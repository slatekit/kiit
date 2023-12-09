package kiit.apis.setup

import kiit.apis.*
import kiit.apis.routes.Action
import kiit.common.Source
import java.util.*


fun toVerb(name: String?): Verb {
    return when (name) {
        null -> Verb.Auto
        else -> {
            val nameToCheck = name.lowercase(Locale.getDefault())
            val verb = when {
                nameToCheck.startsWith(Verbs.AUTO)        -> Verb.Auto
                nameToCheck.startsWith(Verbs.GET)         -> Verb.Get
                nameToCheck.startsWith(Verbs.POST)        -> Verb.Post
                nameToCheck.startsWith(Verbs.PUT)         -> Verb.Put
                nameToCheck.startsWith(Verbs.PATCH)       -> Verb.Patch
                nameToCheck.startsWith(Verbs.DELETE)      -> Verb.Delete
                nameToCheck.startsWith(Verbs.CREATE)      -> Verb.Post
                nameToCheck.startsWith(Verbs.UPDATE)      -> Verb.Put
                nameToCheck.startsWith("add")       -> Verb.Post
                nameToCheck.startsWith("insert")    -> Verb.Post
                nameToCheck.startsWith("upsert")    -> Verb.Put
                nameToCheck.startsWith("purge")     -> Verb.Delete
                nameToCheck.startsWith("remove")    -> Verb.Delete
                else -> Verb.Post
            }
            verb
        }
    }
}


fun validate(action: Action, hostSource: Source):Boolean {
    val include = when(hostSource) {
        Source.All -> true
        Source.CLI -> action.sources.isMatchOrAll(Source.CLI) || action.sources.isMatchOrAll(Source.Queue)
        Source.Web -> action.sources.isMatchOrAll(listOf(Source.Web, Source.API, Source.Queue))
        Source.API -> action.sources.isMatchOrAll(listOf(Source.Web, Source.API, Source.Queue))
        else       -> false
    }
    //println("filtering name=${action.name}, include=$include")
    return include
}
