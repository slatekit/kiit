---
title: "Data"
date: 2019-11-17T23:55:42-05:00
section_header: Data
---

# Overview
Slate Kit contains 3 modules for working with **data and databases**. These allow you to leverage existing code for low-level database calls, mid-level interfaces/abstractions for Entities/Repositories/Services or high-level ORM functionality.
{{% break %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Low-Level</strong></td>
        <td>Provide a low-level database API for JDBC and/or Android Sqlite</td>
    </tr>
    <tr>
        <td><strong>2. Entities </strong> </td>
        <td>Provide interfaces/defaults for Entities / Repositories / Services.</td>                     
    </tr>
    <tr>
        <td><strong>3. ORM</strong></td>
        <td>Provide a light-weight ORM for both Android ( Sqlite ) and Server. </td>
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
        <td><strong><a class="url-ch" href="arch/data#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/data#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/data#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/data#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/data#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/data#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/data#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/data#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>

{{% section-end mod="arch/data" %}}

# Status
This component is currently stable and works with **MySql**. 
Future versions will include support for:
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>PostGres</strong></td>
        <td>In Progress</td>
        <td>Support for PostGres database</td>
    </tr>
    <tr>
        <td><strong>Android</strong></td>
        <td>In Progress</td>
        <td>This is currently but not yet open-sourced.</td>
    </tr>
    <tr>
        <td><strong>Exposed</strong></td>
        <td>Future</td>
        <td>Integration with the **JetBrains Exposed ORM** instead of Slate Kit ORM</td>
    </tr>
</table>

{{% section-end mod="arch/data" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        // Common code for all components
        compile 'com.slatekit:slatekit-common:1.0.0'

        // For database calls to work with JDBC
        compile 'com.slatekit:slatekit-db:1.0.0'

        // Entities / Repositories / Service interfaces and defaults
        compile 'com.slatekit:slatekit-entities:1.0.0'

        // ORM functionality to map to/from models and records and query
        compile 'com.slatekit:slatekit-orm:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="Data"
    package="slatekit.entities"
    jar="slatekit.entities.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-entities"
    gitAlias="slatekit/src/lib/kotlin/slatekit-entities"
    url="arch/data"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_Entities.kt"
%}}
{{% section-end mod="arch/data" %}}

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
{{% section-end mod="arch/data" %}}

# Sample
{{< highlight kotlin >}}

    fun quick_sample() {
        
    }

{{< /highlight >}}
{{% section-end mod="arch/data" %}}


# Guide
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Connections</strong></td>
        <td>Setting up database connections</td>
        <td><a href="arch/data/#connections" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Databases</strong> </td>
        <td>Low-level database calls and operations</td> 
        <td><a href="arch/data/#databases" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Mappers</strong></td>
        <td>Setting up mappers for converting to/from models to records</td>
        <td><a href="arch/data/#mappers" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Repositories</strong></td>
        <td>Using Repositories for common CRUD operations on data</td>
        <td><a href="arch/data/#repos" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Services</strong></td>
        <td>Entity Services for managing data models</td>
        <td><a href="arch/data/#services" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Events</strong></td>
        <td>Load a command at start up</td>
        <td><a href="arch/data/#events" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. Migrations</strong></td>
        <td>Load a command at start up</td>
        <td><a href="arch/data/#migrations" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>8. ORM</strong></td>
        <td>Light-weight ORM layer to managing/querying databases</td>
        <td><a href="arch/data/#orm" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/data" %}}


## Connections {#connections}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Databases {#databases}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Mappers {#mappers}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Repository {#repos}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Services {#services}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Events {#events}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## Migrations {#migrations}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

## ORM {#orm}
coming soon
{{< highlight kotlin >}}

    fun setup() {
        
    }

{{< /highlight >}}
{{% feature-end mod="arch/data" %}}

{{% section-end mod="arch/data" %}}

