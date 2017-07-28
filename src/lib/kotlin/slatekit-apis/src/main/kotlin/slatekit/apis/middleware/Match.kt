/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.middleware

/**
 * Represents a way to match on a route
 * @param area
 * @param api
 * @param action
 */
data class Match(val area: String?, val name: String?, val action: String?) {

    /**
     * represents a match on everything
     */
    val isGlobal: Boolean = area.isNullOrEmpty() && name.isNullOrEmpty() && action.isNullOrEmpty()


    /**
     * represents a match on a specific area only
     */
    val isArea: Boolean = !area.isNullOrEmpty() && name.isNullOrEmpty()


    /**
     * represents a match on a specific name only
     */
    val isApi: Boolean = !area.isNullOrEmpty() && !name.isNullOrEmpty() && action.isNullOrEmpty()


    /**
     * represents a match on a specific action only
     */
    val isAction: Boolean = !area.isNullOrEmpty() && !name.isNullOrEmpty() && !action.isNullOrEmpty()


    /**
     * the full path representing the match
     * @return
     */
    fun fullName(): String {
        //val path = List(area, name, action).fold[String]("")( (a, b) => a + "/" + b)
        //path
        return ""
    }


    /**
     * whether this name match matches the area/name/action supplied.
     * @param targetArea
     * @param targetApi
     * @param targetAction
     * @return
     */
    fun isMatch(targetArea: String, targetApi: String, targetAction: String): Boolean {
        return if (isGlobal)
            true
        else if (isArea && this.area != targetArea)
            false
        else if (isApi && this.name != targetApi)
            false
        else
            isAction && this.action != targetAction
    }
}