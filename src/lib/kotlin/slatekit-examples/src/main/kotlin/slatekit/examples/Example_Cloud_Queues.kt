/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples


//<doc:import_required>
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.queues.QueueStringConverter

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Success
import slatekit.results.Try

//</doc:import_examples>


class Example_Cloud_Queues  : Command("sqs") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:setup>
    val converter = QueueStringConverter()
    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val queue1 = AwsCloudQueue<String>("app1-queue-1", "queue1", converter)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section "sqs" by default
    val queue2 = AwsCloudQueue<String>("app1-queue-1", "queue1", converter,"user://myapp/conf/queue.conf")

    // Setup 3: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section supplied "sqs-3" ( if you have multiple sqs configurations )
    val queue3 = AwsCloudQueue<String>("app1-queue-1",  "queue1", converter, "user://myapp/conf/queue.conf", "sqs-1")

    //</doc:setup>

    //<doc:examples>
    // Use case 1: init()
    queue2.init()

    // Use case 2: send 1 message
    queue2.send("item 1")

    // Use case 3: send multiple messages
    queue2.send("item 2")

    // Use case 4: send message with tags
    queue2.send("user=kishore", tagName="type", tagValue="reg")

    // Use case 5: receive 1 message
    val item1 = queue2.next()
    println(item1?.getValue())
    println(item1?.getTag("type"))

    // Use case 6: recieve 2 messages
    val items = queue2.next(2)

    // Use case 7: delete a message
    queue2.complete(item1)

    // Use case 8: delete many
    queue2.completeAll(items)

    // Use case 9: abandon a message
    queue2.abandon(queue2.next())

    // Use case 10: get count ( approximation )
    val count = queue2.count()
    println(count)
    //</doc:examples>

    return Success("")
  }
}
