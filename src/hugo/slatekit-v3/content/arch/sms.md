---
title: "Sms"
date: 2019-11-17T23:55:42-05:00
section_header: Sms
---

# Overview
The SMS component is an abstraction of Text messaging/sending with a default implementation using **Twilio**. 

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#sample">Import</a></strong></td>
        <td>Packages to import</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#goals">Setup</a></strong></td>
        <td>Set up of credentials, and configuration</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/sms#concepts">Usage</a></strong></td>
        <td>Usage and examples</td>
    </tr>
</table>
{{% section-end mod="arch/sms" %}}

# Status
This component is currently stable with future versions planned that include enhanced support for templates.
{{% section-end mod="arch/sms" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-cloud:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="Files"
    package="slatekit.cloud"
    jar="slatekit.cloud.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-cloud"
    gitAlias="slatekit/src/lib/kotlin/slatekit-cloud"
    url="arch/sms"
    uses="slatekit.results, slatekit.core, slatekit.cloud"
    exampleUrl=""
    exampleFileName="Example_Files.kt"
%}}
{{% section-end mod="arch/sms" %}}

# Requires
This component uses the following other <strong>Slate Kit</strong> and/or third-party components.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td>{{% sk-link-arch page="results" name="Results" %}}</td>
        <td>To model successes and failures with optional status codes</td>
    </tr>
    <tr>
        <td>{{% sk-link-util page="overview" name="Utils" %}}</td>
        <td>Common utilities for both android + server</td>
    </tr>
</table>
{{% section-end mod="arch/sms" %}}

# Imports
{{< highlight kotlin >}}
         
    import slatekit.notifications.sms.SmsMessage
    import slatekit.notifications.sms.SmsServiceTwilio
     
{{< /highlight >}}

{{% section-end mod="arch/sms" %}}

# Setup
{{< highlight kotlin >}}
        
    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf =  Config.of("user://slatekit/conf/sms.conf")

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

{{% section-end mod="arch/sms" %}}

# Usage
{{< highlight kotlin >}}
        
    // Use case 1: Send an invitation message to phone "234567890 in the United States.
    sms3.send("Invitation to MyApp.com", "us", "234567890")

    // Use case 2: Send using a constructed message object
    sms3.sendSync(SmsMessage("Invitation to MyApp.com", "us", "234567890"))

    // Use case 3: Send message using one of the setup templates
    sms3.sendUsingTemplate("sms_welcome", "us", "234567890",
            Vars(listOf(
                    Pair("greeting", "hello"),
                    Pair("user.api", "kishore"),
                    Pair("app.code", "ABC123")
            )))
      

{{< /highlight >}}
{{% section-end mod="arch/sms" %}}




