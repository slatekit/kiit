
# Folders

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Standardized application folder setup; includes conf, cache, inputs, logs, outputs</td>
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
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.info</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/info" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/info</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Folders.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Folders.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results</td>
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
        compile 'com.slatekit:slatekit-common:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.info.Folders


// optional 
import slatekit.results.Try
import slatekit.results.Success
import slatekit.core.cmds.Cmd




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}



        // CASE 1: Build a folder structure for an application
        // Explicitly indicate user-directory with the following folder structure
        // NOTE: Slate Kit based applications are setup this way.
        //
        // /c/users/{user}/
        //  - company-1
        //    - department-1
        //      - app-1
        //        - conf
        //        - cache
        //        - inputs
        //        - logs
        //        - outputs
        //        - temp
        val folders = Folders(
                home = System.getProperty("user.dir"),
                root = "company-1",
                group = "department-1",
                app = "app-1",
                cache = "cache",
                inputs = "input",
                logs = "logs",
                outputs = "output",
                temp = "temp",
                conf = "conf"
        )

        // CASE 2: Build a folder structure for an application
        // Same as Case 1 above but using a short-hand approach
        val folders2 = Folders.userDir(
                root = "company-1",
                group = "department-1",
                app = "app-1"
        )

        // CASE 3: Create the folders if they do not exist
        folders.create()

        // CASE 4: Get the individual paths to the folders
        println("app    :" + folders.pathToApp)
        println("cache  :" + folders.pathToCache)
        println("conf   :" + folders.pathToConf)
        println("input  :" + folders.pathToInputs)
        println("logs   :" + folders.pathToLogs)
        println("output :" + folders.pathToOutputs)
        println("output :" + folders.pathToTemp)

        

{{< /highlight >}}
{{% break %}}

