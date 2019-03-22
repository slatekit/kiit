
# AWS-SQS

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Abstraction layer on message queues using Amazon SQS</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.cloud.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.cloud.aws.AwsCloudQueue</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-cloud</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-cloud/src/main/kotlin/slatekit/cloud/aws/AwsCloudQueue" class="url-ch">src/lib/kotlin/slatekit-cloud/src/main/kotlin/slatekit/cloud/aws/AwsCloudQueue</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Aws_Sqs.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Aws_Sqs.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results slatekit-common slatekit-core</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-cloud:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.queues.QueueStringConverter



// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Success
import slatekit.results.Try




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



    val converter = QueueStringConverter()
    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val queue1 = AwsCloudQueue<String>("app1-queue-1", converter)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section "sqs" by default
    val queue2 = AwsCloudQueue<String>("app1-queue-1", converter,"user://myapp/conf/queue.conf")

    // Setup 3: Use the type safe config in "{user_id}/myapp/conf/queue.conf"
    // Reads from the section supplied "sqs-3" ( if you have multiple sqs configurations )
    val queue3 = AwsCloudQueue<String>("app1-queue-1",  converter, "user://myapp/conf/queue.conf", "sqs-1")

    


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


    // Use case 1: init()
    queue2.init()

    // Use case 2: send 1 message
    queue2.send("test 1")

    // Use case 3: send multiple messages
    queue2.send("test 2")

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
    

{{< /highlight >}}
{{% break %}}

