---
layout: start_page_mods_infra
title: module Email
permalink: /kotlin-mod-email
---

# Email

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | An Email service to send emails with support for templates using SendGrid as the default implementation | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.sms  |
| **source core** | slatekit.core.sms.EmailService.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Email.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Email.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.core.email.EmailMessage
import slatekit.core.email.EmailServiceSendGrid
import slatekit.common.templates.Template
import slatekit.common.templates.Templates


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.conf.Config
import slatekit.common.Uris
import slatekit.common.Vars
import slatekit.common.Result
import slatekit.common.ApiLogin


```

## Setup
```kotlin


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
    

```

## Usage
```kotlin


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
    

```

