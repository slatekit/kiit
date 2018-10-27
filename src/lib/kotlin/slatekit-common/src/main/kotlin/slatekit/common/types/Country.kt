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

package slatekit.common.types

/**
 * Represents a country with iso code, name, and phone codes for sms features
 *
 * @param iso2 : 2 char iso code
 * @param iso3 : 3 char iso code
 * @param phoneCode : international dialing code
 * @param name : name of country
 */
data class Country(val iso2: String, val iso3: String, val phoneCode: String, val phoneLength: Int, val name: String) {

    fun normalize(phone: String): String {
        // Matches length without country phone code ( e.g. U.S "1" )
        return if (phone.length == phoneLength) phoneCode + phone
        else phone
    }
}

