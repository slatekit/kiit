---
title: "App"
date: 2019-03-17T14:30:51-04:00
section_header: App
---
<br/>
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
        <td><strong><a class="url-ch" href="arch/app#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/app#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/app#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/app#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/app#goals">Goals</a></strong></td>
        <td>Goals of this component and the problems it attempts to solve</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/app#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/app#details">Features</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/app#details">How to</a></strong></td>
        <td>How to implement certain tasks, recipes, etc.</td>
    </tr>
</table>
{{% section-end mod="arch/app" %}}

# Status
This component is currently stable and there is a project generator for it ( see below ). <br/>
There are currently no planned changes or enhancements.
{{% section-end mod="arch/app" %}}

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
{{% section-end mod="arch/app" %}}

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
{{% section-end mod="arch/app" %}}

# Sample
You can generate a sample app using the slatekit executable. 
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}
{{< highlight bash >}}
    
    :> slatekit new app -name="MyApp1" -package="company1.myapp1"

{{< /highlight >}}
{{% section-end mod="arch/app" %}}

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
{{% section-end mod="arch/app" %}}

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
{{% section-end mod="arch/app" %}}


# Features
List of all features available and how to use them.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>Args</strong></td>
        <td>Command line arguments ( -env=dev )</td>
        <td><a href="arch/app/#Args" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Envs</strong></td>
        <td>Environment selection ( dev | qat | stg | pro )</td>
        <td><a href="arch/app#Envs" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Logs</strong></td>
        <td>Logging and error handling ( console / file logger )</td>
        <td><a href="arch/app#Logs" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Conf</strong></td>
        <td>Config settings ( env.conf, env.local.conf, env.qat.conf )</td>
        <td><a href="arch/app#Conf" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Encrypt</strong></td>
        <td>Encryption support for settings</td>
        <td><a href="arch/app#Encrypt" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Context</strong></td>
        <td>Application context ( for env, logs, configs, etc )</td>
        <td><a href="arch/app#Context" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Cycle</strong></td>
        <td>Life-Cycle events ( init, exec, done )</td>
        <td><a href="arch/app#Cycle" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Help</strong></td>
        <td>Help support for command line args ( help | version )</td>
        <td><a href="arch/app#Help" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>Info</strong></td>
        <td>Startup info and diagnostics</td>
        <td><a href="arch/app#Info" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
<br/>

## Args {#Args}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/app" %}}

## Envs {#Envs}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/app" %}}

## Logs {#Logs}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/app" %}}


# How to's
Coming soon.
{{% section-end mod="arch/app" %}}

