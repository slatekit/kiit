/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.comms.sms

import kiit.utils.templates.Templates
import kiit.common.types.Countries
import kiit.common.values.Vars
import kiit.common.types.Country
import kiit.http.HttpRPC
import kiit.comms.common.TemplateSender
import kiit.results.*

/**
 * Sms Service base class with support for templates and countries
 * @param templates : The templates for the messages
 * @param ctns : The supported countries ( Defaults to US )
 */
abstract class SmsService(
    override val templates: Templates? = null,
    val countries:List<Country> = listOf(Countries.usa),
    override val client: HttpRPC = HttpRPC()
) : TemplateSender<SmsMessage> {

    /**
     * Default the supported countries to just USA
     */
    protected val countryLookup = countries.map { c -> c.iso2 to c }.toMap()

    /**
     * sends a message via an IO wrapper that can be later called.
     *
     * @param message : message to send
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     */
    suspend fun send(message: String, countryCode: String, phone: String): Outcome<String> {
        return send(SmsMessage(message, countryCode, phone))
    }

    /**
     * sends a message using template and variables supplied using an IO wrapper that can be called
     *
     * @param name : name of the template
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     * @param variables : values to replace the variables in template
     */
    suspend fun sendTemplate(name: String, countryCode: String, phone: String, variables: Vars): Outcome<String> {
        return sendTemplate(name, variables) { SmsMessage(it, countryCode, phone) }
    }

    /**
     * massages the phone number to include the iso code if not supplied
     * @param iso
     * @param phone
     * @return
     */
    open fun massagePhone(iso: String, phone: String): Notice<String> {
        val finalIso = iso.toUpperCase()

        val result = validate(finalIso, phone)

        // Case 1: Invalid params
        return if (!result.success) {
            Failure(result.desc)
        }
        // Case 2: Invalid iso or unsupported
        else if (!countryLookup.contains(finalIso)) {
            Failure("$finalIso is not a valid country code")
        }
        // Case 3: Inputs valid so massage
        else {
            val country = countryLookup[finalIso]
            val finalPhone = country?.normalize(phone)
            Success(finalPhone ?: phone)
        }
    }

    private fun validate(countryCode: String, phone: String): Notice<String> =
            when {
                countryCode.isNullOrEmpty() -> Failure("country code not provided")
                phone.isNullOrEmpty() -> Failure("phone not provided")
                else -> Success("")
            }
}
