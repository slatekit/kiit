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

package slatekit.core.sms

import slatekit.common.*
import slatekit.common.results.ResultFuncs.success
import slatekit.common.info.ApiLogin
import slatekit.common.templates.Templates
import slatekit.common.types.CountryCode

/**
 * simple service to send sms messages using Twilio with support for templates and
 * countries
 *
 * @param key : The twilio sid / account
 * @param password : The twilio password
 * @param phone : The twilio phone number
 * @param templates: The templates supported ( See templates in utils for more info )
 * @param ctns : The countries supported
 * @note:
 *
curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/BCa1234567890d49dcffd51736e0e2e123/Messages.json' \
--data-urlencode 'To=3475143333'  \
--data-urlencode 'From=+17181234567'  \
--data-urlencode 'Body=test from slate sms service' \
-u ACb1234567890d49dcffd51736e0e2e123:xyz5a123456d78d415eaab7ab92e3bab
 */
class SmsServiceTwilio(
    key: String,
    password: String,
    phone: String,
    templates: Templates? = null,
    countries: List<CountryCode>? = null
)
    : SmsService(templates, countries) {

    private val _settings = SmsSettings(key, password, phone)
    private val _baseUrl = "https://api.twilio.com/2010-04-01/Accounts/$key/Messages.json"

    /**
     * Initialize with api credentials
     *
     * @param apiKey
     */
    constructor(apiKey: ApiLogin, templates: Templates? = null, countries:List<CountryCode>? = null) :
            this(apiKey.key, apiKey.pass, apiKey.account, templates, countries)

    /**
     * sends the sms message to the phone
     *
     * @param msg : message to send
     * @return
     */
    override fun send(msg: SmsMessage): ResultMsg<Boolean> {

        val phoneResult = massagePhone(msg.countryCode, msg.phone)
        return when(phoneResult) {
            is Success -> {
                val phone = phoneResult.data
                val result = HttpRPC().sendSync(
                        method = HttpRPC.Method.Post,
                        url = _baseUrl,
                        headers = null,
                        creds = HttpRPC.Auth.Basic(_settings.key, _settings.password),
                        body = HttpRPC.Body.FormData(listOf(
                                Pair("To", phone),
                                Pair("From", _settings.account),
                                Pair("Body", msg.msg))
                        )
                )
                result.fold( { Success(true) }, { Failure(it.message ?: "") })
            }
            is Failure -> Failure(phoneResult.msg)
        }
    }


    /**
     * Format phone to ensure "+" and "iso" is present. e.g. "+{iso}{phone}"
     * @param iso
     * @param phone
     * @return
     */
    override fun massagePhone(iso: String, phone: String): ResultMsg<String> {
        // Remove the "+" and allow base function to ensure the country code is present
        val result = super.massagePhone(iso, phone.replace("+", ""))
        return when (result) {
            is Success -> success("+" + result.data)
            is Failure -> result
        }
    }
}
