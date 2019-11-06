---
title: Generators
date: 2019-03-17T13:02:30-04:00
section_header: Generators
---


# Overview
Slate Kit comes with a command line executable to serve as a project generator.
This is simply a Kotlin executable jar packaged as a Java Application via gradle that can be executed on the command line with bash/batch scripts. 
A quick example is below: 
{{< highlight bash >}}
    
    slatekit new app -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
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
        <td><strong><a class="url-ch" href="start/generators#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="start/generators#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="start/generators#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="start/generators#app">APP</a></strong></td>
        <td>Generate a new console application</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="start/generators#api">API</a></strong></td>
        <td>Generate a new HTTP API</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="start/generators#cli">CLI</a></strong></td>
        <td>Generate a new console CLI (command line interface) application</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="start/generators#job">JOB</a></strong></td>
        <td>Generate a new backgroun job</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="start/generators#lib">LIB</a></strong></td>
        <td>Generate a new reusable library</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="start/generators#orm">ORM</a></strong></td>
        <td>Generate a new Domain Driven Database/Entites/ORM project</td>
    </tr>
    <tr>
        <td><strong>10</strong></td>
        <td><strong><a class="url-ch" href="start/generators#orm">Custom</a></strong></td>
        <td>Create custom templates</td>
    </tr>
    <tr>
        <td><strong>11</strong></td>
        <td><strong><a class="url-ch" href="start/generators#orm">Help</a></strong></td>
        <td>More info and help on the generators</td>
    </tr>
</table>
{{% break %}}

# Status
This component is currently stable and there is a project generator for it ( see below ). <br/>
A small future enhancement will optionally add support for Docker and gradle Docker configuration.
{{% section-end mod="start/generators" %}}

# Install
You can install the slatekit application/command line tool from {{% sk-link href="slatekit" text="slatekit" %}}
{{< highlight groovy >}}
    // For mac,nix
    slatekit
    
    // For windows
    slatekit.bat 

    slatekit -version
    slatekit -help

{{< /highlight >}}
{{% section-end mod="start/generators" %}}

# APP
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new app -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}


# API
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new api -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}


# CLI
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new cli -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}


# JOB
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new job -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}

# LIB
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new job -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}

# ORM
<p>
{{% sk-component-app %}}
You can create a app quickly using the Slate Kit command line executable with the following inputs.
Also refer to the {{% sk-link href="Example_App.html" text="Example_App.kt" %}}.
</p>
{{< highlight bash >}}
    
    slatekit new orm -name="Sample1" -package="mycompany.apps"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}

# Custom
<p>
The command line generator can be extended with custom templates.
You have to create various configuration files and instructions similar to the existing ones.
Refer to a sample configuration for the <strong>App</strong> for more info
</p>
{{< highlight bash >}}
    
    slatekit new my_service_template -name="Service1" -package="mycompany.services"
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}

# Help
<p>
You can also get help on the command for each project type. Run the following commands
</p>
{{< highlight bash >}}
    
    slatekit new app -help
    slatekit new api -help
    slatekit new cli -help
    slatekit new job -help
    slatekit new lib -help
    slatekit new orm -help
    
{{< /highlight >}}
{{% section-end mod="start/generators" %}}




