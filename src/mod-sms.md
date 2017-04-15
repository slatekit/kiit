---
layout: start_page_mods_infra
title: module Sms
permalink: /mod-sms
---

# Sms

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | An Sms ( Text message ) service to send text messages to mobile phones for confirmation codes and invites. | 
| **date**| 2017-04-12T22:59:15.726 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.sms  |
| **source core** | slate.core.sms.SmsService.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/sms](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/sms)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Sms.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Sms.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common._
import slate.common.results.ResultSupportIn
import slate.common.templates.{TemplatePart, Template, Templates}
import slate.core.common.Conf
import slate.core.sms.{SmsMessage, SmsServiceTwilio}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf = new Conf(Some("user://slatekit/conf/sms.conf"))

    // Setup 2: Get the api key either through conf or explicitly
    val apiKey1 = conf.apiKey("sms")
    val apiKey2 = ApiCredentials("17181234567", "ABC1234567", "password", "dev", "twilio-sms")
    val apiKey  = apiKey1.getOrElse(apiKey2)

    // Setup 3a: Setup the sms service ( basic ) with api key
    // Note: The sms service will default to only USA ( you can customize this later )
    val sms1 = new SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account)

    // Setup 3b: Setup the sms service with support for templates
    val templates = Templates(
      templates = Seq(
        new Template("sms_welcome", Uris.readText("user://slatekit/templates/sms_welcome.txt").get),
        new Template("email_welcome", Uris.readText("user://slatekit/templates/email_welcome.txt").get),
        new Template("email_pass", Uris.readText("user://slatekit/templates/email_password.txt").get)
      ),
      subs = Some(List(
        ("company.name" , (s:TemplatePart) => "MyCompany"        ),
        ("app.name"     , (s:TemplatePart) => "SlateKit.Sample"  )
      ))
    )
    val sms2 = new SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account, Some(templates))

    // Setup 3b: Setup the templates with support for different country codes
    val countries = Some(List[CountryCode](CountryCode("US"),CountryCode("FR")))
    val sms3 = new SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account, Some(templates), countries)
    

```

## Usage
```scala


    // Use case 1: Send an invitation message to phone "234567890 in the United States.
    sms3.send(s"Invitation to MyApp.com", "us", "234567890")

    // Use case 2: Send using a constructed message object
    sms3.send(SmsMessage("Invitation to MyApp.com", "us", "234567890"))

    // Use case 3: Send message using one of the setup templates
    sms3.sendUsingTemplate("sms_welcome", "us", "234567890",
      new Vars(Some(List(
        "greeting"  -> "hello",
        "user.name" -> "kishore",
        "app.code"  -> "ABC123"
      ))))
    

```

