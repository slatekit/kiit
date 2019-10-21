/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>
import slatekit.common.info.Folders
//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.results.Success
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest

//</doc:import_examples>


class Example_Folders : Command("folders") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>

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
                area = "department-1",
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
                area = "department-1",
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

        //</doc:examples>
        return Success("")
    }

}

