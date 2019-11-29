package test.cloud

import org.junit.Assert
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.DateTime
import slatekit.common.ext.toStringNumeric
import slatekit.core.queues.QueueStringConverter
import slatekit.core.queues.CloudQueue


class AwsSqsTests {

    val SLATEKIT_DIR = ".slatekit"

    //@Test
    fun can_test_create() {

        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val name = "slatekit-unit-tests"
        val queue = AwsCloudQueue<String>(
                "us-east-1",
                name,
                QueueStringConverter(),
                "user://$SLATEKIT_DIR/conf/aws.conf",
                "aws")

        queue.init()

        // Create unique file name "yyyyMMddhhmmss
        val timestamp = DateTime.now().toStringNumeric()

        // 1. Test Create
        val contentCreate = "version 1 : $timestamp"
        val result = queue.send(contentCreate)
        Assert.assertTrue(result.success)
        ensureQueue(queue, contentCreate, true)

        // Get text
        val result1 = queue.next()
        Assert.assertEquals(result1, null)
//        val item = result1?.getValue()
//        Assert.assertTrue(item != null)
//        Assert.assertTrue(item == contentCreate)
//        queue.complete(result1)
    }


    //@Test
    fun can_test_update() {
        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val name = "slatekit-unit-tests"
        val queue = AwsCloudQueue<String>(
                "us-east-1",
                name,
                QueueStringConverter(),
                "user://$SLATEKIT_DIR/conf/aws.conf",
                "aws")

        queue.init()

        // Create unique file name "yyyyMMddhhmmss
        val timestamp = DateTime.now().toStringNumeric()

        // 2. Test update
        val contentBatch1 = "batch 1 : $timestamp"
        val contentBatch2 = "batch 2 : $timestamp"
        val contentBatch3 = "batch 3 : $timestamp"
        queue.send(contentBatch1)
        queue.send(contentBatch2)
        queue.send(contentBatch3)

        // Get text
        val results = queue.next(2)
        Assert.assertTrue(results.size == 2)

        val item1 = results[0]?.getValue()
        Assert.assertTrue(item1 != null)
        Assert.assertTrue(item1 == contentBatch1)
        queue.complete(results[0])

        val item2 = results[1]?.getValue()
        Assert.assertTrue(item2 != null)
        Assert.assertTrue(item2 == contentBatch2)
        queue.complete(results[1])
    }


    fun ensureQueue(queue: CloudQueue<String>, expectedContent:String, complete:Boolean) {

        // Get text
        val result1 = queue.next()
        val item = result1?.getValue()
        Assert.assertTrue(item != null)
        //Assert.assertTrue(item == expectedContent)
        if(complete) {
            queue.complete(result1)
        }
    }
}