package slatekit.examples


//<doc:import_required>
import slatekit.cloud.aws.S3
import slatekit.core.files.CloudFiles
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse
import kotlinx.coroutines.runBlocking

//</doc:import_examples>

class Example_Cloud_Files : Command("s3") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
        val files1 = S3(credentials = ProfileCredentialsProvider().credentials,
                region = Regions.US_EAST_1, bucket = "slatekit-unit-tests", createBucket = false)

        // Setup 2: Use the default aws config file in "{user_dir}/.aws/credentials"
        val files2 = S3.of(region = "us-west-2", bucket = "slatekit-unit-tests", createBucket = false)

        // Setup 3: Use the config "{user_id}/myapp/conf/files.conf"
        // Specify the api key section as "files"
        /**
         *  SAMPLE CONFIG:
         *  files = true
         *  files.key  = AWS_KEY_HERE
         *  files.pass = AWS_PASSWORD_HERE
         *  files.env  = dev
         *  files.tag  = samples
         */
        val files3 = S3.of(region = "us-west-2", bucket = "slatekit-unit-tests", createBucket = false,
                confPath = "~/.slatekit/conf/files.conf", confSection = "files")

        val files:CloudFiles = files2.getOrElse { files1 }

        //</doc:setup>

        //<doc:examples>
        runBlocking {
            // Use case 1: Creates bucket if configured
            files.init()

            // NOTES:
            // 1. All operations use the slate kit Result<T,E> type
            // 2. All operations return a slate kit Try<T> = Result<T, Exception>
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
