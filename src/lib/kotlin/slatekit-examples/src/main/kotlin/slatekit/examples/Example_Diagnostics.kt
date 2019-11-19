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
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Context
import slatekit.common.log.LoggerConsole
import slatekit.tracking.MetricsLite
import slatekit.common.requests.Response
import slatekit.common.CommonResponse
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.Identity
import slatekit.tracking.Recorder
import slatekit.results.Try
import slatekit.results.Success
import slatekit.results.builders.Outcomes

//</doc:import_examples>


class Example_Diagnostics : Command("cmd") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>

        // OVERVIEW:
        // Diagnostics
        //
        // INCLUDED:
        // 1.  logger : parsed command line arguments
        // 2.  metrics: the selected environment ( dev, qa, uat, prod )
        // 3.  tracker: the config settings
        // 4.  events : the global logger
        data class MyJob(val source:String, val id:String, val action:String, val data:String)
        val id = Identity.test("sample")

        // Logger: Simple console logger, however, you can use the LogBackLogger
        // in the slatekit.providers project.
        // val logs = LogbackLogs()
        // val logger1 = logs.getLogger("mylogger")
        val logger = LoggerConsole()

        // Metrics : In-Memory metrics, however, you can use Data-Dog metrics provider
        // in the slatekit.providers project
        val metrics = MetricsLite.build(id)

        val recorder = Recorder.of<MyJob, Response<String>>(id, logger = logger)

        // Create sample request
        // NOTE: The request can be any data type you want
        val request1 = MyJob("app", "123", "create", "some data")

        // Sample response ( assume you've done processing on your sample request )
        // NOTE: The response has to be an instance of the slatekit.common.requests.Response interface
        val result1:Response<String> = CommonResponse(true, 1000, mapOf(), "processed")

        // CASE 1: Record all diagnostics  :
        // 1. log   : log the response to the logger
        // 2. track : track the last request so you can access the last one
        // 3. metric: send metrics to the metric component
        // 4. event : send events to the eventing component
        recorder.record( sender = this, request = request1, result = Outcomes.of(result1) )

        // CASE 2: Log only ( the logger initialized with )
        recorder.log( sender = this, request = request1, result = Outcomes.of(result1) )

        // CASE 3: Count only ( metrics - e.g. Data Dog )
        recorder.count( sender = this, request = request1, result = Outcomes.of(result1) )

        // CASE 4: Notify only ( events )
        recorder.event( sender = this, request = request1, result = Outcomes.of(result1) )

        // CASE 5: Track only ( store last request/response )
        recorder.last( sender = this, request = request1, result = Outcomes.of(result1) )

        //</doc:examples>
        return Success("")
    }


    fun showContext(ctx: Context) {
        println("args: " + ctx.args)
        println("env : " + ctx.envs)
        println("conf: " + ctx.conf)
        println("logs: " + ctx.logs)
        println("app : " + ctx.about)
        println("dirs: " + ctx.dirs)
        println("host: " + ctx.sys.host)
    }

}

