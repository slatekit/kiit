package slatekit.examples


//<doc:import_required>
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import kotlinx.coroutines.runBlocking
import slatekit.cloud.aws.AwsCloudFiles

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse

//</doc:import_examples>

class Example_Cloud_Files : Command("s3") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        /**
         *  PATHS:
         *  1. /.aws/credentials
         *  2. ~/.slatekit/conf/files.conf
         *
         *  CONTENT:
         *  files = true
         *  files.key  = AWS_KEY_HERE
         *  files.pass = AWS_PASSWORD_HERE
         *  files.env  = dev
         *  files.tag  = samples
         */
        // Not storing any key/secret in source code for security purposes
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val files1 = AwsCloudFiles(credentials = ProfileCredentialsProvider().credentials,
                region = Regions.US_EAST_1, bucket = "slatekit-unit-tests", createBucket = false)

        // Setup 2: Use the default aws config file in "{user_dir}/.aws/credentials"
        val files2 = AwsCloudFiles.of(region = "us-west-2", bucket = "slatekit-unit-tests", createBucket = false)

        // Setup 3: Use the config "{user_id}/myapp/conf/files.conf"
        // Specify the api key section as "files"
        val files3 = AwsCloudFiles.of(region = "us-east-1", bucket = "slatekit-unit-tests", createBucket = false,
                confPath = "~/.slatekit/conf/files.conf", confSection = "files")

        val files = files2.getOrElse { files1 }

        //</doc:setup>

        //<doc:examples>
        runBlocking {
            // Use case 1: Creates bucket if configured
            files.init()

            // Use case 2: create using just name and content
            val result1:Try<String> = files.create("file-1", "content 1")

            // Use case 3: update using just name and content
            files.update("file-1", "content 2")

            // Use case 4: create using folder and file name
            files.create("folder-1", "file-1", "content 1")

            // Use case 5: update using folder and file name
            files.update("folder-1", "file-1", "content 2")

            // Use case 6: get file as a text using just name
            files.getAsText("file-1")

            // Use case 7: get file using folder and file name
            files.getAsText("folder-1", "file-1")

            // Use case 8: download file to local folder
            files.download("file-1", "~/dev/temp/")

            // Use case 9: download using folder and file name to local folder
            files.download("folder-1", "file-1", "~/dev/temp")

            // Use case 10: delete file by just the name
            files.delete("file-1")

            // Use case 11: delete using folder and name
            files.delete("folder-1", "file-1")
        }
        //</doc:examples>

        return Success("")
    }
}
