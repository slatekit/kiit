
# Email

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>An Email service to send emails with support for templates using SendGrid as the default implementation</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.core.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.core.sms.EmailService</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-core</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/sms/EmailService" class="url-ch">src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/sms/EmailService</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Email.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Email.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results slatekit-common</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-core:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.*
import slatekit.core.email.EmailMessage
import slatekit.core.email.EmailServiceSendGrid
import slatekit.common.templates.Template
import slatekit.common.templates.Templates


// optional 
import slatekit.results.Try
import slatekit.results.Success
import slatekit.core.cmds.Cmd
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



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
    


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


    // Use case 1: Send a confirmation code to the U.S. to verify a users phone number.
    val result = email2.send("kishore@abc.com", "Welcome to MyApp.com", "showWelcome!", false)

    // Use case 2: Send using a constructed message object
    email2.send(EmailMessage("kishore@abc.com", "Welcome to MyApp.com", "showWelcome!", false))

    // Use case 3: Send message using one of the setup templates
    email2.sendUsingTemplate("email_welcome", "kishore@abc.com", "Welcome to MyApp.com", true,
       Vars(listOf(
        Pair("greeting" , "hello"),
        Pair("user.api", "kishore"),
        Pair("app.code" , "ABC123")
      )))
    

{{< /highlight >}}
{{% break %}}

