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

/**
 * Represents a user for authentication purposes
 * @param id : User id
 * @param fullName : Full name of user
 * @param firstName : First name
 * @param lastName : Last name
 * @param email : Email
 * @param phone : Primary phone
 * @param isPhoneVerified : Whether the phone is verified
 * @param isDeviceVerified : Whether the device is verified
 * @param isEmailVerified : Whether the email is verified
 * @param city : The city where user is in
 * @param state : The state where user is in
 * @param zip : The zip where user is in
 * @param country : The country where user is in ( 2 digit county code )
 * @param region : The region of the user ( can use as a shard )
 * @param tag : A tag used for external links
 * @param version : The schema version of this model
 */
data class User(
    val id: String = "",
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val isPhoneVerified: Boolean = false,
    val isDeviceVerified: Boolean = false,
    val isEmailVerified: Boolean = false,
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "",
    val region: String = "",
    val tag: String = "",
    val version: String = "",
    val token: String = ""
) {

    fun isMatch(user: User): Boolean = user.id == this.id
}
