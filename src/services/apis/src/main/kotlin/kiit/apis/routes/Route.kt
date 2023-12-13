package kiit.apis.routes

/**
 * Represents the 3 part routing convention in Kiit API actions.
 * Example:
 * 1. format: {area}/{api}/{action}
 * 2. sample: content/blog/create
 *
 * @param area   : 1st level logical group that the api belongs to
 * @param api    : 2nd level logical group of related actions/endpoints
 * @param action : 3rd level executable portion that maps to a method
 */
data class Path(val area:Area, val api:Api, val action: Action) {
    val name:String = "${area.name}/${api.name}/${action.name}"
}


/**
 * Represents a mapping of the path and handle to execute action in the path
 * Example:
 * content/blog/create -> MethodExecutor( BlogApi::create method )
 */
data class Route(val path: Path, val handler: RouteHandler)
