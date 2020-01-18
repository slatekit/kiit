/**
 * <slate_header>
 * author: Kishore Reddy
 * url: www.github.com/code-helix/slatekit
 * copyright: 2015 Kishore Reddy
 * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
 * desc: A tool-kit, utility library and server-backend
 * usage: Please refer to license on github for more info.
 * </slate_header>
 */

package slatekit.examples

//<doc:import_required>
import slatekit.notifications.sms.SmsMessage
import slatekit.notifications.sms.SmsService
import slatekit.notifications.sms.TwilioSms
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.templates.Template
import slatekit.common.templates.TemplatePart
import slatekit.common.templates.Templates
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin
import slatekit.common.types.CountryCode
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.Vars
import slatekit.common.ext.env
import slatekit.results.Try
import slatekit.results.Success
import kotlinx.coroutines.runBlocking
import slatekit.common.ext.orElse
import slatekit.common.io.Uris

//</doc:import_examples>


class Example_Sms : Command("sms") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        // Setup 1: Getting Api key/login info from config
        // Load the config file from slatekit directory in user_home directory
        // e.g. {user_home}/myapp/conf/sms.conf
        // NOTE: It is safer/more secure to store config files there.
        val conf = Config.of("~/.slatekit/conf/sms.conf")
        val apiKey1 = conf.apiLogin("sms")

        // Setup 2: Get the api key either through conf or explicitly
        val apiKey2 = ApiLogin("17181234567", "ABC1234567", "password", "dev", "twilio-sms")

        // Setup 3a: Setup the sms service ( basic ) with api key
        // Note: The sms service will default to only USA ( you can customize this later )
        val apiKey = apiKey1 ?: apiKey2
        val sms1 = TwilioSms(apiKey.key, apiKey.pass, apiKey.account)

        // Setup 3b: Setup the sms service with support for templates
        // Template 1: With explicit text and embedded variables
        // Template 2: Loaded from text file
        val templates = Templates.build(
                templates = listOf(
                        Template("sms_welcome", """
                            Hi @{user.name}, Welcome to @{app.name}!
                            We have sent a welcome email and account confirmation to @{user.email}.
                         """.trimIndent()),
                        Template("sms_confirm", Uris.readText("~/slatekit/templates/sms_confirm.txt") ?: "")
                ),
                subs = listOf(
                        "app.name" to { s: TemplatePart -> "My App" },
                        "app.from" to { s: TemplatePart -> "My App Team" }
                )
        )
        val sms2 = TwilioSms(apiKey.key, apiKey.pass, apiKey.account, templates)

        // Setup 3b: Setup the templates with support for different country codes
        val countries = listOf(CountryCode("US"), CountryCode("FR"))
        val sms3 = TwilioSms(apiKey.key, apiKey.pass, apiKey.account, templates, countries)
        val sms: SmsService = sms3
        //</doc:setup>

        //<doc:examples>
        runBlocking {
            // Sample phone number ( loaded from environment variable for test/example purposes )
            val phone = "SLATEKIT_EXAMPLE_PHONE".env().orElse("1234567890")

            // Use case 1: Send an invitation message to phone "234567890 in the United States.
            sms.send("Invitation to MyApp.com 1", "us", phone)

            // Use case 2: Send using a constructed message object
            sms.sendSync(SmsMessage("Invitation to MyApp.com 2", "us", phone))

            // Use case 3: Send message using one of the templates
            sms.sendTemplate("sms_welcome", "us", phone,
                    Vars(listOf(
                            "user.name" to "user1",
                            "user.email" to "user1@gmail.com"
                    )))
        }
        //</doc:examples>

        return Success("")
    }
}
