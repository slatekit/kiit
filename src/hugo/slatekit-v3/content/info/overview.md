---
title: "Overview"
date: 2019-03-17T13:02:30-04:00
draft: true
---

{{% heading name="Overview" %}}
Describe this {COMPONENT_NAME} concisely in 2-3 sentences.
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
</table>
{{% section-end mod="core/cli" %}}


{{% heading name="Heading 1" %}}
coming soon
{{% sk-modules %}}
{{% section-end mod="core/cli" %}}


{{% heading name="Heading 2" %}}
coming soon
{{< highlight kotlin >}}

    fun quick_sample() {
        
    }

{{< /highlight >}}
{{% section-end mod="core/cli" %}}


{{% heading name="Heading 3" %}}
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
        <td><strong>Results</strong></td>
        <td>Brief description of Sub-Heading 1</td>
        <td><a href="info/overview#feature1" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Common</strong></td>
        <td>Brief description of Sub-Heading 2</td>
        <td><a href="info/overview#feature2" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>App</strong></td>
        <td>Brief description of Sub-Heading 3</td>
        <td><a href="info/overview#feature3" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
<br/>

## App {#app}
An application template with built-in support for many features
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
{{% break %}}
{{% feature-end mod="core/cli" %}}

## Sub-heading 2 {#sub-heading2}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="core/cli" %}}

## Sub-heading 3 {#sub-heading3}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="core/cli" %}}

{{% section-end mod="core/cli" %}}

