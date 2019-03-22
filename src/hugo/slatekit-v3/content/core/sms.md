
# Sms

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>An Sms ( Text message ) service to send text messages to mobile phones for confirmation codes and invites.</td>
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
      <td>slatekit.core.sms.SmsService</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-core</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/sms/SmsService" class="url-ch">src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/sms/SmsService</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Sms.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Sms.kt</a></td>
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



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.templates.Template
import slatekit.common.templates.TemplatePart
import slatekit.common.templates.Templates
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin
import slatekit.common.types.CountryCode
import slatekit.core.sms.SmsMessage
import slatekit.core.sms.SmsServiceTwilio
import slatekit.results.Try
import slatekit.results.Success




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



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
    


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


    // Use case 1: Send an invitation message to phone "234567890 in the United States.
    sms3.send("Invitation to MyApp.com", "us", "234567890")

    // Use case 2: Send using a constructed message object
    sms3.send(SmsMessage("Invitation to MyApp.com", "us", "234567890"))

    // Use case 3: Send message using one of the setup templates
    sms3.sendUsingTemplate("sms_welcome", "us", "234567890",
       Vars(listOf(
        Pair("greeting" , "hello"),
        Pair("user.api", "kishore"),
        Pair("app.code" , "ABC123")
      )))
    

{{< /highlight >}}
{{% break %}}

