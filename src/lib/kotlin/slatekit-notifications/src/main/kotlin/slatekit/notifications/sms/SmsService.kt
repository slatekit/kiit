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

package slatekit.notifications.sms

import slatekit.common.templates.Templates
import slatekit.common.types.Countries
import slatekit.common.types.CountryCode
import slatekit.common.Vars
import slatekit.notifications.common.Sender
import slatekit.results.*
import slatekit.results.builders.Outcomes

/**
 * Sms Service base class with support for templates and countries
 * @param templates : The templates for the messages
 * @param ctns : The supported countries ( Defaults to US )
 */
abstract class SmsService(
    val templates: Templates? = null,
    ctns: List<CountryCode>? = null
) : Sender<SmsMessage> {

    /**
     * Default the supported countries to just USA
     */
    val countries = Countries.filter(ctns ?: listOf(CountryCode("US"))).map { c -> c.iso2 to c }.toMap()

    /**
     * sends a message via an IO wrapper that can be later called.
     *
     * @param message : message to send
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     */
    fun send(message: String, countryCode: String, phone: String): Outcome<String> {
        val validationResult = validate(countryCode, phone)
        val result = validationResult.then {
            sendSync(SmsMessage(message, countryCode, phone))
        }.toOutcome()
        return result
    }

    /**
     * sends a message using template and variables supplied using an IO wrapper that can be called
     *
     * @param name : name of the template
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     * @param variables : values to replace the variables in template
     */
    fun sendUsingTemplate(name: String, countryCode: String, phone: String, variables: Vars): Outcome<String> {
        val result = validate(countryCode, phone)
        return if (result.success) {
            // Send the message
            // send(to, subject, message, html)
            templates?.let { t ->
                val tres = t.resolveTemplateWithVars(name, variables.toMap())
                tres?.let { message ->
                    send(message, countryCode, phone)
                }
            } ?: Outcomes.invalid("templates are not setup")
        } else {
            Outcomes.errored(result.msg)
        }
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
            Failure(result.msg)
        }
        // Case 2: Invalid iso or unsupported
        else if (!countries.contains(finalIso)) {
            Failure("$finalIso is not a valid country code")
        }
        // Case 3: Inputs valid so massage
        else {
            val country = countries[finalIso]
            val finalPhone = country?.let { c ->
                if (!phone.startsWith(country.phoneCode)) {
                    "${c.phoneCode}$phone"
                } else {
                    phone
                }
            }

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
