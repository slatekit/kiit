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
import slatekit.common.*

//</doc:import_required>

//<doc:import_examples>
import slatekit.functions.cmds.Command
import slatekit.common.templates.Template
import slatekit.common.templates.TemplatePart
import slatekit.common.templates.Templates
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin
import slatekit.common.types.CountryCode
import slatekit.functions.cmds.CommandRequest
import slatekit.notifications.sms.SmsMessage
import slatekit.notifications.sms.SmsServiceTwilio
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>



class Example_Sms : Command("sms") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:setup>
    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf =  Config("user://slatekit/conf/sms.conf")

    // Setup 2: Get the api key either through conf or explicitly
    val apiKey1 = conf.apiLogin("sms")
    val apiKey2 = ApiLogin("17181234567", "ABC1234567", "password", "dev", "twilio-sms")
    val apiKey  = apiKey1 ?: apiKey2

    // Setup 3a: Setup the sms service ( basic ) with api key
    // Note: The sms service will default to only USA ( you can customize this later )
    val sms1 =  SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account)

    // Setup 3b: Setup the sms service with support for templates
    val templates = Templates.build(
      templates = listOf(
         Template("sms_welcome", Uris.readText("user://slatekit/templates/sms_welcome.txt") ?: ""),
         Template("email_welcome", Uris.readText("user://slatekit/templates/email_welcome.txt") ?: ""),
         Template("email_pass", Uris.readText("user://slatekit/templates/email_password.txt") ?: "")
      ),
      subs = listOf(
        Pair("company.api" , { s: TemplatePart -> "MyCompany"        }),
        Pair("app.api"     , { s: TemplatePart -> "SlateKit.Sample"  })
      )
    )
    val sms2 =  SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account, templates)

    // Setup 3b: Setup the templates with support for different country codes
    val countries = listOf(CountryCode("US"), CountryCode("FR"))
    val sms3 =  SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account, templates, countries)
    //</doc:setup>

    //<doc:examples>
    // Use case 1: Send an invitation message to phone "234567890 in the United States.
    sms3.send("Invitation to MyApp.com", "us", "234567890")

    // Use case 2: Send using a constructed message object
    sms3.sendSync(SmsMessage("Invitation to MyApp.com", "us", "234567890"))

    // Use case 3: Send message using one of the setup templates
    sms3.sendUsingTemplate("sms_welcome", "us", "234567890",
       Vars(listOf(
        Pair("greeting" , "hello"),
        Pair("user.api", "kishore"),
        Pair("app.code" , "ABC123")
      )))
    //</doc:examples>

    return Success("")
  }

  /*
  val c = Country.allCountries()
    val text =  StringBuilder()
    val maxPhoneLen = c.map( c => c.phone.trim() ).maxBy( c => c.length).length + 2
    val maxNameLen = c.map( c => c.api.trim() ).maxBy( c => c.length).length + 2
    c.foreach( c => {
      val phone = Strings.pad("'" + c.phone.trim() + "'", maxPhoneLen)
      val api = Strings.pad("'" + c.api.trim() + "'", maxNameLen)
      text.append(s"Country('${c.iso}', '${c.iso3}', ${phone}, ${api})," + Strings.line())
    })
    println(text)
    ""
  * */
}
