---
title: "Files"
date: 2019-11-17T23:55:42-05:00
section_header: Files
---

# Overview
The Files component is an abstraction of file storage with a default implementation using **AWS S3**. This also provides a much simplified API while making the underlying implementation swappable.

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/files#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/files#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/files#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/files#sample">Import</a></strong></td>
        <td>Packages to import</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/files#goals">Setup</a></strong></td>
        <td>Set up of credentials, and configuration</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/files#concepts">Usage</a></strong></td>
        <td>Usage and examples</td>
    </tr>
</table>
{{% section-end mod="arch/files" %}}

# Status
This component is currently **stable**. Following limitations, current work, planned features apply.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td>**Binary**</td>
        <td>In-Progress</td>
        <td>Ability to store/retrieve binary files. ( you can still use the underlying AWS provider to so )</td>
    </tr>
    <tr>
        <td>**Names**</td>
        <td>Upcoming</td>
        <td>Ability to enforce a naming convention on the file names</td>
    </tr>
    <tr>
        <td>**Async**</td>
        <td>Upcoming</td>
        <td>Async support via Kotlin suspend/coroutines and Java AWS 2.0 SDK</td>
    </tr>
    <tr>
        <td>**URI**</td>
        <td>Upcoming</td>
        <td>Ability to use Slate Kit Uri for explicit references to file paths</td>
    </tr>
</table>
{{% section-end mod="arch/files" %}}

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
    url="arch/files"
    uses="slatekit.results, slatekit.core, slatekit.cloud"
    exampleUrl=""
    exampleFileName="Example_Files.kt"
%}}
{{% section-end mod="arch/files" %}}

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
{{% section-end mod="arch/files" %}}

# Imports
{{< highlight kotlin >}}
        
    import slatekit.cloud.aws.AwsCloudFiles
    
{{< /highlight >}}

{{% section-end mod="arch/files" %}}

# Setup
{{< highlight kotlin >}}
        
    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val files1 = AwsCloudFiles("app1-files-1", "slatekit", false)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files2 = AwsCloudFiles("app1-files-1", "slatekit",false, "user://myapp/conf/files.conf", "s3")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files3 = AwsCloudFiles("app1-files-1", "slatekit",false, "user://myapp/conf/files.conf", "s3-1")

{{< /highlight >}}

{{% section-end mod="arch/files" %}}

# Usage
{{< highlight kotlin >}}
        
    // Use case 1: Connect using parameters
    files1.init()

    // Use case 2: create using just name and content
    files1.create("file-1", "content 1")

    // Use case 3: update using just name and content
    files1.update("file-1", "content 2")

    // Use case 4: create using folder and file name
    files1.create("folder-1", "file-1", "content 1")

    // Use case 5: update using folder and file name
    files1.update("folder-1", "file-1", "content 2")

    // Use case 6: get file as a text using just name
    files1.getAsText("file-1")

    // Use case 7: get file using folder and file name
    files1.getAsText("folder-1", "file-1")

    // Use case 8: download file to local folder
    files1.download("file-1", "~/dev/temp/")

    // Use case 9: download using folder and file name to local folder
    files1.download("folder-1", "file-1", "~/dev/temp")

    // Use case 10: delete file by just the name
    files1.delete("file-1")

    // Use case 11: delete using folder and name
    files1.delete("folder-1", "file-1")
      

{{< /highlight >}}
{{% section-end mod="arch/files" %}}

