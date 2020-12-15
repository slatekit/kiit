package slatekit.apis.setup

import kotlin.reflect.KClass
import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.apis.routes.Lookup
import slatekit.common.Ignore
import slatekit.common.naming.Namer
import slatekit.meta.Reflector

class AnnoLoader(val cls: KClass<*>, val raw: Api? = null) : Loader {
    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     * @param cls : The class representing the API
     * @param namer: The naming convention
     */
    override fun loadApi(namer: Namer?): Api {
        val api = toApi(cls, raw?.singleton, raw?.access, namer)

        // Get all the actions using the @ApiAction
        val actions = loadActions(api, false, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }

    override fun loadActions(api: Api, local: Boolean, namer: Namer?): List<Action> {

        // 1. get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembers<slatekit.apis.Action>(api.klass, slatekit.apis.Action::class, api.declaredOnly)
        val rawIgnores = Reflector.getAnnotatedMembers<Ignore>(api.klass, Ignore::class, api.declaredOnly)
        val rawIgnoresLookup = rawIgnores.map { it -> Pair(it.first.name, true) }.toMap()

        // 2. Filter out builtin methods
        val matches = rawMatches.filter { it -> !Reflector.isBuiltIn(it.first) }

        // 3. Convert to Action
        val actions: List<Action?> = matches.map { item ->

            // a) The member
            val member = item.first

            // Ensure it does not have an Ignore annotation
            if (rawIgnoresLookup.containsKey(member.name)) {
                null
            } else {
                toAction(item.first, api, item.second, namer)
            }
        }

        // 4. Filter out ignored ones.
        return actions.filterNotNull()
    }
}
