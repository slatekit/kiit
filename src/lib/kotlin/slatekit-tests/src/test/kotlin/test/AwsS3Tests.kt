package test

import org.junit.Test
import slatekit.cloud.aws.AwsCloudFiles
import slatekit.common.DateTime
import slatekit.common.Uris
import slatekit.core.cloud.CloudFilesBase
import java.io.File


class AwsS3Tests {

    @Test
    fun can_test_create(){

        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val files = AwsCloudFiles("slatekit-unit-tests", false, "user://slatekit-kotlin/conf/aws.conf", "aws")

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
        assert(!result.success)
    }


    fun ensureFile(files: CloudFilesBase, fileName:String, expectedContent:String):Unit {

        // Get text
        val result1 = files.getAsText(fileName)
        assert(result1.success)
        assert(result1.value == expectedContent)

        // Download
        val folderPath = Uris.interpret("user://slatekit-kotlin/temp/")
        val downloadResult1 = files.download(fileName, folderPath!!)
        val downloadFilePath1 = downloadResult1.value!!
        val file1 = File(downloadFilePath1)
        assert(file1.exists())
        assert(file1.readText() == expectedContent)

        // Download as
        val newFileName = fileName + "-01"
        val filePath = Uris.interpret("user://slatekit-kotlin/temp/$newFileName")
        val downloadResult2 = files.downloadToFile(fileName, filePath!!)
        val downloadFilePath2 = downloadResult2.value!!
        val file = File(downloadFilePath2)
        assert(file.exists())
        assert(file.readText() == expectedContent)
    }
}