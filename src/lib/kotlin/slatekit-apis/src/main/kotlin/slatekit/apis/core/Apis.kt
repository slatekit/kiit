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

package slatekit.apis.core

import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.common.*


/**
 * Storage and lookup of all the actions in a single API
 */
class Apis(val api: ApiReg, val actions:List<Pair<String,Actions>>) {

    var _lookup = ListMap(actions)


    /**
     * Number of actions
     */
    val size:Int = _lookup.size


    /**
     * gets a list of all the actions / methods this api supports.
     *
     * @return
     */
    fun actions(): ListMap<String, Actions> = _lookup.clone()


    /**
     * whether or not the action exists in this api
     *
     * @param action : e.g. "invite" as in "users.invite"
     * @return
     */
    fun contains(action: String): Boolean = _lookup.contains(action)


    /**
     * gets the value with the supplied key(action)
     *
     * @param action
     * @return
     */
    operator fun get(action: String): Actions? {
        return if (!contains(action))
            null
        else
            _lookup[action]!!
    }


    /**
     * gets the value with the supplied key(action)
     *
     * @param action
     * @return
     */
    fun getOpt(action: String): Actions? = _lookup[action]
}
