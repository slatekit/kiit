/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.examples


//<doc:import_required>
import kotlinx.coroutines.runBlocking
import slatekit.notifications.email.EmailMessage
import slatekit.notifications.email.SendGrid
import slatekit.common.templates.Template
import slatekit.common.templates.Templates
//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.cmds.Command
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin
import slatekit.cmds.CommandRequest
import slatekit.common.io.Uris
import slatekit.common.Vars
import slatekit.results.builders.Tries

//</doc:import_examples>


class Example_Email  : Command("auth") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:setup>
    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf =  Config.of("~/.slatekit/conf/email.conf")

    // Setup 2: Get the api key either through conf or explicitly
    val apiKey1 = conf.apiLogin("email")
    val apiKey2 = ApiLogin("17181234567", "ABC1234567", "password", "dev", "sendgrid-email")
    val apiKey  = apiKey1 ?: apiKey2

    // Setup 3a: Setup the email service ( basic ) with api key
    val emailService1 =  SendGrid(apiKey.key, apiKey.pass, apiKey.account)

    // Setup 3b: Setup the sms service with support for templates
    val templates = Templates.build(
      templates = listOf(
         Template("email_welcome", Uris.readText("~/slatekit/templates/email_welcome.txt") ?: "" ),
         Template("email_pass", Uris.readText("~/slatekit/templates/email_password.txt") ?: "")
      ),
      subs = listOf(
        "company.api" to { s -> "MyCompany"        },
        "app.api"     to { s -> "SlateKit.Sample"  }
      )
    )
    val emailService2 =  SendGrid(apiKey.key, apiKey.pass, apiKey.account, templates)
    //</doc:setup>

    //<doc:examples>
    runBlocking {
      // Use case 1: Send a confirmation code to the U.S. to verify a users phone number.
      val result1 = emailService1.send("user1@company1.com", "Welcome to MyApp.com", "showWelcome!", false)

      // Use case 2: Send using a constructed message object
      emailService1.sendSync(EmailMessage("user1@company1.com", "Welcome to MyApp.com", "showWelcome!", false))

      // Use case 3: Send message using one of the setup templates
      val result2 = emailService2.sendUsingTemplate("email_welcome", "user1@company1.com", "Welcome to MyApp.com", true,
              Vars(listOf(
                      "greeting" to "hello",
                      "user.api" to "user1",
                      "app.code" to "ABC123"
              )))
    }
    //</doc:examples>

    return Tries.success("Ok")
  }
}
