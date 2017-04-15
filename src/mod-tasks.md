---
layout: start_page_mods_infra
title: module Tasks
permalink: /mod-tasks
---

# Tasks

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A robust Task/Job implementation that can be hooked up with a Queue | 
| **date**| 2017-04-12T22:59:15.737 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.tasks  |
| **source core** | slate.core.tasks.Task.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/tasks](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/tasks)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Task.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Task.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 

import slate.core.tasks.{TaskSettings, TaskQueue, Task}
import slate.ext.tasks.TaskRunner



// optional 
import slate.common.app.AppMeta
import slate.common.results.ResultSupportIn
import slate.common.Result
import slate.common.info._
import slate.common.queues.QueueSourceDefault
import slate.core.cmds.Cmd


```

## Setup
```scala


  // Setup 1: This is a simple task that extends from task
  // It has built in support for
  // 1. life-cycle events for init, exec, end.
  // 2. status updates
  // 3. support for setting/getting app info
  class SampleTask1 extends Task("sampletask1", new TaskSettings(),
    new AppMeta(new About(
      id = "slatekit.task",
      name = "Sample Slate Task",
      desc = "Sample to show creating a simple task",
      company = "slatekit",
      region = "ny",
      version = "0.9.1",
      url = "http://www.slatekit.com",
      group = "codehelix.co",
      contact = "kishore@codehelix.co",
      tags = "slate,shell,cli",
      examples = ""
    ), Host.local(), Lang.asScala(), Status.none, StartInfo.none
  ))
  {


    override protected def onInit(args:Option[Any]): Result[Boolean] = {
      ok()
    }


    override protected def onExec():Result[Any] =
    {
      Thread.sleep(2000)
      success(true)
    }


    override protected def onEnd() : Unit =
    {
    }
  }


  // Setup 2: Create a new task that is a long running background task
  // that processes messages in a queue.
  // NOTE: The queue can be any type of queue ( AWS-SQS, default queue )
  val task2 = new TaskQueue("sample task", new TaskSettings(), AppMeta.none, None, new QueueSourceDefault())


  // Send some messages to the in-memory default queue.
  for(i <- 1 to 10 ){
    task2.queue.send(i)
  }
  

```

## Usage
```scala


    // CASE 1: Start in background thread
    TaskRunner.run(task2, null)

    // CASE 2: Pause the task
    task2.pause()
    task2.isPaused()

    // CASE 3: Resume the task
    task2.resume()
    //task2.isResumed()
    //task2.isStartedOrResumed()

    // CASE 4: Stop the task and ends the background thread
    task2.stop()
    task2.isStopped()

    // CASE 5: Starts the task again ( via background thread )
    TaskRunner.run(task2, null)
    task2.isStarted()

    // CASE 6: Check if task is waiting for messages on queue
    task2.isWaiting()
    

```

