/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.app.LocationUserDir
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd

//</doc:import_examples>


class Example_Folders : Cmd("folders") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
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
                location = LocationUserDir,
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

        //</doc:examples>
        return ok()
    }

}

