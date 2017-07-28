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

package slatekit.apis.helpers

import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.common.*


/**
 * Provides lookup functionality to check for exposed api actions.
 * @param api: The APIReg that represents the metadata for 1 API/Class
 */
class ApiLookup(val api: ApiReg) {

    var _lookup = ListMap<String, ApiRegAction>()


    /**
     * gets a list of all the actions / methods this api supports.
     *
     * @return
     */
    fun actions(): ListMap<String, ApiRegAction> = _lookup.clone()


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
    operator fun get(action: String): ApiRegAction? {
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
    fun getOpt(action: String): ApiRegAction? = _lookup[action]


    /**
     * adds a key/value to this collection
     *
     * @param action
     * @param value
     */
    fun update(action: String, value: ApiRegAction): Unit {
        _lookup = _lookup.add(action, value)
    }
}
