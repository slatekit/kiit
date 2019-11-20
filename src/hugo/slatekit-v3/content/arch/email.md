---
title: "Email"
date: 2019-11-17T23:55:42-05:00
section_header: Email
---

# Overview
The Email component is an abstraction of an Email Service with support for simple templates and a default implementation for sending emails using **SendGrid**. 

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
This component is currently stable with a default implementation for **AWS S3**. However it is currently using the AWS 1.0 sdk that is synchonous. A future version will involve using AWS 2.0 sdk that is **Async** and incorporate Coroutines.
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
         
    import slatekit.notifications.email.EmailMessage
    import slatekit.notifications.email.EmailServiceSendGrid
    import slatekit.common.templates.Template
    import slatekit.common.templates.Templates
     
{{< /highlight >}}

{{% section-end mod="arch/sms" %}}

# Setup
{{< highlight kotlin >}}
        
    // Setup 1: Getting key from config
    // Load the config file from slatekit directory in user_home directory
    // e.g. {user_home}/slatekit/conf/sms.conf
    // NOTE: It is safer/more secure to store config files there.
    val conf =  Config.of("user://slatekit/conf/email.conf")

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

{{% section-end mod="arch/sms" %}}

# Usage
{{< highlight kotlin >}}
        
    // Use case 1: Send a confirmation code to the U.S. to verify a users phone number.
    val result = email2.send("kishore@abc.com", "Welcome to MyApp.com", "showWelcome!", false)

    // Use case 2: Send using a constructed message object
    email2.sendSync(EmailMessage("kishore@abc.com", "Welcome to MyApp.com", "showWelcome!", false))

    // Use case 3: Send message using one of the setup templates
    email2.sendUsingTemplate("email_welcome", "kishore@abc.com", "Welcome to MyApp.com", true,
            Vars(listOf(
                    Pair("greeting", "hello"),
                    Pair("user.api", "kishore"),
                    Pair("app.code", "ABC123")
            )))
      

{{< /highlight >}}
{{% section-end mod="arch/sms" %}}

