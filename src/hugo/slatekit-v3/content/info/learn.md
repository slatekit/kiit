---
title: "Learn"
date: 2019-03-17T13:02:30-04:00
draft: true
---

{{% heading name="Overview" %}}
To get started with Slate Kit, first take a look at the overview page. 
This will provide info on the goals, use-cases, technology, philosophy
and the components offered.
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
        <td><strong><a class="url-ch" href="core/cli#status">Install</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="core/cli#status">Setup</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="core/cli#install">Available</a></strong></td>
        <td>Overview of all the modules available</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Results</a></strong></td>
        <td>Start with the Results component used throughout the project</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Utilities</a></strong></td>
        <td>Use the common component for 24+ utilities for both Android + Server</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Concepts</a></strong></td>
        <td>Some general concepts before diving into the other modules</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Components</a></strong></td>
        <td>All the components available and their artifacts</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="core/cli#requires">Samples</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}


{{% heading name="Setup" %}}
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


{{% heading name="Results" %}}
The Result component is used throughout Slate Kit to model Successes and Failures in code using optional status codes.
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
    }

{{< /highlight >}}
{{% sk-module 
    name="Results"
    package="slatekit.results"
    jar="slatekit.results.jar"
    git="https://github.com/code-helix/slatekit-result"
    gitAlias="slatekit-result"
    url="core/result"
    uses="none"
    exampleUrl=""
    exampleFileName="Example_Result.kt"
%}}
<ol>
    <li>Has 0 dependencies</li>
    <li>Can be used in Android + Server </li>
    <li>Is used throughout all other projects</li>
    <li>Has typealiases for further simplifying usage</li>
</ol>
{{% break %}}
{{% button url="" text="see result component" %}}
{{% break %}} 
{{% break %}}


{{% heading name="Utilities" %}}
The Utilities supplement the Kotlin standard library by offering a set of powerful general purpose components that can be used for any application. Thse are located in the <a class="url-ch" href="util/utils.html">SlateKit.Common</a>project and used by all other architectural components.
{{% break %}}
{{< highlight kotlin >}}

    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries
        
        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.9'
    }

{{< /highlight >}}
{{% sk-module 
    name="Common"
    package="slatekit.common"
    jar="slatekit.common.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common"
    gitAlias="slatekit-common"
    url="utils/utils.html"
    uses="slatekit-results"
    exampleUrl=""
    exampleFileName=""
%}}
<ol>
    <li>Contains several ( almost 24+ ) utilities</li>
    <li>Many utilities are very general purpose</li>
    <li>Can be used in Android + Server </li>
    <li>Only depends on the Slate Kit Results component</li>
</ol>
{{% break %}}
{{% button url="" text="see overview" %}}
{{% break %}} 
{{% break %}}


{{% heading name="Concepts" %}}
There are some key concepts in Slate Kit that are used throughout the project in various components. They are fairly straigh-forward, but understanding them will make reading the docs even easier.
{{% break %}}
<table class="table table-bordered table-striped">
    <tr class="">
        <td><strong>Number</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Source</strong></td>
        <td><strong>Example</strong></td>
        <td><strong>Purpose</strong></td>
    </tr>
    <tr>
        <td><strong>IntelliJ</strong></td>
        <td>2017.2</td>
        <td>IDE</td>
        <td>IDE</td>
        <td><a class="url-ch" href="https://www.jetbrains.com/idea/download/">download</a></td>
    </tr>
    <tr>
        <td><strong>IntelliJ</strong></td>
        <td>2017.2</td>
        <td>IDE</td>
        <td>IDE</td>
        <td><a class="url-ch" href="https://www.jetbrains.com/idea/download/">download</a></td>
    </tr>
    <tr>
        <td><strong>Gradle</strong></td>
        <td>3.5</td>
        <td>Build tool</td>
        <td>IDE</td>
        <td><a class="url-ch" href="#installation">see below</a></td>
    </tr>
</table>
{{% button url="" text="see concepts" %}}
{{% break %}} 
{{% break %}}


{{% heading name="Projects" %}}
All the components available are located in various projects for the purpose of organization and modularity. Take a look at the projects to get an understanding of the purpose of each project and the dependencies between them. You can use as little of Slate Kit or as much as you need.
<strong>The simplest way to start is with the Results component, followed by the Utilities</strong>. Then you can proceed to checking out the concepts, sample apps and using just the individual libraries that you need.
{{% break %}}
{{% sk-modules-artificats %}}
{{% button url="" text="see overview" %}}
{{% break %}} 
{{% break %}}


{{% heading name="Samples" %}}
There are multiple examples, unit-tests, sample applications, and docs available to fully understand how to use the Slate Kit components.
{{% break %}}
{{% sk-resources %}}
{{% button url="" text="see overview" %}}
{{% break %}} 
{{% break %}}



