package test.cloud

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import slatekit.providers.aws.S3
import slatekit.common.DateTime
import slatekit.common.io.Uris
import slatekit.common.ext.toStringNumeric
import slatekit.common.ids.ULIDs
import slatekit.core.common.FileUtils
import slatekit.core.files.CloudFileEntry
import slatekit.core.files.CloudFiles
import slatekit.http.HttpRPC
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
            val files = S3.of(app,"us-west-2", bucket, false, "~/$SLATEKIT_DIR/common/conf/aws.conf", "aws")
            files.onSuccess { files ->
                files.init()

                // Create unique file name "yyyyMMddhhmmss
                val filename = "file-2021-11-18-22-11-45" //"file-" + DateTime.now().toStringNumeric()

                // 1. Test Create
                val contentCreate = "version 1 : $filename"
                val createResult = files.create(filename, contentCreate)

                // 2. Test Create With Atts
                val fileName2 = "17d362e56eejjpamsr86swnuzyjvva.txt" //"${ULIDs.create()}.txt"
                val contentCreate2 = "version 2 : $filename"
                val createResult2 = files.create(
                        CloudFileEntry(
                                null,
                                fileName2,
                                contentCreate2.toByteArray(),
                                mapOf("name" to  fileName2, "type" to "data")
                        )
                )

                // 3. Test binary
                val binaryName = "bin-" + DateTime.now().toStringNumeric()
                val binaryContent = "version 1 : $filename"
                val binaryBytes = binaryContent.toByteArray()
                val updateResult = files.create(binaryName, binaryBytes)

                // 4. Get
                val actualFileResult = files.getFile(binaryName)
                val actualTextResult = files.getFileText(binaryName)
                val actualByteResult = files.getFileBytes(binaryName)
                Assert.assertTrue(actualFileResult.success)
                actualFileResult.onSuccess {
                    Assert.assertEquals(contentCreate, it.text)
                }
                Assert.assertTrue(actualTextResult.success)
                actualTextResult.onSuccess {
                    Assert.assertEquals(contentCreate, it)
                }
                Assert.assertTrue(actualByteResult.success)
                actualByteResult.onSuccess {
                    val content = String(it)
                    Assert.assertEquals(contentCreate, content)
                }

                // 6. SignUrl
                val http = HttpRPC()
                val url = files.buildSignedGetUrl(null, filename, 180)
                val httpResult = http.get(url)
                val content = httpResult.getOrNull()?.body()?.string() ?: ""
                Assert.assertEquals(contentCreate, content)

                // 5. Test update
                val contentUpdate = "version 2 : $filename"
                files.update(filename, contentUpdate)
                ensureFile(files, filename, contentUpdate)

                // 3. Test delete
                files.delete(filename)
                val result = files.getFileText(filename)
                Assert.assertTrue(!result.success)
            }
        }
    }


    fun ensureFile(files: CloudFiles, fileName:String, expectedContent:String):Unit {

        runBlocking {
            // Get text
            val result1 = files.getFileText(fileName)
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
            val filePath = Uris.interpret("usr://$SLATEKIT_DIR/temp/$newFileName")
            val downloadResult2 = files.downloadToFile(fileName, filePath!!)
            val downloadFilePath2 = downloadResult2.getOrElse { null }
            val file = File(downloadFilePath2)
            Assert.assertTrue(file.exists())
            Assert.assertTrue(file.readText() == expectedContent)
        }
    }
}
