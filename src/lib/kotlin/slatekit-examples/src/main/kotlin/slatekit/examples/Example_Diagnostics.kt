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
import slatekit.app.AppFuncs
import slatekit.core.common.AppContext
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.log.LogsDefault
import slatekit.app.AppRunner
import slatekit.common.Context
import slatekit.common.diagnostics.Diagnostics
import slatekit.common.diagnostics.Events
import slatekit.common.diagnostics.Tracker
import slatekit.common.log.LoggerConsole
import slatekit.common.metrics.MetricsLite
import slatekit.common.requests.Response
import slatekit.common.requests.SimpleResponse
import slatekit.entities.core.Entities
import slatekit.core.cmds.Cmd
import slatekit.core.loader.SampleResult
import slatekit.db.Db
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.results.StatusCodes
import slatekit.results.Try
import slatekit.results.Success
import slatekit.results.getOrElse
import slatekit.workers.WorkRequest

//</doc:import_examples>


class Example_Diagnostics : Cmd("cmd") {

    override fun executeInternal(args: Array<String>?): Try<Any> {
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

        // Logger: Simple console logger, however, you can use the LogBackLogger
        // in the slatekit.providers project.
        // val logs = LogbackLogs()
        // val logger1 = logs.getLogger("mylogger")
        val logger = LoggerConsole()

        // Metrics : In-Memory metrics, however, you can use Data-Dog metrics provider
        // in the slatekit.providers project
        val metrics = MetricsLite.build()

        val diagnostics = Diagnostics<MyJob>(
                // Shows up as a prefix in the logs message
                prefix = "workers.jobs",

                // Shows up as the log prefix ( e.g. "workers.jobs task1" )
                nameFetcher = { myRequest ->  myRequest.action },

                // Shows up in the logs as key/value pairs
                infoFetcher = { "id:${it.id}, name:${it.action}" },

                // Used as the prefix of the metric sent to capture all metrics.
                // workers.apis.( total_requests | total_successes | total_failed )
                metricFetcher = { "workers." + it.source },

                // Used as the tags for associating metrics. e.g. env
                tagsFetcher = { listOf() },

                // The logger that the diagnostics will log to
                logger = logger,

                // The metrics the diagnostics will log to
                metrics = metrics,

                // The events the diagnostics will event out to
                events = Events(),

                // The tracker the diagnostics will use for last request/response/etc
                tracker = Tracker("workers.jobs", "all")
        )

        // Create sample request
        // NOTE: The request can be any data type you want
        val request1 = MyJob("app", "123", "create", "some data")

        // Sample response ( assume you've done processing on your sample request )
        // NOTE: The response has to be an instance of the slatekit.common.requests.Response interface
        val response1:Response<String> = SimpleResponse(true, 1000, mapOf(), "processed")

        // CASE 1: Record all diagnostics  :
        // 1. log   : log the response to the logger
        // 2. track : track the last request so you can access the last one
        // 3. metric: send metrics to the metric component
        // 4. event : send events to the eventing component
        diagnostics.record( sender = this, request = request1, response = response1 )

        // CASE 2: Log only ( the logger initialized with )
        diagnostics.log( sender = this, request = request1, response = response1 )

        // CASE 3: Meter only ( metrics - e.g. Data Dog )
        diagnostics.meter( sender = this, request = request1, response = response1 )

        // CASE 4: Notify only ( events )
        diagnostics.notify( sender = this, request = request1, response = response1 )

        // CASE 5: Track only ( store last request/response )
        diagnostics.track( sender = this, request = request1, response = response1 )

        //</doc:examples>
        return Success("")
    }


    fun showContext(ctx: Context) {
        println("args: " + ctx.arg)
        println("env : " + ctx.env)
        println("conf: " + ctx.cfg)
        println("logs: " + ctx.logs)
        println("app : " + ctx.app)
        println("dirs: " + ctx.dirs)
        println("host: " + ctx.sys.host)
    }

}

