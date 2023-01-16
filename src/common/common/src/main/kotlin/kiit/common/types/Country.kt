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

package kiit.common.types

/**
 * Represents a country with iso code, name, and phone codes for sms features
 *
 * @param name : name of country                 e.g. "U.S"
 * @param iso2 : 2 char iso code                 e.g. "us"
 * @param iso3 : 3 char iso code                 e.g. "usa"
 * @param phoneCode : international dialing code e.g. 1
 * @param length : length of the phone number without phone code
 */
data class Country(val name: String, val iso2: String, val iso3: String, val phoneCode: String, val length: Int, val format:String, val sample:String, val icon:String = "") {

    fun normalize(phone: String): String {
        // Matches length without country phone code ( e.g. U.S "1" )
        return if (phone.length == length) phoneCode + phone
        else phone
    }
}
