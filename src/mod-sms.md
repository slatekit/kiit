---
layout: start_page_mods_infra
title: module Sms
permalink: /kotlin-mod-sms
---

# Sms

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | An Sms ( Text message ) service to send text messages to mobile phones for confirmation codes and invites. | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.sms  |
| **source core** | slatekit.core.sms.SmsService.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Sms.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Sms.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.common.*
import slatekit.common.auth.AuthConsole
import slatekit.common.auth.User



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.templates.Template
import slatekit.common.templates.TemplatePart
import slatekit.common.templates.Templates
import slatekit.common.conf.Config
import slatekit.core.sms.SmsMessage
import slatekit.core.sms.SmsServiceTwilio



```

## Setup
```kotlin


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
    

```

## Usage
```kotlin


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
    

```

