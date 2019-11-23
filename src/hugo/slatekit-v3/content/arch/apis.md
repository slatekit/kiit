---
title: "Apis"
date: 2019-11-17T23:55:41-05:00
section_header: Apis
---

# Overview
Slate Kit uses a somewhat new, yet familiar paradigm to building out APIs by enriching normal Kotlin methods and making them easily discoverable and accessible across as range of hosts. This will resemble an **RPC** type approach, but contains some support ad concepts from **REST**. More specifically, APIs in Slate Kit can be hosted and made available as Web/HTTP APIs, on the CLI, or called from requests from queues or files for automation purposes. Under the hood, Slate Kit simply leverages existing HTTP servers ( currently **Ktor** ), to host, discover, manage, and access Slate Kit APIs. Our {{% sk-link-arch page="cli" name="CLI" %}} also supports the ability to host Slate Kit APIs. This specific approach to API development in Slate Kit is referred to as **Universal APIs**.
{{% break %}}

# Create
{{< highlight bash >}}
    
    slatekit new api -name="SampleAPI" -package="mycompany.apps"
    
{{< /highlight >}}

{{% sk-link-cli %}}
{{% break %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Accessable</strong></td>
        <td><strong>Write once</strong> and have APIs hosted and/or run anywhere ( e.g. <strong>Web, CLI, from File/Queue</strong> sources )</td>
    </tr>
    <tr>
        <td><strong>2. Discoverable</strong> </td>
        <td>Folder like heirarchical and drill-down discovery of APIS using a <strong>3 part routing format { area / api / action }</strong></td>                     
    </tr>
    <tr>
        <td><strong>3. Natural</strong></td>
        <td>Allow <strong>normal methods</strong> to be easily turned to API actions with <strong>strong types</strong>, <strong>automatic docs</strong> and accurate {{% sk-link-arch page="results" name="Results" %}}</td>
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
This component is currently stable and uses JetBrains **Ktor** as the underlying HTTP server for hosting Slate Kit APIs as Web/HTTP APIs. 
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td>**Open-API**</td>
        <td>Upcoming</td>
        <td>Auto-generate Open-APIs docs from Actions</td>
    </tr>
    <tr>
        <td>**Postman**</td>
        <td>Upcoming</td>
        <td>Auto-generate Postman scripts from Actions</td>
    </tr>
    <tr>
        <td>**Serialization**</td>
        <td>Upcoming</td>
        <td>Replace internal serializer with Jackson, Moshi, or kotlinx.serialization</td>
    </tr>
    <tr>
        <td>**Streaming**</td>
        <td>Upcoming</td>
        <td>Support for streaming</td>
    </tr>
</table>
{{% section-end mod="arch/apis" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-apis:1.0.0'
    }

{{< /highlight >}}

{{% sk-module 
    name="APIs"
    package="slatekit.apis"
    jar="slatekit.apis.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-apis/src/main/kotlin/slatekit/apis"
    gitAlias="slatekit/src/lib/kotlin/slatekit-apis"
    url="arch/apis"
    uses="slatekit.results, slatekit.common"
    exampleUrl="Example_APIs.kt"
    exampleFileName="Example_APIs.kt"
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
This is a quick and simple example of creating an API using the Slate Kit **Universal API paradigm**. This API is then accessible on the CLI and Web
{{% sk-tip-generic text="Slate Kit APIs have a 3 part routing convention: {AREA} / {API} / {ACTION}" %}}
{{< highlight kotlin >}}
      
    import slatekit.apis.*
    import slatekit.common.DateTime
    import slatekit.common.Request

    @Api(area = "app", name = "movies", desc = "Create and manage movies", auth = AuthModes.NONE)
    class MovieApi {
        // Using explicit fields
        @Action()
        fun createSample(title:String, playing:Boolean, cost:Int, released: DateTime):Movie {
            return Movie.of(title, playing, cost, released)
        }

        // Using request object
        @Action()
        fun createWithRequest(req:Request):Movie {
            return Movie.of(req.getString("title"), req.getBool("playing"), req.getInt("cost"), req.getDateTime("released"))
        }
    }

{{< /highlight >}}

{{% section-end mod="arch/apis" %}}


# Features
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Setup</strong></td>
        <td>Description of feature</td>
        <td><a href="arch/apis/#setup" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Routes</strong> </td>
        <td>How routes work actions are called</td> 
        <td><a href="arch/apis/#routes" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Config</strong></td>
        <td>Configuration options including auth, verbs, sources and more </td>
        <td><a href="arch/apis/#config" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Requests</strong></td>
        <td>How to handle requests and parameters</td>
        <td><a href="arch/apis/#requests" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Responses</strong></td>
        <td>How to return responses</td>
        <td><a href="arch/apis/#responses" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Errors</strong></td>
        <td>Modeling of successes, failures and dealing with status codes</td>
        <td><a href="arch/apis/#errors" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. Middleware</strong></td>
        <td>Incorporate middle ware globally or per API</td>
        <td><a href="arch/apis/#middleware" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>8. Web</strong></td>
        <td>How to make APIs hosted in a Web Server (Ktor)</td>
        <td><a href="arch/apis/#web" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>9. CLI</strong></td>
        <td>How to make APIs hosted in a CLI ( Slate Kit CLI )</td>
        <td><a href="arch/apis/#cli" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>10. Files</strong></td>
        <td>How to handle requests from Files</td>
        <td><a href="arch/apis/#files" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>11. REST</strong></td>
        <td>How to set up partial REST compatible actions</td>
        <td><a href="arch/apis/#rest" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>

{{% section-end mod="arch/apis" %}}


## Setup {#setup}
APIs are developed as normal Kotlin methods. The only difference is that they are enriched with annotations and/or configuration during registration, to provided metadata to the Slate Kit API Server indicated how they should be accessed and managed. 
{{% sk-tip-generic text="Annotations can be avoided and instead the configurations can be explicitly supplied during registration of the APIs into the API server ( see web/cli sections below for setting up the server)" %}}

### 1: Annotations
This approach is convenient and puts all relevant metadata at the source.
{{< highlight kotlin >}}
      
    import slatekit.apis.*
    import slatekit.common.DateTime
    import slatekit.common.Request

    @Api(area = "manage", name = "movies", desc = "Create and manage movies", auth = AuthModes.NONE)
    class MovieApi {
        // Using explicit fields
        @Action()
        fun createSample(title:String, playing:Boolean, cost:Int, released: DateTime):Movie {
            return Movie.of(title, playing, cost, released)
        }

        // Using request object
        @Action()
        fun createWithRequest(req:Request):Movie {
            return Movie.of(req.getString("title"), req.getBool("playing"), req.getInt("cost"), req.getDateTime("released"))
        }
    }

{{< /highlight >}}

### 2: Registration
This approach reduces the dependency on Slate Kit, and requires that the metadata be supplied during registration of the API, and all the actions assume that the annotation values are inherited from the parent Api metadata.
{{< highlight kotlin >}}
      
    import slatekit.apis.*
    import slatekit.common.DateTime
    import slatekit.common.Request

    class MovieApi {
        // Using explicit fields
        fun createSample(title:String, playing:Boolean, cost:Int, released: DateTime):Movie {
            return Movie.of(title, playing, cost, released)
        }

        // Using request object
        fun createWithRequest(req:Request):Movie {
            return Movie.of(req.getString("title"), req.getBool("playing"), req.getInt("cost"), req.getDateTime("released"))
        }
    }

    // ... Registration code ( see other sections for more details )
    val api = slatekit.apis.core.Api(
                    instance = MovieApi(ctx), 
                    area = "manage", 
                    name = "movies", 
                    auth = AuthMode.None
                )

{{< /highlight >}}

{{% feature-end mod="arch/apis" %}}

## Routes {#routes}
API routes consist of a **3 part ( {AREA} / {API} / {ACTION} ) routing convention** to enfore standards and simplify the discovery of the routes. 
{{% sk-tip-generic text="Sample: /manage/movies/createSample" %}}

### 1. Parts
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Part</strong></td>
        <td><strong>Example</strong></td>
        <td><strong>Note</strong></td>
    </tr>
    <tr>
        <td><strong>AREA</strong></td>
        <td>manage</td>
        <td>Represents a logical grouping of 1 or more APIs</td>
    </tr>
    <tr>
        <td><strong>API</strong></td>
        <td>movies</td>
        <td>Represents an actual API associated with a Kotlin class</td>
    </tr>
    <tr>
        <td><strong>ACTION</strong></td>
        <td>createSample</td>
        <td>Represents an action/endpoint on an API, and maps to a Kotlin method</td>
    </tr>
</table>

{{% break %}}

### 2. Example
{{< highlight kotlin >}}
     
     POST localhost:5000/manage/movies/createSample 
     {
        "title": "Dark Knight",
        "playing": true,
        "cost" : 12.50,
        "released": "2018-07-18T00:00:00Z"
     }

{{< /highlight >}}
{{% break %}}

### 3. Discovery
Because the routes are standardized on a 3 part heirarchical format, this makes discovery of all **Areas, APIS, Actions, Inputs(for actions)** incredibly straight-forward for both Web and CLI hosted APIs. You can drill down from areas into actions using an approach that resembles navigating a folder and showing the items.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Discover</strong></td>
        <td><strong>CLI</strong></td>
        <td><strong>Web</strong></td>
        <td><strong>Note</strong></td>
    </tr>
    <tr>
        <td><strong>Areas</strong></td>
        <td>?</td>
        <td>/help</td>
        <td>Lists <strong>Areas</strong> available. <br/>
            E.g. manage, discover
        </td>
    </tr>
    <tr>
        <td><strong>APIs in Area</strong></td>
        <td>manage?</td>
        <td>/manage/help</td>
        <td>Lists <strong>APIs</strong> available in the <strong>manage</strong> area<br/>
            E.g. movies, concerts
        </td>
    </tr>
    <tr>
        <td><strong>Actions on API</strong></td>
        <td>manage.movies?</td>
        <td>/manage/movies/help</td>
        <td>Lists <strong>Actions</strong> available in <strong>manage/movies</strong> API<br/>
            E.g. create, update, delete, disable
        </td>
    </tr>
    <tr>
        <td><strong>Inputs on Action</strong></td>
        <td>manage.movies.createSample?</td>
        <td>/manage/movies/createSample/help</td>
        <td>Lists <strong>Inputs</strong> for <strong>manage / movies /createSample</strong> Action<br/>
            E.g. title=string, cost=double
        </td>
    </tr>
</table>

{{% feature-end mod="arch/apis" %}}

## Config {#config}
There are several annotation properties available to configure APIs and Actions. 

### 1. API
These are all the properties for the **@Api** annotation to put on a class to indicate it should be accessable as an Api.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Type</strong></td>
        <td><strong>Name</strong></td>
        <td><strong>Required</strong></td>
        <td><strong>Default</strong></td>
        <td><strong>Purpose</strong></td>
        <td><strong>Example</strong></td>
    </tr>
    <tr>
        <td><strong>@Api</strong></td>
        <td><strong>area</strong></td>
        <td><strong>Required</strong></td>
        <td>n/a</td>
        <td>Represents the logical area of the API</td>
        <td>manage</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>name<strong></td>
        <td><strong>Required</strong></td>
        <td>n/a</td>
        <td>Represents the name of this API</td>
        <td>movies</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>desc</strong></td>
        <td>Optional</td>
        <td>Empty</td>
        <td>Description of the API</td>
        <td>Manages movies</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>auth</strong></td>
        <td>Optional</td>
        <td>None</td>
        <td>Specifies how the authentication should work</td>
        <td>{{% sk-link-code component="apis" filepath="apis/AuthMode.kt" name="AuthMode.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>roles</strong></td>
        <td>Optional</td>
        <td>Empty</td>
        <td>List of roles allowed to access this API</td>
        <td>{{% sk-link-code component="common" filepath="common/auth/Roles.kt" name="Roles.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>access</strong></td>
        <td>Optional</td>
        <td>Public</td>
        <td>Desribes visibility of the API</td>
        <td>{{% sk-link-code component="apis" filepath="apis/Access.kt" name="Access.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>verb</strong></td>
        <td>Optional</td>
        <td>Auto</td>
        <td>Desribes how the Verbs should be handled </td>
        <td>{{% sk-link-code component="apis" filepath="apis/Verbs.kt" name="Verbs.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>sources</strong></td>
        <td>Optional</td>
        <td>All</td>
        <td>Indicates where this API can handle requests from</td>
        <td>{{% sk-link-code component="common" filepath="common/Sources.kt" name="Sources.kt" %}}</td>
    </tr>
</table>

### 2. Action
These are all the properties for the **@Action** annotation to be put on methods to indicate it should be available as an action/endpoint

<div class="alert alert-warning" role="alert">
  1. Several @Action properties can reference the @Api properties via <strong>@parent</strong><br/>
  2. Many of the @Action properties are defaulted
</div>

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>@Action</strong></td>
        <td><strong>area<strong></td>
        <td><strong>Required</strong></td>
        <td><strong>Default</strong></td>
        <td>Represents the logical area of the API</td>
        <td>Example</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>name</strong></td>
        <td>Optional</td>
        <td>method name</td>
        <td>Represents the name of this Action</td>
        <td>createSample</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>desc</strong></td>
        <td>Optional</td>
        <td>Empty</td>
        <td>Description of the Action</td>
        <td>Create sample movie</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>roles</strong></td>
        <td>Optional</td>
        <td>Empty</td>
        <td>List of roles allowed to access this API</td>
        <td>{{% sk-link-code component="common" filepath="common/auth/Roles.kt" name="Roles.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>access</strong></td>
        <td>Optional</td>
        <td>Public</td>
        <td>Desribes visibility of the API</td>
        <td>{{% sk-link-code component="apis" filepath="apis/Access.kt" name="Access.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>verb</strong></td>
        <td>Optional</td>
        <td>Auto</td>
        <td>Desribes how the Verbs should be handled </td>
        <td>{{% sk-link-code component="apis" filepath="apis/Verbs.kt" name="Verbs.kt" %}}</td>
    </tr>
    <tr>
        <td><strong>-</strong></td>
        <td><strong>sources</strong></td>
        <td>Optional</td>
        <td>All</td>
        <td>Indicates where this API can handle requests from</td>
        <td>{{% sk-link-code component="common" filepath="common/Sources.kt" name="Sources.kt" %}}</td>
    </tr>
</table>

{{% feature-end mod="arch/apis" %}}

## Requests {#requests}
Requests in Slate Kit are abstracted out as {{% sk-link-code component="common" filepath="common/requests/Request.kt" name="Request.kt" %}}. They are implementations for a Web Request and CLI request.
{{< highlight kotlin >}}
     
    val request:Request = CommonRequest(
                path = "app.users.activate",
                parts = listOf("app", "users", "activate"),
                source = Source.CLI,
                verb = Verbs.POST,
                meta = InputArgs(mapOf("api-key" to "ABC-123")),
                data = InputArgs(mapOf("userId" to 5001)),
                raw = "the raw HTTP SparkJava send or CLI ShellCommand",
                tag = Random.uuid()
        )

        // NOTES: ResultSupport trait builds results that simulate Http Status codes
        // This allows easy construction of results/status from a server layer
        // instead of a controller/api layer

        // CASE 1: Get path of the request, parts, and associated 3 part routes
        println( request.path )
        println( request.parts )
        println( request.area   )
        println( request.name   )
        println( request.action )

        // CASE 2: Get the verb (For the CLI - the verb will be "cli")
        println( request.verb )

        // CASE 3: Get the tag
        // The request is immutable and the tag field is populated with
        // a random guid, so if an error occurs this tag links the request to the error.
        println( request.tag )

        // CASE 4: Get metadata named "api-key"
        println( request.meta.getString("api-key") )
        println( request.meta.getInt("sample-id") )
        println( request.meta.getIntOrNull("sample-id") )
        println( request.meta.getIntOrElse("sample-id", -1) )

        // CASE 5: Get a parameter named "userId" as integer
        // IF its not there, this will return a 0 as getInt
        // returns a non-nullable value.  
        // The value is obtained from query string if get, otherwise, 
        // if the request is a post, the value is
        // first checked in the body ( json data ) before checking
        // the query params
        println( request.data.getInt("userId") )
        println( request.data.getIntOrNull("userId") )
        println( request.data.getIntOrElse("userId", -1) )
     
{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Responses {#responses}
There are 2 ways to returns responses/values from methods. The first is to simply return the exact value. The second is to wrap the value into a Slate Kit {{% sk-link-arch page="results" name="Result" %}}.

### 1. Result model
{{< highlight kotlin >}}
      
    // Case 1: Return explicit value( will return an HTTP 200)
    return Movie.of(title, playing, cost, released)

    // Case 2. Return value wrapped as Outcome<Movie> = Result<Movie, Err>
    val movie = Movie.of(title, playing, cost, released)
    return Outcomes.success(movie)
      
{{< /highlight >}}

### 2. Response JSON
JSON Responses from APIs always the following fields. The **value** field will represent the payload returned from your method. The **success, code, msg, err** fields are representing by the {{% sk-link-arch page="results" name="Result" %}} component for modeling successes/failures.
{{< highlight json >}}

    {
        "success": true,
        "code": 200001,
        "meta": null,
        "value": {
            "version": "1.0.0",
            "date": "2019-08-10"
        },
        "msg": "Success",
        "err": null,
        "tag": null
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Errors {#errors}
You can accurately model successes and failures using {{% sk-link-arch page="results" name="Result" %}}. Result ( and the Outcome typealias ) help to model errors accurately which can then be easily converted to Http status codes and responses.
{{< highlight kotlin >}}
     
    @Action()
    fun createSampleOutcome(req:Request, title:String, playing:Boolean, cost:Int, released: DateTime):Outcome<Movie> {
        val result:Outcome<Movie> = when {
            !canCreate(req)       -> Outcomes.denied ("Not allowed to create")
            title.isNullOrEmpty() -> Outcomes.invalid("Title missing")
            !playing              -> Outcomes.ignored("Movies must be playing")
            cost > 20             -> Outcomes.errored("Prices must be reasonable")
            else                  -> {
                // Simple simulation of creation
                Outcomes.success( Movie.of(title, playing, cost, released) )
            }
        }
        return result 
    }
     
{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## Middleware {#middleware}

{{% sk-tip-generic text="Functionality is available but docs are not yet ready." %}}
{{% feature-end mod="arch/apis" %}}

## Web {#web}
You can host Slate Kit APIs as Web APIs using the default Http Engine which is **Ktor**.
<div class="alert alert-warning" role="alert">
  Use the generator to create Slate Kit APIs
</div>

{{< highlight kotlin >}}
     
    fun runServer() {
        // ====================================
        // Slate Kit Setup
        // 1. Settings ( defaults: port = 5000, prefix = /api/)
        val settings = ServerSettings(docs = true, docKey = "abc123")

        // 2. APIs ( defaults applied )
        val apis = listOf(
                slatekit.apis.core.Api(
                    klass = SampleApi::class, 
                    singleton = SampleApi(ctx)
                )
        )

        // 3. API host
        val apiHost = ApiServer.of( ctx, apis, auth = null)

        // 4. Ktor handler: Delegates Ktor requests to Slate Kit
        val handler = KtorHandler(ctx, settings, apiHost)

        // ====================================
        // Jet Brains Ktor setup
        val server = embeddedServer(Netty, settings.port) {
            routing {

                // Root
                get("/") { ping(call) }

                // Your own custom path
                get(settings.prefix + "/ping") { ping(call) }

                // Your own multi-path route
                get("module1/feature1/action1") {
                    KtorResponse.json(call, Success("action 1 : " + DateTime.now().toString()).toResponse())
                }

                // Remaining outes beginning with /api/ to be handled by Slate Kit API Server
                handler.register(this)
            }
        }

        // Start Ktor server
        server.start(wait = true)
    }

{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## CLI {#cli}
You can host Slate Kit **Universal APIs** on the CLI using the Slate Kit {{% sk-link-arch page="cli" name="CLI" %}}

### CLI Setup
{{< highlight kotlin >}}
      
    // 1. The API keys( DocApi, SetupApi are authenticated using an sample API key )
    val keys = listOf(ApiKey( name ="cli", key = "abc", roles = "dev,qa,ops,admin"))

    // 2. Authentication
    val auth = Authenticator(keys)

    // 3. Load all the Slate Kit Universal APIs
    val apis = listOf(
            slatekit.apis.core.Api(
                klass = SampleApi::class, 
                singleton = SampleApi(ctx)
            )
    )

    // 4. Makes the APIs accessible on the CLI runner
    val cli = CliApi(
            ctx = ctx,
            auth = auth,
            settings = CliSettings(enableLogging = true, enableOutput = true),
            apiItems = apis,
            metaTransform = {
                listOf("api-key" to keys.first().key)
            },
            serializer = {item, type -> Content.csv(slatekit.meta.Serialization.csv().serialize(item))}
    )

    // 5. Run interactive mode
    return cli.run()
       
{{< /highlight >}}

### CLI Example
You can then access this API on the CLI by hosting it in the Slate Kit CLI component.
The request would like this:
{{< highlight kotlin >}}
     
     :> app.movies.createSample -title="Dark Knight" -playing=true -cost=12.50 -released="2018-07-18T00:00:00Z"
      
{{< /highlight >}}
{{% feature-end mod="arch/apis" %}}

## File
You can then call this API from a request saved to a file
{{% sk-tip-generic text="Functionality is available but docs are not yet ready." %}}
{{< highlight kotlin >}}

     // Assume file is ~/dev/slatekit/samples/apis/file-sample.json
     {
        "path": "/app/movies/createSample",
        "source": "file",
        "version": "1.0",
        "tag" : "",
        "timestamp": null,
        "meta": { }
        "data": {
            "title": "Dark Knight",
            "playing": true,
            "cost" : 12.50,
            "released": "2018-07-18T00:00:00Z"
         }
     }
      
{{< /highlight >}}
{{% section-end mod="arch/apis" %}}

