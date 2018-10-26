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

package slatekit.common.auth

import slatekit.common.security.ApiKey
import slatekit.common.Inputs
import slatekit.common.ListMap
import slatekit.common.ResultMsg
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.yes


object AuthFuncs {

    val guest = User(id = "guest")
    val emptyRoles = mapOf<String, String>()

    /**
     * determines whether or not there is a valid api key in the inputs (Map like collection) supplied
     * @param inputs      : The inputs ( abstracted Map-like collection )
     * @param keys        : The list of ApiKey available
     * @param inputName   : The name of the key in the inputs containing the ApiKey.key
     *                      NOTES:
     *                      1. This is like the "Authorization" header in http.
     *                      2. In fact, since the HttpRequest is abstracted out via ApiCmd
     *                         in the Api feature ( Protocol Independent APIs ), the inputName
     *                         should be "Authorization" if the protocol is Http
     * @param expectedRoles : The role required on the action being performed
     *                        NOTE: The key if present in inputs and matching one of ApiKeys in lookup
     * @return
     */
    fun isKeyValid(inputs: Inputs?,
                   keys: ListMap<String, ApiKey>,
                   inputName: String,
                   expectedRoles: String): ResultMsg<Boolean> {

        val key = inputs?.getStringOpt(inputName) ?: ""

        // Check 3: Key is non-empty ?
        return if (key.isNullOrEmpty()) {
            no("Api Key not provided or invalid")
        }
        else {
            // Check 4: CHeck if valid key
            if (keys.contains(key))
                validateKey(key, keys, expectedRoles)
            else
                no("Api Key not provided or invalid")
        }
    }


    /**
     * matches the expected roles with the actual roles
     * @param expectedRole : "dev,ops,admin"
     * @param actualRoles  : Map of actual roles the user has.
     * @return
     */
    fun matchRoles(expectedRole: String, actualRoles: Map<String, String>): ResultMsg<Boolean> {
        // 1. No roles ?
        val anyRoles = actualRoles.isNotEmpty()
        return if (!anyRoles) {
            unAuthorized()
        }
        // 2. Any role "*"
        else if (expectedRole == Roles.all) {
            if(actualRoles.isNotEmpty()) yes() else unAuthorized()
        }
        else {
            // 3. Get all roles "dev,moderator,admin"
            val expectedRoles = expectedRole.split(',')

            // 4. Now compare
            val matches = expectedRoles.filter { role -> actualRoles.contains(role) }
            if (matches.isNotEmpty())
                yes()
            else
                unAuthorized()
        }
    }


    /**
     * gets the primary value supplied unless it references the parent value via "@parent"
     * @param primaryValue
     * @param parentValue
     * @return
     */
    fun getReferencedValue(primaryValue: String, parentValue: String): String =
            if (!primaryValue.isNullOrEmpty()) {
                if (primaryValue == Roles.parent) {
                    parentValue
                }
                else
                    primaryValue
            }
            // Parent!
            else if (!parentValue.isNullOrEmpty()) {
                parentValue
            }
            else
                ""


    /**
     * Converts api keys supplied into a listmap ( list + map ) of Api Keys using the api.key as key
     * @param keys
     * @return
     */
    fun convertKeys(keys: List<ApiKey>): ListMap<String, ApiKey> {
        val items = keys.map { it -> Pair<String, ApiKey>(it.key, it) }
        return ListMap(items)
    }


    /**
     * converts a comma delimited string of roles to an immutable map of role:String -> boolean:true
     * @param roles
     * @return
     */
    fun convertRoles(roles: String): Map<String, Boolean> {
        return roles.split(',').map { it to true }.toMap()
    }


    private fun validateKey(key: String, keys: ListMap<String, ApiKey>, expectedRoles: String): ResultMsg<Boolean> {

        // Now ensure that key contains roles matching one provided.
        val apiKey = keys[key]

        // Now match the roles.
        return matchRoles(expectedRoles, apiKey?.rolesLookup ?: emptyRoles)
    }
}
