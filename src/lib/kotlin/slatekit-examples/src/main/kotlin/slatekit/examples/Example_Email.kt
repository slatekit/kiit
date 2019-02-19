/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.examples


//<doc:import_required>
import slatekit.common.*
import slatekit.core.email.EmailMessage
import slatekit.core.email.EmailServiceSendGrid
import slatekit.common.templates.Template
import slatekit.common.templates.Templates
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin

//</doc:import_examples>


class Example_Email  : Cmd("auth") {

  override fun executeInternal(args: Array<String>?) : ResultEx<Any>
  {
    //<doc:setup>
    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf =  Config("user://slatekit/conf/email.conf")

    // Setup 2: Get the api key either through conf or explicitly
    val apiKey1 = conf.apiLogin("email")
    val apiKey2 = ApiLogin("17181234567", "ABC1234567", "password", "dev", "sendgrid-email")
    val apiKey  = apiKey1 ?: apiKey2

    // Setup 3a: Setup the email service ( basic ) with api key
    val email1 =  EmailServiceSendGrid(apiKey.key, apiKey.pass, apiKey.account)

    // Setup 3b: Setup the sms service with support for templates
    val templates = Templates.build(
      templates = listOf(
         Template("email_welcome", Uris.readText("user://slatekit/templates/email_welcome.txt") ?: "" ),
         Template("email_pass", Uris.readText("user://slatekit/templates/email_password.txt") ?: "")
      ),
      subs = listOf(
        Pair("company.api" , { s -> "MyCompany"        }),
        Pair("app.api"     , { s -> "SlateKit.Sample"  })
      )
    )
    val email2 =  EmailServiceSendGrid(apiKey.key, apiKey.pass, apiKey.account, templates)
    //</doc:setup>

    //<doc:examples>
    // Use case 1: Send a confirmation code to the U.S. to verify a users phone number.
    val result = email2.send("kishore@abc.com", "Welcome to MyApp.com", "welcome!", false)

    // Use case 2: Send using a constructed message object
    email2.send(EmailMessage("kishore@abc.com", "Welcome to MyApp.com", "welcome!", false))

    // Use case 3: Send message using one of the setup templates
    email2.sendUsingTemplate("email_welcome", "kishore@abc.com", "Welcome to MyApp.com", true,
       Vars(listOf(
        Pair("greeting" , "hello"),
        Pair("user.api", "kishore"),
        Pair("app.code" , "ABC123")
      )))
    //</doc:examples>

    return result.toResultEx()
  }
}
