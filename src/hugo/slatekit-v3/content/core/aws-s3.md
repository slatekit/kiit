
# AWS-S3

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Abstraction layer on cloud file storage to Amazon S3</td>
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
      <td>slatekit.cloud.aws.AwsCloudFiles</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-cloud</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-cloud/src/main/kotlin/slatekit/cloud/aws/AwsCloudFiles" class="url-ch">src/lib/kotlin/slatekit-cloud/src/main/kotlin/slatekit/cloud/aws/AwsCloudFiles</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Aws_S3.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Aws_S3.kt</a></td>
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
import slatekit.cloud.aws.AwsCloudFiles



// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Success
import slatekit.results.Try




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val files1 = AwsCloudFiles("app1-files-1", false)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files2 = AwsCloudFiles("app1-files-1", false, "user://myapp/conf/files.conf", "s3")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files3 = AwsCloudFiles("app1-queue-1", false, "user://myapp/conf/files.conf", "s3-1")

    


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


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
    

{{< /highlight >}}
{{% break %}}

