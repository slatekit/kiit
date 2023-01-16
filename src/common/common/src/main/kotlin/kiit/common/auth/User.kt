/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.common.auth

/**
 * Represents a user for authentication purposes
 * @param id               : User id
 * @param userName         : User name of user
 * @param displayName      : Display name of user
 * @param fullName         : Full name of user
 * @param firstName        : First name
 * @param lastName         : Last name
 * @param email            : Email
 * @param phone            : Primary phone
 * @param isPhoneVerified  : Whether the phone is verified
 * @param isDeviceVerified : Whether the device is verified
 * @param isEmailVerified  : Whether the email is verified
 * @param country          : The country where user is in ( 2 digit county code )
 * @param schema           : The schema version of this model
 * @param tag              : A tag used for external / correlation ids
 */
open class User(
        val id: String,
        val userName: String,
        val displayName: String,
        val fullName: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val phone: String = "",
        val country: String = "",
        val isPhoneVerified: Boolean = false,
        val isDeviceVerified: Boolean = false,
        val isEmailVerified: Boolean = false,
        val schema: String = "1",
        val tag: String = "",
        val fields: List<Pair<String, Any?>> = listOf()
) {


    /**
     * NOTE: Perhaps a better hashCode / equals implementation ??
     */
    override fun hashCode(): Int {
        return this.javaClass.hashCode() * this.id.hashCode()
    }


    /**
     * NOTE: Perhaps a better hashCode / equals implementation ??
     */
    override fun equals(other: Any?): Boolean {
        return when(other) {
            is User -> this.id == other.id
            else    -> false
        }
    }


    companion object {
        @JvmField
        val empty = User("", "", "")

        @JvmField
        val guest = User("guest", "guest", "guest")

    }
}
