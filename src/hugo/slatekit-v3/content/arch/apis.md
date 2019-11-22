---
title: "Apis"
date: 2019-11-17T23:55:41-05:00
section_header: Apis
---

# Overview
The Slate CLI is a Command Line Interface application that provides 
pre-built functionality for you to integrate your own commands in an interactie manner. This CLI offers 2 distinct approaches to integration. The first approach allows you to handle the raw text supplied in the CLI yourself and is the most flexible. The second approach provides a more connected, automatic by exposing, validating, and executing inputs against Slate Kit Universal APIs. You can create a CLI app quickly using the Slate Kit command line executable with command.
{{< highlight bash >}}
    
    slatekit new cli -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% break %}}

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#goals">Goals</a></strong></td>
        <td>Goals of this component and the problems it attempts to solve</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="arch/apis#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>
{{% section-end mod="arch/apis" %}}

# Status
This component is currently stable and there is a project generator for it ( see below ). <br/>
A small future enhancement will add support for question and answer flow.
{{% section-end mod="arch/apis" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-cli:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="App"
    package="slatekit.app"
    jar="slatekit.app.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-app"
    gitAlias="slatekit/src/lib/kotlin/slatekit-cli"
    url="core/app"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_CLI.kt"
%}}
{{% section-end mod="arch/apis" %}}

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
{{% section-end mod="arch/apis" %}}

# Sample
coming soon
{{< highlight kotlin >}}

    fun quick_sample() {
        
    }

{{< /highlight >}}
{{% section-end mod="arch/apis" %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Pre-Built CLI</strong></td>
        <td>Support for raw text, parsing, looping, formats, param types</td>
    </tr>
    <tr>
        <td><strong>2. Flexible Use</strong> </td>
        <td>Handle raw requests or leverage existing integration</td>                     
    </tr>
    <tr>
        <td><strong>3. API Support</strong></td>
        <td>Slate Kit APIs are accessible on Web or CLI</td>
    </tr>
</table>
{{% section-end mod="arch/apis" %}}

# Concepts
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="cli" filepath="cli/CLI.kt" name="CLI" %}}</strong></td>
        <td>The main component managing the interaction</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="cli" filepath="cli/CliRequest.kt" name="CliRequest" %}}</strong> </td>
        <td>Represents the command entered</td>                     
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="cli" filepath="cli/CliResponse.kt" name="CliResponse" %}}</strong></td>
        <td>Represents the output of the operation</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="integration" filepath="integration/apis/CliApi.kt" name="CliApi" %}}</strong></td>
        <td>Extends the CLI by integrating it with Slate Kit APIs</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="cli" filepath="cli/Command.kt" name="Command" %}}</strong></td>
        <td>Reserved commands like **about, version, help, quit, exit**, etc</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="common" filepath="common/args/Args.kt" name="Input Param" %}}</strong></td>
        <td>Parameter starting with **-** representing data for a command</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="common" filepath="common/args/Args.kt" name="Meta Param" %}}</strong></td>
        <td>Parameter starting with **@** representing metadata for a command </td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="cli" filepath="cli/SysParam.kt" name="System Param" %}}</strong></td>
        <td>Parameter starting with **$** representing an instruction for the CLI</td>
    </tr>
    <tr>
        <td><strong>9. Reference</strong></td>
        <td>Reference to command originating from a file</td>
    </tr>
</table>
{{% section-end mod="arch/apis" %}}

# Features
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Input</strong></td>
        <td>Description of feature</td>
        <td><a href="arch/apis/#inputs" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Reserved</strong> </td>
        <td>List of reserved commands</td> 
        <td><a href="arch/apis/#reserved" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Args</strong></td>
        <td>How to convert raw text into parsed parameters</td>
        <td><a href="arch/apis/#args" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Requests</strong></td>
        <td>Working with parsed commands as CLI Requests</td>
        <td><a href="arch/apis/#requests" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Execute</strong></td>
        <td>How to execute a request</td>
        <td><a href="arch/apis/#execute" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Responses</strong></td>
        <td>Working with parsed commands as CLI Requests</td>
        <td><a href="arch/apis/#responses" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. Startup</strong></td>
        <td>Load a command at start up</td>
        <td><a href="arch/apis/#startup" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>8. From file</strong></td>
        <td>Load a command from a file</td>
        <td><a href="arch/apis/#from-file" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>9. API</strong></td>
        <td>How to access APIs on the command line</td>
        <td><a href="arch/apis/#apis" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>10. Scripts</strong></td>
        <td>Run a series of commands in batch mode</td>
        <td><a href="arch/apis/#scripts" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/apis" %}}


## Inputs {#inputs}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Reserved {#reserved}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Args {#args}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Requests {#requests}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Execute {#execute}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Responses {#responses}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Startup {#startup}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## From file {#from-file}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## API {#apis}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Scripts {#scripts}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

{{% section-end mod="arch/apis" %}}

