# AWS.SQS
| prop | desc  |
|:--|:--|
| **desc** | Abstraction layer on message queues using Amazon S3 | 
| **date**| 2016-3-28 1:12:23 |
| **version** | 0.9.1  |
| **namespace** | slate.cloud.aws  |
| **core source** | slate.cloud.aws.AwsCloudQueue  |
| **example** | [Example_Aws_Sqs](https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_Sqs.scala) |

# Import
```scala 
// required 
import slate.cloud.aws.AwsCloudQueue


// optional 
import slate.common.OperationResult
import slate.core.commands.Command


```

# Setup
```scala


    // Not storing any key/secret in source code for security purposes
    // Using the recommended approach of aws to store a config file
    // in the users directory
    val queue = new AwsCloudQueue("dev1-usa-001-shr")
    

```

# Examples
```scala


    // Use case 1: connect using parameters
    queue.connect(null)

    // Use case 2: send 1 message
    queue.send("scala test 1")

    // Use case 3: send multiple messages
    queue.send("scala test 2")

    // Use case 4: send message with tags
    queue.send("user=kishore", tagName="type", tagValue="reg")

    // Use case 5: receive 1 message
    val item1 = queue.next()
    println(queue.getMessageBody(item1))
    println(queue.getMessageTag(item1, "type"))

    // Use case 6: recieve 2 messages
    val items = queue.nextBatch(2)

    // Use case 7: delete a message
    queue.complete(item1)

    // Use case 8: delete many
    queue.completeAll(items)

    // Use case 9: abandon a message
    queue.abandon(queue.next())

    // Use case 10: get count ( approximation )
    val count = queue.count()
    println(count)
    

```
