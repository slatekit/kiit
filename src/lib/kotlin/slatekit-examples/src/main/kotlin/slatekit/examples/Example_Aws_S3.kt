package slatekit.examples


//<doc:import_required>
import slatekit.cloud.aws.AwsCloudFiles

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.results.Success
import slatekit.results.Try

//</doc:import_examples>

class Example_Aws_S3  : Cmd("s3") {

  override fun executeInternal(args: Array<String>?) : Try<Any>
  {
    //<doc:setup>
    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val files1 = AwsCloudFiles("app1-files-1", false)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files2 = AwsCloudFiles("app1-files-1", false, "user://myapp/conf/files.conf", "s3")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files3 = AwsCloudFiles("app1-queue-1", false, "user://myapp/conf/files.conf", "s3-1")

    //</doc:setup>

    //<doc:examples>
    // Use case 1: Connect using parameters
    files1.init()

    // Use case 2: create using just name and content
    files1.create("2016_nba_v3", "version 1")

    // Use case 3: update using just name and content
    files1.update("2016_nba_v3", "version 2")

    // Use case 4: create using folder and file name
    files1.create("2016_nba_v3", "chi", "version 1")

    // Use case 5: update using folder and file name
    files1.update("2016_nba_v3", "chi", "version 2")

    // Use case 6: get file as a text using just name
    files1.getAsText("2016_nba_v3")

    // Use case 7: get file using folder and file name
    files1.getAsText("2016_nba_v3", "chi")

    // Use case 8: download file to local folder
    files1.download("2016_nba_v3", "c:/dev/temp/")

    // Use case 9: download using folder and file name to local folder
    files1.download("2016_nba_v3", "chi", "c:/dev/temp")

    // Use case 10: delete file by just the name
    files1.delete("2016_nba_v3")

    // Use case 11: delete using folder and name
    files1.delete("2016_nba_v3", "chi")
    //</doc:examples>

    return Success("")
  }
}
