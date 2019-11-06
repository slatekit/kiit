---
title: "App"
date: 2019-03-17T14:30:51-04:00
section_header: App
---

# Overview
The Slate App is base application and template to build console, batch, cli and server applications.
It has pre-built support for common features such as <strong>command line args</strong>, <strong>environment selection</strong>, 
<strong>configs per environment</strong>, <strong>logging</strong>, <strong>life-cycle events</strong>, <strong>diagnostics</strong> and much more.
This is accomplished by integrating some of the components and utilities available in the 
{{% sk-link href="utils.html" text="Slate Kit Common" %}} project.
{{% break %}}

# Index
Table of contents for this page
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="core/cli#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="core/cli#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="core/cli#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="core/cli#goals">Goals</a></strong></td>
        <td>Goals of this component and the problems it attempts to solve</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="core/cli#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="core/cli#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="core/cli#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="core/cli#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
    <tr>
        <td><strong>10</strong></td>
        <td><strong><a class="url-ch" href="core/cli#details">How to</a></strong></td>
        <td>How to implement certain tasks, recipes, etc.</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

# Status
This component is currently stable and there is a project generator for it ( see below ). <br/>
There are currently no planned changes or enhancements.
{{% section-end mod="core/cli" %}}

# Install
Use the following settings in gradle for installing this component.
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-app:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="App"
    package="slatekit.app"
    jar="slatekit.app.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-app"
    gitAlias="slatekit/src/lib/kotlin/slatekit-app"
    url="core/app"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_App.kt"
%}}
{{% section-end mod="core/cli" %}}

# Requires
This component uses the following other <strong>Slate Kit</strong> and/or third-party components.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><a class="url-ch" href="core/results">Slate Kit - Results</a></td>
        <td>To model successes and failures with optional status codes</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/utils.html">Slate Kit - Common</a></td>
        <td>Common utilities for both android + server</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

# Sample
You can generate a sample app using the slatekit executable. 
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}
{{< highlight bash >}}
    
    :> slatekit new app -name="MyApp1" -package="company1.myapp1"

{{< /highlight >}}
{{% section-end mod="core/cli" %}}

# Goals
We often have to create a new application which requires typically much boiler-plate code.
These include environments, config files, argument parsing, logging setup, and application life-cycle events.
This component quickly gets a new application set up with all these features ready.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Template</strong></td>
        <td>Provide template for any application 
        ( Console, Batch, CLI, Server )</td>
    </tr>
    <tr>
        <td><strong>2. Features</strong> </td>
        <td>Provide pre-built support commandline args, 
        config, logging, and more.</td>                     
    </tr>
    <tr>
        <td><strong>3. Standardized</strong></td>
        <td>Provide standardized setup, functionality and diagnostics</td>
        </td>                       
    </tr>
</table>    
{{% section-end mod="core/cli" %}}

# Concepts
coming soon
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Concept</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Life-cycle</strong></td>
        <td>The app component is a base class, with support for life-cycle events ( check, init, accept, execute, shutdown )</td>
    </tr>
    <tr>
        <td><strong>2. Envs</strong></td>
        <td>Environment selection ( local, dev, qa, stg, prod, etc ) is available with integration with respective config files.</td>
    </tr>
    <tr>
        <td><strong>3. Overrides</strong></td>
        <td>Support for overriding <strong>some</strong> settings on the command line with what is set up in the configuration file(s)</td>
    </tr>
    <tr>
        <td><strong>4. Context</strong></td>
        <td>Container for services and items such as selected environment ( local, dev, etc ), config, logger, encryptor.</td>
    </tr>
    <tr>
        <td><strong>5. Help</strong></td>
        <td>Support for app information, version, and command line args, is made available through config files and/or code</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

# Features
These are all the features supported by this component
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>Example</strong></td>
    </tr>
    <tr> 
        <td><strong>1. Args</strong></td>
        <td><a class="url-ch" href="kotlin-mod-args.html">Arguments</a> from command line ( which can override some config settings )
        </td>
        <td>-env=dev -log.level=info -region=ny</td>
    </tr>
    <tr> 
        <td><strong>2. Envs</strong></td>
        <td><a class="url-ch" href="kotlin-mod-env.html">Environment</a> selection and validation support</td>
        <td>local, dev, qa, stg, pro</td>
    </tr>
    <tr> 
        <td><strong>3. Logs</strong></td>
        <td><a class="url-ch" href="kotlin-mod-logger.html">Logging</a> and error handling ( any log provider can be easily added )</td>
        <td>console / file logger, custom logger</td>
    </tr>
    <tr> 
        <td><strong>4. Config</strong></td>
        <td><a class="url-ch" href="kotlin-mod-config.html">Configuration</a> files for each environment ( with support for config inheritance )</td>
        <td>env.conf, env.local.conf, env.qa.conf</td>
    </tr>
    <tr> 
        <td><strong>5. Settings</strong></td>
        <td>Settings can be in config, encrypted, or a file in user folder ( for added security )</td>
        <td>${user}/mycompany/myapp/database.conf</td>
    </tr>
    <tr> 
        <td><strong>6. Encryption</strong></td>
        <td><a class="url-ch" href="kotlin-mod-encrypt.html">Encryption</a>  and decryption support</td>
        <td>Using AES</td>
    </tr>
    <tr> 
        <td><strong>7. Life-cycle</strong></td>
        <td>Application life-cycle events</td>
        <td>check(), init(), accept(), execute(), shutdown()</td>
    </tr>
    <tr> 
        <td><strong>8. Context</strong></td>
        <td><a class="url-ch" href="kotlin-mod-ctx.html">Application Context</a> to contain and provide access to many services</td>
        <td>encryptor, logger, settings, environment, etc</td>
    </tr>
    <tr> 
        <td><strong>9. Help</strong></td>
        <td><a class="url-ch" href="kotlin-mod-info.html">App Info</a>, version, and command line args can be displayed</td>
        <td>on command line type {app} ? | help | about </td>
    </tr>
    <tr> 
        <td><strong>10. Output</strong></td>
        <td>Full display of app info and diagnostics can be shown at beginning or end of app</td>
        <td>info, version, start time/end time, etc</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

# Setup
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% section-end mod="core/cli" %}}


# Details
Details on using the features here.
coming soon

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong>Feature 1</strong></td>
        <td>Brief description of feature 1</td>
        <td><a href="core/cli#feature1" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Feature 2</strong></td>
        <td>Brief description of feature 2</td>
        <td><a href="core/cli#feature2" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>Feature 3</strong></td>
        <td>Brief description of feature 3</td>
        <td><a href="core/cli#feature3" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
<br/>

## Feature 1 {#feature1}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="core/cli" %}}

## Feature 2 {#feature2}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="core/cli" %}}

## Feature 3 {#feature3}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="core/cli" %}}


# How to's
Coming soon.
{{% section-end mod="core/cli" %}}

