package test.cloud

import org.junit.Test
import slatekit.cloud.aws.AwsCloudFiles
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.DateTime
import slatekit.common.Uris
import slatekit.core.cloud.CloudFilesBase
import slatekit.core.cloud.CloudQueueBase
import java.io.File


class AwsSqsTests {

    val SLATEKIT_DIR = ".slatekit"

    //@Test
    fun can_test_create() {

        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val queue = AwsCloudQueue("slatekit-unit-tests", "user://$SLATEKIT_DIR/conf/aws.conf", "aws")

        queue.init()

        // Create unique file name "yyyyMMddhhmmss
        val timestamp = DateTime.now().toStringNumeric()

        // 1. Test Create
        val contentCreate = "version 1 : $timestamp"
        queue.send(contentCreate)
        ensureQueue(queue, contentCreate)

        // Get text
        val result1 = queue.next()
        val item = queue.toString(result1)
        assert(item != null)
        assert(item == contentCreate)
        queue.complete(result1)
    }


    fun can_test_update() {
        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val queue = AwsCloudQueue("slatekit-unit-tests", "user://$SLATEKIT_DIR/conf/aws.conf", "aws")

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
        val results = queue.nextBatch(2)
        assert(results.size == 2)

        val item1 = queue.toString(results[0])
        assert(item1 != null)
        assert(item1 == contentBatch1)
        queue.complete(results[0])

        val item2 = queue.toString(results[1])
        assert(item2 != null)
        assert(item2 == contentBatch2)
        queue.complete(results[1])
    }


    fun ensureQueue(queue: CloudQueueBase, expectedContent:String):Unit {

        // Get text
        val result1 = queue.next()
        val item = queue.toString(result1)
        assert(item != null)
        assert(item == expectedContent)
        queue.complete(result1)
    }
}