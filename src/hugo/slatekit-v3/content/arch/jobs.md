---
title: "Jobs"
date: 2019-11-17T23:55:42-05:00
section_header: Jobs
---

# Overview
The Slate Jobs component is a background job/persistant task queue system for Kotlin and is similar to Ruby SideKiq, NodeJS Bull. It leverages Kotlin **Coroutines and Channels** for concurrency based operations.
{{% break %}}

# Create
{{< highlight bash >}}
    
    slatekit new job -name="SampleJob" -package="mycompany.apps"
    
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
        <td><strong>1. Job types</strong></td>
        <td>Different types of jobs OneTime, Paged, Queued job types.</td>
    </tr>
    <tr>
        <td><strong>2. Diagnostics</strong> </td>
        <td>Multiple types of diagnostics and tracking of jobs statistics available</td>                     
    </tr>
    <tr>
        <td><strong>3. Policies</strong></td>
        <td>Policies as middleware and hooks to restrict, customize, or enhance jobs</td>
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
        <td><strong><a class="url-ch" href="arch/jobs#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/jobs#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>

{{% section-end mod="arch/jobs" %}}

# Status
This component is currently stable and there is a project generator for it
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td>**Periodic**</td>
        <td>Upcoming</td>
        <td>Support for periodic and scheduled jobs</td>
    </tr>
    <tr>
        <td>**Flow**</td>
        <td>Upcoming</td>
        <td>Integration with Kotlin Flows</td>
    </tr>
</table>
{{% section-end mod="arch/jobs" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-jobs:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="Jobs"
    package="slatekit.jobs"
    jar="slatekit.jobs.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-jobs"
    gitAlias="slatekit/src/lib/kotlin/slatekit-jobs"
    url="arch/jobs"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_Jobs.kt"
%}}
{{% section-end mod="arch/jobs" %}}

# Requires
This component uses the following other <strong>Slate Kit</strong> and/or third-party components.
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
    <tr>
        <td><a class="url-ch" href="arch/queues">Queues</a></td>
        <td>Persistent queues with AWS SQS as the default</td>
    </tr>
</table>

{{% section-end mod="arch/jobs" %}}

# Sample
This is a simple example of a Queued worker ( which takes it work from a Task which a unit-of-work stored in-memory or in persistent queue ).
{{< highlight kotlin >}}
     
    suspend fun sendNewsLetterFromQueue(task: Task):WorkResult {

        // Get data out of the task
        val userId = task.data.toInt()

        // Simulate getting the user and sending the newsletter
        val user = getUser(userId)
        send(task.job, "Latest News Letter!", user)

        // Acknowledge the task or abandon via task.fail()
        task.done()

        // Indicate that this can now handle more
        return WorkResult(WorkState.More)
    }

{{< /highlight >}}

{{% section-end mod="arch/jobs" %}}


# Concepts
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Jobs.kt" name="Jobs" %}}</strong></td>
        <td>A registry of all the jobs being managed.</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Job.kt" name="Job" %}}</strong></td>
        <td>The main component managing work between workers, queues, tasks, and policies.</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/support/Coordinator.kt" name="Coordinator" %}}</strong></td>
        <td>Responsible for coordinating requests on Workers ( by default using Channels )</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Worker.kt" name="Worker" %}}</strong> </td>
        <td>Performs the actual work (on a possible Task)</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/WorkState.kt" name="WorkState" %}}</strong></td>
        <td>Represents the state of a single iteration of work done by a worker</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Task.kt" name="Task" %}}</strong></td>
        <td>A single work item that can be performed by a worker</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Queue.kt" name="Queue" %}}</strong></td>
        <td>Represents a queue of Tasks either in-memory or persisted ( AWS SQS )</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/JobAction.kt" name="Action" %}}</strong></td>
        <td>The Action(s) that can be performed on a Job/Worker: Start, Stop, Pause, Resume, Control, Process, Delay
        </td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="jobs" filepath="jobs/Priority.kt" name="Priority" %}}</strong></td>
        <td>The priority level of a Queue</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="functions" filepath="functions/policy/Policy.kt" name="Policy" %}}</strong></td>
        <td>A form of middleware to inject into a Job to adjust behaviour</td>
    </tr>
    <tr>
        <td><strong>{{% sk-link-code component="common" filepath="common/Status.kt" name="Status" %}}</strong></td>
        <td>The status ( idle, running, paused, stopped, completed ) of a worker or job.</td>
    </tr>
</table>

{{% section-end mod="arch/jobs" %}}

# Guide

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Setup</strong></td>
        <td>How to setup a Worker</td>
        <td><a href="arch/jobs/#setup" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Register</strong></td>
        <td>How to set up a job and register it with Jobs system</td>
        <td><a href="arch/jobs/#register" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>3. Cycle</strong></td>
        <td>The life-cycle of a worker</td>
        <td><a href="arch/jobs/#cycle" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Types</strong> </td>
        <td>OneTime, Paged, Queued Job types</td> 
        <td><a href="arch/jobs/#types" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>5. Actions</strong> </td>
        <td>Starting, Stopping, Pausing, Resuming jobs</td> 
        <td><a href="arch/jobs/#types" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>6. Stats</strong> </td>
        <td>Capturing job and worker statistics</td> 
        <td><a href="arch/jobs/#types" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
</table>

{{% section-end mod="arch/jobs" %}}

## Identity {#identity}
Every job must be set up with an {{% sk-link-code component="common" filepath="common/Identity.kt" name="Identity" %}}. This identity is transfered down to all workers ( given a unique uuid per worker ) and is used to uniquely identity jobs and workers.

{{< highlight kotlin >}}
      
    // slatekit.common.Identity is used to identify the job.  
    val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")

{{< /highlight >}}
{{% break %}}

## Setup {#setup}
You can set up workers in various ways using either simple functions ( which get wrapped inside a Worker) or extending from the Worker class itself and implementing the **work** method. For full example, refer to {{% sk-link-example file="Example_Jobs.kt" name="Example_Jobs.kt" %}}
{{% break %}}

### 1. Function
You can declare a simple 0 parameter function to perform some work
{{< highlight kotlin >}}
     
    suspend fun sendNewsLetter():WorkResult {
        // Perform the work ...
        return WorkResult(WorkState.Done)
    }

{{< /highlight >}}
{{% break %}}

### 2. Function with Task
You can declare a simple 1 parameter that takes in a {{% sk-link-code component="jobs" filepath="jobs/Task.kt" name="Task" %}} ( representing a unit of work ). This assumes you are sourcing the work from a Queue.
{{< highlight kotlin >}}
     
    suspend fun sendNewsLetter(task: Task):WorkResult {
        println(task.id)    // abc123 
        println(task.from)  // queue://notification
        println(task.job)   // job1
        println(task.name)  // users.sendNewsletter
        println(task.data)  // { ... } json payload
        println(task.xid)   // 12345   correlation id
        // Perform the work ...
        return WorkResult(WorkState.Done)
    }

{{< /highlight >}}
{{% break %}}

### 3. Worker class
Finally, you can extend from the {{% sk-link-code component="jobs" filepath="jobs/Worker.kt" name="Worker" %}} class. This is the most comprehensive option as it gives you the ability to customize various life-cycel events ( see later section ). The life-cycle events go from **init -> work -> done**.

{{< highlight kotlin >}}
      
    // slatekit.common.Identity is used to identify the job.  
    val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")

    // Worker
    class NewsLetterWorker : Worker<String>(id) {

        // Initialization hook ( for setup / logs / alerts )
        override suspend fun init() {
            notify("initializing", listOf(("id" to this.id.name)))
        }

        // Implement your work here.
        // NOTE: If you are not using a queue, this task will be empty e.g. Task.empty
        override suspend fun work(task:Task): WorkResult {
            // Perform the work ...
            return WorkResult(WorkState.Done)
        }

        // Transition hook for when the status is changed ( e.g. from Status.Running -> Status.Paused )
        override suspend fun move(state: Status) {
            notify("move", listOf("status" to state.name))
        }

        // Completion hook ( for logic / logs / alerts )
        override suspend fun done() {
            notify("done", listOf(("id" to this.id.name)))
        }

        // Failure hook ( for logic / logs / alerts )
        override suspend fun fail(err:Throwable?) {
            notify("failure", listOf(("id" to this.id.name), ("err" to (err?.message ?: ""))))
        }

        // Initialization hook ( for setup / logs / alerts )
        override fun notify(desc: String?, extra: List<Pair<String, String>>?) {
            val detail = extra?.joinToString(",") { it.first + "=" + it.second }
            // Simulate notification to email/alerts/etc
            println(desc + detail)
        }
    }

{{< /highlight >}}

{{% feature-end mod="arch/jobs" %}}


## Register
Once you have a work function, you can register it as a new Job.

{{< highlight kotlin >}}
     

    // Identity
    val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")
        
    // Queues
    val queue1 = Queue("queue1", Priority.Mid, QueueSourceInMemory.stringQueue(5))
    val queue2 = Queue("queue2", Priority.Mid, QueueSourceInMemory.stringQueue(5))

    // Registry
    // NOTE: Create an identity for each job using the id template above.
    val jobs = Jobs(
            listOf(queue1, queue2),
            listOf(
                    slatekit.jobs.Job(id.copy(service = "job1"), ::sendNewsLetter),
                    slatekit.jobs.Job(id.copy(service = "job2"), listOf(::sendNewsLetter, ::sendNewsLetterWithPaging)),
                    slatekit.jobs.Job(id.copy(service = "job3"), listOf(::sendNewsLetterWithPaging)),
                    slatekit.jobs.Job(id.copy(service = "job4"), listOf(::sendNewsLetterWithPaging)),
                    slatekit.jobs.Job(id.copy(service = "job5"), listOf(::sendNewsLetterFromQueue), queue1),
                    slatekit.jobs.Job(id.copy(service = "job6"), listOf(NewsLetterWorker()), queue2)
            )
    )

    // Run job named "samples.job1"
    jobs.run("samples.job1")

{{< /highlight >}}

{{% feature-end mod="arch/jobs" %}}

## Cycle {#cycle}
Workers have several life-cycle event methods. They are called in order and the status of the worker changes after the event.

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Purpose</strong></td>
    </tr>
    <tr>
        <td><strong>init</strong></td>
        <td>Starting</td>
        <td>initialization hook for the worker</td>
    </tr>
    <tr>
        <td><strong>work</strong></td>
        <td>Running</td>
        <td>initialization hook for the worker</td>
    </tr>
    <tr>
        <td><strong>fail</strong></td>
        <td>Failed</td>
        <td>Called in the event of an error/exception/failure</td>
    </tr>
    <tr>
        <td><strong>done</strong></td>
        <td>Complete</td>
        <td>Called when the work method has issues Work.Done</td>
    </tr>
</table>

{{% feature-end mod="arch/jobs" %}}

## Types {#types}
There are different types of jobs based on whether they run all their work in 1 go or perform work in batches/pages. This is **(currently)** not indicated explicitly at the function/worker level, but is based on what the function/work methods returns for the {{% sk-link-code component="jobs" filepath="jobs/WorkState.kt" name="WorkState" %}}. 
{{% break %}}

### 1. OneTime
A one time worker simply performs the long-running operation and returns a WorkResult indicating it's done.
{{< highlight kotlin >}}
     
    suspend fun sendNewsLetter():WorkResult {
        // Process the work
        // ...
        return WorkResult(WorkState.Done)
    }

{{< /highlight >}}
{{% break %}}

### 2. Paged
A paged worker runs performs work in batches or pages. After performing a batch of work, this paged worker must return a WorkState.Next.

{{< highlight kotlin >}}
        
    // This keeps track of the page/offset
    val offset = AtomicInteger(0)
    val batchSize = 4
    suspend fun sendNewsLetterWithPaging():WorkResult {
        // Process the work
        // ...

        // Increment the batch/page offset for next time
        offset.addAndGet(batchSize)

        // Indicate to the system this is paged and there is more to do.
        return WorkResult.next(
            offset = offset.get() + batchSize.toLong(), 
            processed = batchSize
            reference = "newsletters"
        )
    }
     
{{< /highlight >}}
{{% break %}}

### 3. Queued
A queued worker runs work via {{% sk-link-code component="jobs" filepath="jobs/Task.kt" name="Tasks" %}} from a Queue. 
{{< highlight kotlin >}}
        
    suspend fun sendNewsLetterFromQueue(task: Task):WorkResult {

        // Get data out of the task
        val userId = task.data.toInt()

        // Simulate getting the user and sending the newsletter
        val user = getUser(userId)
        send(task.job, "Latest News Letter!", user)

        // Acknowledge the task or abandon via task.fail()
        task.done()

        // Indicate that this can now handle more
        return WorkResult(WorkState.More)
    }
     
{{< /highlight >}}

{{% feature-end mod="arch/jobs" %}}

## Actions {#actions}
Jobs that are **paged** or **queued** can be **gracefully** started, stopped, paused, resumed. This is accomplished by sending Job Requests to the Job. These requests impact the status of the job and all its workers. These are methods available on the **Job** component itself. 
{{% sk-tip-generic text="You can also issues individual actions/requests on 1 worker in the job by building up a JobRequest" %}}
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Purpose</strong></td>
    </tr>
    <tr>
        <td><strong>start</strong></td>
        <td>Starting</td>
        <td>Starts the job and its associated workers</td>
    </tr>
    <tr>
        <td><strong>stop</strong></td>
        <td>Stopped</td>
        <td>Stops the job and its associated workers</td>
    </tr>
    <tr>
        <td><strong>pause</strong></td>
        <td>Paused</td>
        <td>Pauses the job and its associated workers</td>
    </tr>
    <tr>
        <td><strong>resume</strong></td>
        <td>Running</td>
        <td>Resumes the jobs and its associated workers</td>
    </tr>
    <tr>
        <td><strong>process</strong></td>
        <td>Running</td>
        <td>Issues a single process / work request </td>
    </tr>
</table>

{{% feature-end mod="arch/jobs" %}}

## Policies
You can set up custom policies which essentially represent middleware functions to execute at various times and/or conditions of workers and jobs.
{{< highlight kotlin >}}
    
    // There are 3 policies setup for this job:
    // 1. Every: for every 10 items processed, call the function supplied
    // 2. Limit: limit number of items to process to 12. ( useful for "waves")
    // 3. Ratio: Ensure the job stops if the error (failed) job ratio hits 10%
    val job = slatekit.jobs.Job(id.copy(service = "job7"), listOf(::sendNewsLetterWithPaging), 
        policies = listOf(
            Every(10, { req, res -> println("Paged : " + req.task.id + ":" + res.msg) }),
            Limit(12, true, { req -> req.context.stats.counts }),
            Ratio(.1, slatekit.results.Status.Errored(0, ""), { req -> req.context.stats.counts })
        )
    )
     
{{< /highlight >}}

{{% feature-end mod="arch/jobs" %}}

## Stats
{{% sk-tip-generic text="Functionality is available but docs are not ready" %}}

{{% section-end mod="arch/jobs" %}}

