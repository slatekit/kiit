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
import slatekit.cloud.aws.SQS
import slatekit.core.queues.QueueEntry
import slatekit.core.queues.QueueStringConverter
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions

//</doc:import_required>

//<doc:import_examples>


import slatekit.results.Success
import slatekit.results.Try
import kotlinx.coroutines.runBlocking
import slatekit.results.getOrElse

//</doc:import_examples>


class Example_Cloud_Queues : Command("sqs") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>

        // This converts the queue entry payload to a type
        // NOTES:
        // 1. This can be String converter for any payload ( e.g. JSON )
        // 2. You can create a custom type for type safety
        val converter = QueueStringConverter()

        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "~/.aws/credentials and supply AWS region
        val queue1 = SQS<String>(credentials = ProfileCredentialsProvider().credentials,
                region = Regions.US_EAST_1, name = "slatekit", converter = converter)

        // Setup 2: Allow auto-loading of credentials from ~/.aws/credentials and region by string name supplied
        val queue2 = SQS.of<String>(region = "us-east-1", name = "slatekit", converter = converter)

        // Setup 3: Use the config at "~/myapp/conf/queue.conf"
        // Reads from the section "queues" by default
        /**
         *  SAMPLE CONFIG:
         *  queues = true
         *  queues.key  = AWS_KEY_HERE
         *  queues.pass = AWS_PASSWORD_HERE
         *  queues.env  = dev
         *  queues.tag  = samples
         */
        val queue3 = SQS.of<String>( region = "us-east-1", name = "slatekit", converter = converter,
                confPath = "~/myapp/conf/queue.conf", confSection = "queues")

        val queue = queue3.getOrElse { queue1 }

        //</doc:setup>

        //<doc:examples>
        runBlocking {
            // Use case 1: init()
            queue.init()

            // NOTES:
            // 1. All operations use the slate kit Result<T,E> type
            // 2. All operations return a slate kit Try<T> = Result<T, Exception>
            // Use case 2: send 1 message ( get back message id as String )
            val result1:Try<String> = queue.send("item 1")

            // Use case 3: send multiple messages
            queue.send("item 2")

            // Use case 4: send message with tags
            queue.send("user=kishore", tagName = "type", tagValue = "reg")

            // Use case 5: receive 1 message
            val item1 = queue.next()
            println(item1?.getValue())
            println(item1?.getTag("type"))

            // Use case 6: recieve 2 messages
            val items = queue.next(2)

            // Use case 7: delete a message
            queue.done(item1)

            // Use case 8: delete many
            queue.done(items)

            // Use case 9: abandon a message
            queue.abandon(queue.next())

            // Use case 10: get count ( approximation )
            val count = queue.count()
            println(count)
        }
        //</doc:examples>

        return Success("")
    }
}
