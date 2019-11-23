---
title: "Cache"
date: 2019-11-17T23:55:41-05:00
section_header: Cache
---

# Overview
The Slate Cache is a light-weight Cache for both Android and Server.
{{% break %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Light-Weight</strong></td>
        <td>Simple, light-weight with default implementations for sync and async based Caches.</td>
    </tr>
    <tr>
        <td><strong>2. Diagnostics </strong> </td>
        <td>Provides a reasonable level of diagnostics and cache metrics</td>                     
    </tr>
    <tr>
        <td><strong>3. Channels</strong></td>
        <td>Async based cache leverages Channels for write operations</td>
    </tr>
</table>
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
        <td><strong><a class="url-ch" href="arch/cache#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>
{{% section-end mod="arch/cache" %}}

# Status
This component is **NOT** currently ready. Also, the Docs are being worked on.
{{% break %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-cache:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="Cache"
    package="slatekit.cache"
    jar="slatekit.cache.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-cache"
    gitAlias="slatekit/src/lib/kotlin/slatekit-cache"
    url="arch/cache"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_Cache.kt"
%}}
{{% section-end mod="arch/cache" %}}

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
{{% section-end mod="arch/cache" %}}

# Sample
coming soon
{{< highlight kotlin >}}

    fun quick_sample() {
        
    }

{{< /highlight >}}
{{% section-end mod="arch/cache" %}}


# Features
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Usage</strong></td>
        <td>Usage of the features</td>
        <td><a href="arch/cache/#usage" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Stats</strong> </td>
        <td>Gettign stats and cache diagnostics</td> 
        <td><a href="arch/cache/#stats" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Sync</strong></td>
        <td>How to convert raw text into parsed parameters</td>
        <td><a href="arch/cache/#sync" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Async</strong></td>
        <td>Working with parsed commands as CLI Requests</td>
        <td><a href="arch/cache/#asyc" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/cache" %}}


## Usage {#usage}
COMIN SOON.
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

## Stats {#stats}
COMIN SOON.
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

## Sync {#sync}
COMIN SOON.
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

## Async {#async}
Async flow is based on using CoRoutines and Channels for writes.
COMIN SOON.
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

{{% section-end mod="arch/cache" %}}

