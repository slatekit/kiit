/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slate.examples


//<doc:import_required>
import slate.cloud.aws.AwsCloudQueue
import slate.common.results.ResultSupportIn

//</doc:import_required>

//<doc:import_examples>
import slate.common.{Result}
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Aws_Sqs extends Cmd("types")  with ResultSupportIn{

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:setup>
    // Not storing any key/secret in source code for security purposes
    // Using the recommended approach of aws to store a config file
    // in the users directory
    val queue = new AwsCloudQueue("dev1-usa-001-shr")
    //</doc:setup>

    //<doc:examples>
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
    //</doc:examples>

    ok()
  }
}
