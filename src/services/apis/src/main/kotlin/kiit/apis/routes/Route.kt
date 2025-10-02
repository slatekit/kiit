package kiit.apis.routes

/**
 * Represents a mapping of the path and handle to execute action in the path
 * A path is the 3 part routing convention in Kiit API actions.
 * Example:
 * 1. format: {area}/{api}/{action}
 * 2. sample: content/blog/create
 *
 * @param area   : 1st level logical group that the api belongs to
 * @param api    : 2nd level logical group of related actions/endpoints
 * @param action : 3rd level executable portion that maps to a method
 * Example:
 * content/blog/create -> MethodExecutor( BlogApi::create method )
 */
data class Route(val area:Area, val api:Api, val action: Action, val handler: RouteHandler) {
    val path:String = "${area.name}/${api.name}/${action.name}"
}
