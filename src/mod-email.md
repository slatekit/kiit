---
layout: start_page_mods_infra
title: module Email
permalink: /mod-email
---

# Email

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | An Email service to send emails with support for templates using SendGrid as the default implementation | 
| **date**| 2017-04-12T22:59:15.708 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.sms  |
| **source core** | slate.core.sms.EmailService.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/sms](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/sms)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Email.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Email.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.templates.{TemplatePart, Template, Templates}
import slate.common.{Result, Vars, Uris, ApiCredentials}
import slate.core.common.Conf
import slate.core.email.{EmailMessage, EmailServiceSendGrid}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf = new Conf(Some("user://slatekit/conf/email.conf"))

    // Setup 2: Get the api key either through conf or explicitly
    val apiKey1 = conf.apiKey("email")
    val apiKey2 = ApiCredentials("17181234567", "ABC1234567", "password", "dev", "sendgrid-email")
    val apiKey  = apiKey1.getOrElse(apiKey2)

    // Setup 3a: Setup the email service ( basic ) with api key
    val email1 = new EmailServiceSendGrid(apiKey.key, apiKey.pass, apiKey.account)

    // Setup 3b: Setup the sms service with support for templates
    val templates = Templates(
      templates = Seq(
        new Template("email_welcome", Uris.readText("user://slatekit/templates/email_welcome.txt").get),
        new Template("email_pass", Uris.readText("user://slatekit/templates/email_password.txt").get)
      ),
      subs = Some(List(
        ("company.name" , (s:TemplatePart) => "MyCompany"        ),
        ("app.name"     , (s:TemplatePart) => "SlateKit.Sample"  )
      ))
    )
    val email2 = new EmailServiceSendGrid(apiKey.key, apiKey.pass, apiKey.account, Some(templates))
    

```

## Usage
```scala


    // Use case 1: Send a confirmation code to the U.S. to verify a users phone number.
    val result = email2.send("kishore@abc.com", "Welcome to MyApp.com", "welcome!", false)

    // Use case 2: Send using a constructed message object
    email2.send(EmailMessage("kishore@abc.com", "Welcome to MyApp.com", "welcome!", false))

    // Use case 3: Send message using one of the setup templates
    email2.sendUsingTemplate("email_welcome", "kishore@abc.com", "Welcome to MyApp.com", true,
      new Vars(Some(List(
        "greeting"  -> "hello",
        "user.name" -> "kishore",
        "app.code"  -> "ABC123"
      ))))
    

```

