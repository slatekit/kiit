---
title: "Setup"
date: 2019-03-17T13:02:30-04:00
draft: true
---

{{% heading name="Overview" %}}
Slate Kit can be setup fairly easily. Most of the dependencies are on Java. You can download binaries from any of the sources below. You can find the setup instructions here for all the dependencies, and for using Kotlin in Intellij or on the command line via gradle.
{{% break %}}

{{% heading name="Index" %}}
Table of contents for this page
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="core/cli#status">Required</a></strong></td>
        <td>Required dependencies</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="core/cli#install">Suggested</a></strong></td>
        <td>Suggested dependencies</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Optional</a></strong></td>
        <td>Optional dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Gradle</a></strong></td>
        <td>Gradle configuration</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Artifacts</a></strong></td>
        <td>Projects and their corresponding artifacts</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}


{{% heading name="Required" %}}
All the software below is required to run Kotlin and Slate Kit. Kotlin is dependent on <strong>Java</strong>. <strong>Gradle</strong> is used to build and package Kotlin projects
and is the build tool for Slate Kit.
<table class="table table-bordered table-striped">
    <tr class="">
        <td><strong>Tech</strong></td>
        <td><strong>Version</strong></td>
        <td><strong>About</strong></td>
        <td><strong>Link</strong></td>
    </tr>
    <tr class="">
        <td><strong>Java</strong></td>
        <td>1.8</td>
        <td>Java</td>
        <td><a class="url-ch" href="#installation">see below</a></td>
    </tr>
    <tr class="">
        <td><strong>Kotlin</strong></td>
        <td>1.1.2</td>
        <td>Kotlin</td>
        <td><a class="url-ch" href="#installation">see below</a></td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}


{{% heading name="Suggested" %}}
The software below is recommended but not required. 
Slate Kit supports MySql with support for PostGres and Sql Server coming later. Having the MySql Java Connector will 
help with the sample apps and is also needed for the ORM ( entities ).
<table class="table table-bordered table-striped">
    <tr class="">
        <td><strong>Tech</strong></td>
        <td><strong>Version</strong></td>
        <td><strong>About</strong></td>
        <td><strong>Link</strong></td>
    </tr>
    <tr>
        <td><strong>IntelliJ</strong></td>
        <td>2017.2</td>
        <td>IDE</td>
        <td><a class="url-ch" href="https://www.jetbrains.com/idea/download/">download</a></td>
    </tr>
    <tr>
        <td><strong>Gradle</strong></td>
        <td>3.5</td>
        <td>Build tool</td>
        <td><a class="url-ch" href="#installation">see below</a></td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

{{% heading name="Optional" %}}
Slate Kit supports building Web APIs using <strong>Spark Java</strong>, Cloud Services ( Files, Queues ) using <strong>AWS</strong> and databases using <strong>MySql</strong>. The following are needed if you plan on using any of these.
<table class="table table-bordered table-striped">
    <tr class="">
        <td><strong>Tech</strong></td>
        <td><strong>Version</strong></td>
        <td><strong>About</strong></td>
        <td><strong>Link</strong></td>
    </tr>
    <tr>
        <td><strong>MySql</strong></td>
        <td>5.7</td>
        <td>Database Server</td>
        <td><a class="url-ch" href="https://dev.mysql.com/downloads/mysql/">download</a></td>
    </tr>
    <tr>
        <td><strong>MySql Connector</strong></td>
        <td>5.7</td>
        <td>For database connectivity and orm</td>
        <td><a class="url-ch" href="https://dev.mysql.com/downloads/connector/j/">download</a></td>
    </tr>
    <tr>
        <td><strong>Spark Java</strong></td>
        <td>2.6</td>
        <td>For Slate API Server</td>
        <td><a class="url-ch" href="http://sparkjava.com/">visit</a></td>
    </tr>
    <tr>
        <td><strong>AWS Sdk</strong></td>
        <td>1.10.55</td>
        <td>File Storage( S3 ), Queues( SQS )</td>
        <td><a class="url-ch" href="https://aws.amazon.com/sdk-for-java/">download</a></td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}


{{% heading name="Gradle" %}}
You can reference all the Slate Kit modules in gradle. The binaries are available from <strong>bintray</strong>. 
First you just have to include the Slate Kit maven url.
Only the results, utilities modules are listed here for sample purposes. 
You can include additional projects
{{% break %}}
{{< highlight kotlin >}}

    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries
        
        // slatekit-results: Result<T,E> to model successes/failures with optional status codes
        compile 'com.slatekit:slatekit-results:0.9.9'
        
        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.9'
        
        // Misc architecture components ( all depend on results/common components above )
        // Use only what you need ( see artifacts next section )

    }

{{< /highlight >}}
{{% section-end mod="core/cli" %}}


{{% heading name="Artifacts" %}}
{{% sk-artifacts %}}
{{% section-end mod="core/cli" %}}
