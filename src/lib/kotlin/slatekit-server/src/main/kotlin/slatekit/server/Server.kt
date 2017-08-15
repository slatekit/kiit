/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.server

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.apis.WebProtocol
import slatekit.apis.core.Auth
import slatekit.apis.doc.DocWeb
import slatekit.common.Context
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.app.AppMeta
import slatekit.common.app.AppMetaSupport
import slatekit.common.results.ResultFuncs.success
import slatekit.core.common.AppContext
import slatekit.server.spark.HttpRequest
import slatekit.server.spark.HttpResponse
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.staticFiles
import java.io.File
import javax.servlet.MultipartConfigElement


class Server(
        val config: ServerConfig,
        val ctx   : Context,
        val auth  : Auth?,
        val apis  : List<ApiReg>
) : AppMetaSupport {


    /**
     * initialize with port, prefix for api routes, and all the dependent items
     */
    constructor(
                port      :Int          = 5000,
                prefix    :String       = ""  ,
                info      :Boolean      = true ,
                cors      :Boolean      = false,
                docs      :Boolean      = false,
                static    :Boolean      = false,
                staticDir :String       = ""   ,
                docKey    :String       = ""   ,
                apis      :List<ApiReg>        ,
                auth      :Auth?        = null ,
                ctx       :Context   = AppContext.simple("slatekit-server")
        ) :
        this(ServerConfig(port, prefix, info, cors, docs, docKey, static, staticDir), ctx, auth, apis)


    val container = ApiContainer(ctx, false, auth, WebProtocol, apis, docKey = config.docKey, docBuilder = ::DocWeb)

    override fun appMeta(): AppMeta = ctx.app


    /**
     * executes the application
     * @return
     */
    fun run(): Result<Any> {

        // Configure
        Spark.port(config.port)

        // Display startup
        if (config.info) {
            this.info()
        }

        // Static files
        if(config.static) {
            if(config.staticDir.isNullOrEmpty()){
                staticFiles.location("/public");
            }
            else {
                staticFiles.externalLocation(File(config.staticDir).absolutePath)
            }
        }

        // Ping/Check
        Spark.get(config.prefix + "/ping", { req, res -> ping(req, res) })

        // CORS
        if(config.cors) Spark.options("/*") { req, res  -> cors(req, res) }

        // Before
        Spark.before("*", { req, res ->
            req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement((System.getProperty("java.io.tmpdir"))))
            //req.attribute("org.eclipse.multipartConfig", MultipartConfigElement((System.getProperty("java.io.tmpdir"))))
            if (config.cors) {
                res.header("Access-Control-Allow-Origin", "*")
                res.header("Access-Control-Request-Method", "*")
                res.header("Access-Control-Allow-Headers", "*")
            }
        })

        // Allow all the verbs/routes to hit exec method
        // The exec method will dispatch the request to
        // the corresponding SlateKit API.
        Spark.get(config.prefix    + "/*", { req, res -> exec(req, res) })
        Spark.post(config.prefix   + "/*", { req, res -> exec(req, res) })
        Spark.put(config.prefix    + "/*", { req, res -> exec(req, res) })
        Spark.patch(config.prefix  + "/*", { req, res -> exec(req, res) })
        Spark.delete(config.prefix + "/*", { req, res -> exec(req, res) })

        return success(true)
    }


    /**
     * stops the server ( this is not currently accessible on the command line )
     */
    fun stop(): Unit {
        spark.Spark.stop()
    }


    fun cors(req: Request, res: Response) {
        val accessControlRequestHeaders = req.headers("Access-Control-Request-Headers")
        if (accessControlRequestHeaders != null) {
            res.header("Access-Control-Allow-Headers", accessControlRequestHeaders)
        }

        val accessControlRequestMethod = req.headers("Access-Control-Request-Method")
        if (accessControlRequestMethod != null) {
            res.header("Access-Control-Allow-Methods", accessControlRequestMethod)
        }
    }


    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    fun ping(req: Request, res: Response): String {
        val result = DateTime.now()
        val text = HttpResponse.json(res, success(result))
        return text
    }


    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    fun exec(req: Request, res: Response): Any {
        val request = HttpRequest.build(ctx, req, config)
        val result = container.call(request)
        val text = HttpResponse.result(res, result)
        return text
    }


    /**
     * prints the summary of the arguments
     */
    fun info(): Unit {
        println("===============================================================")
        println("STARTING : ")
        this.appLogStart({ name: String, value: String -> println(name + " = " + value) })
        println("===============================================================")
    }
}