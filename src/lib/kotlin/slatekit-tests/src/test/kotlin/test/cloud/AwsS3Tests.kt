package test.cloud

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import slaetkit.providers.aws.aws.S3
import slatekit.common.DateTime
import slatekit.common.io.Uris
import slatekit.common.ext.toStringNumeric
import slatekit.core.files.CloudFiles
import slatekit.results.getOrElse
import test.TestApp
import test.setup.TestSupport
import java.io.File


@Ignore
class AwsS3Tests : TestSupport {
    val SLATEKIT_DIR = ".slatekit"

    @Test
    fun can_test_create(){

        runBlocking {
            // Not storing any key/secret in source code for security purposes
            // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
            val bucket = "slatekit-unit-tests"
            val files = S3.of(app,"us-east-1", bucket, false, "~/$SLATEKIT_DIR/conf/aws.conf", "aws")
            files.onSuccess { files ->
                files.init()

                // Create unique file name "yyyyMMddhhmmss
                val filename = "file-" + DateTime.now().toStringNumeric()

                // 1. Test Create
                val contentCreate = "version 1 : $filename"
                files.create(filename, contentCreate)
                ensureFile(files, filename, contentCreate)

                // 2. Test update
                val contentUpdate = "version 2 : $filename"
                files.update(filename, contentUpdate)
                ensureFile(files, filename, contentUpdate)

                // 3. Test delete
                files.delete(filename)
                val result = files.getAsText(filename)
                Assert.assertTrue(!result.success)
            }
        }
    }


    fun ensureFile(files: CloudFiles, fileName:String, expectedContent:String):Unit {

        runBlocking {
            // Get text
            val result1 = files.getAsText(fileName)
            Assert.assertTrue(result1.success)
            Assert.assertTrue(result1.getOrElse { null } == expectedContent)

            // Download
            val folderPath = Uris.interpret("~/$SLATEKIT_DIR/temp/")
            val downloadResult1 = files.download(fileName, folderPath!!)
            val downloadFilePath1 = downloadResult1.getOrElse { null }
            val file1 = File(downloadFilePath1)
            Assert.assertTrue(file1.exists())
            Assert.assertTrue(file1.readText() == expectedContent)

            // Download as
            val newFileName = fileName + "-01"
            val filePath = Uris.interpret("user://$SLATEKIT_DIR/temp/$newFileName")
            val downloadResult2 = files.downloadToFile(fileName, filePath!!)
            val downloadFilePath2 = downloadResult2.getOrElse { null }
            val file = File(downloadFilePath2)
            Assert.assertTrue(file.exists())
            Assert.assertTrue(file.readText() == expectedContent)
        }
    }
}
