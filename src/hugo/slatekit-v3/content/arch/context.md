---
title: "Context"
date: 2019-11-17T23:55:41-05:00
section_header: Context
---

# Overview
Describe this {COMPONENT_NAME} concisely in 2-3 sentences.
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
</table>
{{% section-end mod="core/cli" %}}

# Status
coming soon
{{% section-end mod="core/cli" %}}

# Install
coming soon
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-{COMPONENT_ID}:0.9.9'
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
coming soon
{{< highlight kotlin >}}

    fun quick_sample() {
        
    }

{{< /highlight >}}
{{% section-end mod="core/cli" %}}

# Goals
coming soon
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Goal A</strong></td>
        <td>Description of goal</td>
    </tr>
    <tr>
        <td><strong>2. Goal B</strong> </td>
        <td>Description of goal</td>                     
    </tr>
    <tr>
        <td><strong>3. Goal C</strong></td>
        <td>Description of goal</td>
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
        <td><strong>1. Concept A</strong></td>
        <td>Description of concept</td>
    </tr>
    <tr>
        <td><strong>2. Concept B</strong> </td>
        <td>Description of concept</td>                     
    </tr>
    <tr>
        <td><strong>3. Concept C</strong></td>
        <td>Description of concept</td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}

# Features
coming soon
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Feature A</strong></td>
        <td>Description of feature</td>
        <td><a href="arch/app/#feature1" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Feature B</strong> </td>
        <td>Description of feature</td> 
        <td><a href="arch/app/#feature2" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Feature C</strong></td>
        <td>Description of feature</td>
        <td><a href="arch/app/#feature3" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="core/cli" %}}


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

{{% section-end mod="core/cli" %}}

