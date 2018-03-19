---
layout: start_page_mods_infra
title: module AWS-SQS
permalink: /kotlin-mod-aws-sqs
---

# AWS-SQS

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Abstraction layer on message queues using Amazon SQS | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.cloud.jar  |
| **namespace** | slatekit.cloud.aws  |
| **source core** | slatekit.cloud.aws.AwsCloudQueue.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Aws_Sqs.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Aws_Sqs.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar slatekit.core.jar  |

## Import
```kotlin 
// required 
import slatekit.cloud.aws.AwsCloudQueue



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin


    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val queue1 = AwsCloudQueue("app1-queue-1")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section "sqs" by default
    val queue2 = AwsCloudQueue("app1-queue-1", "user://myapp/conf/queue.conf")

    // Setup 3: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section supplied "sqs-3" ( if you have multiple sqs configurations )
    val queue3 = AwsCloudQueue("app1-queue-1", "user://myapp/conf/queue.conf", "sqs-1")

    

```

## Usage
```kotlin


    // Use case 1: init()
    queue2.init()

    // Use case 2: send 1 message
    queue2.send("scala test 1")

    // Use case 3: send multiple messages
    queue2.send("scala test 2")

    // Use case 4: send message with tags
    queue2.send("user=kishore", tagName="type", tagValue="reg")

    // Use case 5: receive 1 message
    val item1 = queue2.next()
    println(queue2.getMessageBody(item1))
    println(queue2.getMessageTag(item1, "type"))

    // Use case 6: recieve 2 messages
    val items = queue2.nextBatch(2)

    // Use case 7: delete a message
    queue2.complete(item1)

    // Use case 8: delete many
    queue2.completeAll(items)

    // Use case 9: abandon a message
    queue2.abandon(queue2.next())

    // Use case 10: get count ( approximation )
    val count = queue2.count()
    println(count)
    

```

