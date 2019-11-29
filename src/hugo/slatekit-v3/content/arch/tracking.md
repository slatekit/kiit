---
title: "Tracking"
date: 2019-11-17T23:55:41-05:00
section_header: Tracking
---

# Overview
The Tracking components in Slate Kit are useful for diagnostics, specifically for tracking, storing and viewing values and success/failure states. This are especially useful for jobs, reports, caching, and ETL (Extract, Transform, Load) operations. **These are NOT a replacement for monitoring libraries like Micrometer**. 

{{% break %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Supplemental</strong></td>
        <td>Supplement the {{% sk-link-arch page="results" name="Results" %}} for tracking logical categories of errors.</td>
    </tr>
    <tr>
        <td><strong>2. Inspection</strong> </td>
        <td>Tracking and storing of complex objects/values for <strong>runtime level inspection</strong></td>
    </tr>
    <tr>
        <td><strong>3. Structured</strong></td>
        <td>Provides data structures to assist with <strong>structured</strong> logging and alerts</td>
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
        <td><strong><a class="url-ch" href="arch/tracking#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/tracking#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/tracking#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/tracking#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/tracking#features">Guide</a></strong></td>
        <td>List all the features supported</td>
    </tr>
</table>

{{% section-end mod="arch/tracking" %}}

# Status
This component is currently stable and can be used for both **Android and Server**

{{% section-end mod="arch/tracking" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-tracking:1.0.0'
    }

{{< /highlight >}}

{{% sk-module 
    name="Tracking"
    package="slatekit.tracking"
    jar="slatekit.tracking.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-tracking/src/main/kotlin/slatekit/tracking"
    gitAlias="slatekit/src/lib/kotlin/slatekit-tracking"
    url="arch/tracking"
    uses="slatekit.results, slatekit.common"
    exampleUrl="Example_Tracking.kt"
    exampleFileName="Example_Tracking.kt"
%}}

{{% section-end mod="arch/tracking" %}}

# Requires
This component uses several components from the Slate Kit Utilities.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><a class="url-ch" href="arch/results">Slate Kit - Results</a></td>
        <td>To model successes and failures with optional status codes</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/overview">Slate Kit - Common</a></td>
        <td>Common utilities for both android + server</td>
    </tr>
</table>

{{% section-end mod="arch/tracking" %}}

# Sample
The context can be constructed manually or using convenience methods that build the context from the command line args, and configs.
{{< highlight kotlin >}}
    
    import slatekit.common.CommonContext

    // Create simple context
    val ctx1 = CommonContext.simple("demoapp")

{{< /highlight >}}

{{% section-end mod="arch/tracking" %}}


# Guide
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Calls</strong></td>
        <td>Tracks total processed, succeeded, failed operations.</td>
        <td><a href="arch/tracking/#args" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Counts</strong> </td>
        <td>Tracks total successes and all error categories from {{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}}</td> 
        <td><a href="arch/tracking/#env" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Lasts</strong></td>
        <td>Tracks the last request and response/result values grouped by {{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}}</td>
        <td><a href="arch/tracking/#conf" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Event</strong></td>
        <td>Models a past event with fields and is used for structured logging.</td>
        <td><a href="arch/tracking/#logs" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Events</strong></td>
        <td>Event handler for handling/tracking results based on {{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}}</td>
        <td><a href="arch/tracking/#encrypt" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Recorder</strong></td>
        <td>Records diagnostics via the calls, counts, events, logger components.</td>
        <td><a href="arch/tracking/#build" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. Updates</strong></td>
        <td>Tracks an updating value by storing its create/update time, value and total changes.</td>
        <td><a href="arch/tracking/#about" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/tracking" %}}


## Calls {#calls}
This is a simple counter to keep track of the total times an operation is called / passed / failed. These can be integrated with more comprehensive Timers/Guages from such libraries as **Micrometer**. See {{% sk-link-code component="tracking" filepath="tracking/Calls.kt" name="Calls" %}} for more info.
{{< highlight kotlin >}}
      
    val id = SimpleIdentity("beta", "setup", Agent.Job, "dev")
    val calls = Calls(id)

    // Use case 1.1: Increment attempt
    calls.inc()

    // Use case 1.2: Increment number of passed calls
    calls.passed()

    // Use case 1.3: Increment number of failed calls
    calls.failed()

    // Use case 1.4: Get the total number of times the operation was run
    calls.totalRuns()

    // Use case 1.4a: Get the last error
    calls.totalPassed()

    // Use case 1.4b: Get the last error
    calls.totalFailed()

    // Use case 1.5: Determine if operation has run ( if totalPassed > 0 )
    calls.hasRun()

    // Use case 1.6: Get the last time the call was made
    calls.lastTime()

    // Use case 1.7: Get the last error
    calls.lastError()
         
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}

## Counts {#counts}
Counters count the totals across the logical errors groups from 
{{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}}. These can be integrated with more comprehensive Timers/Guages from such libraries as **Micrometer**. See {{% sk-link-code component="tracking" filepath="tracking/Counters.kt" name="Counters" %}} for more info.
{{< highlight kotlin >}}
      
    /** 
     * Slate Kit heavily uses logical success / failure categories @see[slatekit.results.Status]
     * 1. Succeeded   -> Success
     * 2. Pending     -> In Progress
     * 3. Denied      -> Security related
     * 4. Ignored     -> Ignored items
     * 5. Invalid     -> Invalid data
     * 6. Errored     -> Expected errors
     * 7. Unexpected  -> Unexpected errors
     * This counters component keeps track of various categories
     */
    val counts = Counters(id, custom = listOf("deferred"))

    // Use case 2.1: Increment total processed
    counts.incProcessed()
    counts.incSucceeded()
    counts.incDenied()
    counts.incInvalid()
    counts.incIgnored()
    counts.incErrored()
    counts.incUnexpected()
    counts.inc("deferred")

    // Use case 2.2: Increment total processed
    counts.decProcessed()
    counts.decSucceeded()
    counts.decDenied()
    counts.decInvalid()
    counts.decIgnored()
    counts.decErrored()
    counts.decUnexpected()
    counts.dec("deferred")

    // Use case 2.3: Get totals
    counts.totalProcessed()
    counts.totalSucceeded()
    counts.totalDenied()
    counts.totalInvalid()
    counts.totalIgnored()
    counts.totalErrored()
    counts.totalUnexpected()
    counts.totalCustom("deferred")
     

{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}

## Lasts {#lasts}
Lasts track request and response/result values for **inspecting** values at runtime. These become useful during diagnosing specific requests/response that may match certain criteria and having access to the full object vs simply logging the data. See {{% sk-link-code component="tracking" filepath="tracking/Lasts.kt" name="Lasts" %}} for more info.
{{< highlight kotlin >}}
     
    // Lets setup some example request / result types
    data class UserRequest(val id:String, val action:String)
    data class UserResult (val id:String, val action:String, val msg:String)
    val sampleRequest = UserRequest("uuid123", "register")
    val sampleResult = UserResult("uuid123", "registration", "registered as beta user")

    // Use case 3.1: Track various states
    val lasts = Lasts<UserRequest, UserResult, Err>(Identity.test("job1"))
    lasts.succeeded (this, sampleRequest, sampleResult)
    lasts.denied    (this, sampleRequest, Err.of("Not authorized"))
    lasts.invalid   (this, sampleRequest, Err.of("Not a beta user"))
    lasts.ignored   (this, sampleRequest, Err.of("In active user"))
    lasts.errored   (this, sampleRequest, Err.of("Unable to determine user type"))
    lasts.unexpected(this, sampleRequest, Err.of("Unexpected error while handling analytics"))

    // Use case 3.2: Get info
    println(lasts.lastSuccess())
    println(lasts.lastDenied())
    println(lasts.lastInvalid())
    println(lasts.lastIgnored())
    println(lasts.lastErrored())
    println(lasts.lastUnexpected())

    // Use case 3.3: Clear the tracking
    lasts.clear()
    
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}

## Event {#event}
The event model is a general purpose data structure to represent a past event with extensible fields. This model can then be used for structured logging, alerts, and analytics. See {{% sk-link-code component="tracking" filepath="tracking/Event.kt" name="Event" %}} for more info.
{{< highlight kotlin >}}
     
        
    val event = Event(
         area  = "registration",
         name  = "NEW_ANDROID_REGISTRATION",
         agent ="job",
         env   = "dev",
         uuid  = "abc-123-xyz",
         desc  = "User registration via mobile",
         status= Codes.SUCCESS,
         target= "registration-alerts",
         tag   = "a1b2c3",
         fields= listOf(
             Triple("region" , "usa"     , ""),
             Triple("device" , "android" , "")
         )
    )
    
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}

## Events {#events}
You have have to the logs/factory to create loggers.
See {{% sk-link-code component="tracking" filepath="tracking/Events.kt" name="Events" %}} for more info.
{{< highlight kotlin >}}
     
     // Lets setup some example request / result types
    data class UserRequest(val id:String, val action:String)
    data class UserResult (val id:String, val action:String, val msg:String)
    val sampleRequest = UserRequest("uuid123", "register")
    val sampleResult = UserResult("uuid123", "registration", "registered as beta user")

    /** Setup a simple Events class that can handle Requests/Results/Failures of type
     * request = UserRequest, result = UserResult, error = @see[slatekit.results.Err]
     * This also needs to be able to convert the request / result / failure to an event.
     */
    val events = Events<UserRequest, UserResult, Err>(
            successConverter = { sender, req, res -> event.copy(uuid = res.id, desc = res.msg)},
            failureConverter = { sender, req, err -> event.copy(uuid = req.id, desc = "Failed registering beta user", status = err.status)}
    )

    // Use case 4.2: Model a sample request and result using slatekit.results.Outcome and have the handler
    // process it accordingly based on its category
    events.handle(this, sampleRequest, Outcomes.success(sampleResult))
    events.handle(this, sampleRequest, Outcomes.denied(Err.of("Not authorized")))
    events.handle(this, sampleRequest, Outcomes.invalid(Err.of("Not a beta user")))
    events.handle(this, sampleRequest, Outcomes.ignored(Err.of("In active user")))
    events.handle(this, sampleRequest, Outcomes.errored(Err.of("Unable to determine user type")))
    events.handle(this, sampleRequest, Outcomes.unexpected(Err.of("Unexpected error while handling analytics")))

    
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}


## Recorder {#recorder}
The recorder component is a combination of all the above and can be used to record a request / result across calls, counts, lasts, events, logs.
See {{% sk-link-code component="tracking" filepath="tracking/Recorder.kt" name="Recorder" %}} for more info.
{{< highlight kotlin >}}
      
    
    val recorder = Recorder<UserRequest, UserResult>(Identity.test("job1"), LoggerConsole(), calls, counts, lasts, events)

    // Use case 5: Record the request / result in the recorder which will track the call, counts, lasts, and events
    recorder.record(this, sampleRequest, Outcomes.success(sampleResult))
    recorder.record(this, sampleRequest, Outcomes.denied(Err.of("Not authorized")))
    recorder.record(this, sampleRequest, Outcomes.invalid(Err.of("Not a beta user")))
    recorder.record(this, sampleRequest, Outcomes.ignored(Err.of("In active user")))
    recorder.record(this, sampleRequest, Outcomes.errored(Err.of("Unable to determine user type")))
    recorder.record(this, sampleRequest, Outcomes.unexpected(Err.of("Unexpected error while handling analytics")))
     
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}


## Updates {#updates}
Updates track the initial creation time, update time, total updates of some value T. See {{% sk-link-code component="tracking" filepath="tracking/Updates.kt" name="Updates" %}} for more info.
{{< highlight kotlin >}}
      
    // Initial settings
    data class UserSettings(val userId:String, val isBetaTester:Boolean)
    val settings = UserSettings("user1", false)

    // Track as updates
    val updates = Updates.of(settings)
    val update1 = updates.set(settings)
    val update2 = update1.map { it.copy(isBetaTester = true) }

    println(update2.created) // initial creation time
    println(update2.updated) // last update time
    println(update2.applied) // total times changed
    println(update2.current) // current value
     
{{< /highlight >}}
{{% feature-end mod="arch/tracking" %}}

{{% section-end mod="arch/tracking" %}}

