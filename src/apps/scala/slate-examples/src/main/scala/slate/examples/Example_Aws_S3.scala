package slate.examples.common


//<doc:import_required>
import slate.cloud.aws.AwsCloudFiles
import slate.common.results.ResultSupportIn

//</doc:import_required>

//<doc:import_examples>
import slate.common.{Result}
import slate.core.cmds.Cmd
//</doc:import_examples>

class Example_Aws_S3 extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:setup>
    // Not storing any key/secret in source code for security purposes
    // Using the recommended approach of aws to store a config file
    // in the users directory
    val files = new AwsCloudFiles("blendlife-dev1", false)
    //</doc:setup>

    //<doc:examples>
    // Use case 1: Connect using parameters
    files.connect(null)

    // Use case 2: create using just name and content
    files.create("2016_nba_v3", "version 1")

    // Use case 3: update using just name and content
    files.update("2016_nba_v3", "version 2")

    // Use case 4: create using folder and file name
    files.create("2016_nba_v3", "chi", "version 1")

    // Use case 5: update using folder and file name
    files.update("2016_nba_v3", "chi", "version 2")

    // Use case 6: get file as a text using just name
    files.getAsText("2016_nba_v3")

    // Use case 7: get file using folder and file name
    files.getAsText("2016_nba_v3", "chi")

    // Use case 8: download file to local folder
    files.download("2016_nba_v3", "c:/dev/temp/")

    // Use case 9: download using folder and file name to local folder
    files.download("2016_nba_v3", "chi", "c:/dev/temp")

    // Use case 10: delete file by just the name
    files.delete("2016_nba_v3")

    // Use case 11: delete using folder and name
    files.delete("2016_nba_v3", "chi")
    //</doc:examples>

    ok()
  }
}