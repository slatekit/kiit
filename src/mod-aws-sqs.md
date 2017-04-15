---
layout: start_page_mods_infra
title: module AWS-SQS
permalink: /mod-aws-sqs
---

# AWS-SQS

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Abstraction layer on message queues using Amazon SQS | 
| **date**| 2017-04-12T22:59:15.765 |
| **version** | 1.4.0  |
| **jar** | slate.cloud.jar  |
| **namespace** | slate.cloud.aws  |
| **source core** | slate.cloud.aws.AwsCloudQueue.scala  |
| **source folder** | [/src/lib/scala/Slate.Cloud/src/main/scala/slate/cloud/aws](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Cloud/src/main/scala/slate/cloud/aws)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_Sqs.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_Sqs.scala) |
| **depends on** |  slate.common.jar slate.core.jar  |

## Import
```scala 
// required 
import slate.cloud.aws.AwsCloudQueue
import slate.common.results.ResultSupportIn



// optional 
import slate.common.{Result}
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val queue1 = new AwsCloudQueue("app1-queue-1")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section "sqs" by default
    val queue2 = new AwsCloudQueue("app1-queue-1", Some("user://myapp/conf/queue.conf"))

    // Setup 3: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section supplied "sqs-3" ( if you have multiple sqs configurations )
    val queue3 = new AwsCloudQueue("app1-queue-1", Some("user://myapp/conf/queue.conf"), Some("sqs-1"))

    

```

## Usage
```scala


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

