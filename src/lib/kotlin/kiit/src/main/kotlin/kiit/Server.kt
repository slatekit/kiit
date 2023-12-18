package kiit

// Ktor
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kiit.apis.*
import kiit.apis.core.Auth
import kiit.apis.core.Meta
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes

// Slate Kit - Common Utilities
import kiit.common.*
import kiit.common.ext.toStringMySql
import kiit.common.types.*
import kiit.results.*

// Slate Kit - App ( provides args, help, life-cycle methods, etc )
import kiit.requests.toResponse
import kiit.context.Context
import kiit.requests.Request

// Slate Kit - Server ( Ktor support )
import kiit.server.ServerSettings
import kiit.server.ktor.KtorHandler
import kiit.server.ktor.KtorResponse
import java.io.File


class Server(val ctx: Context)  {

    /**
     * executes the app
     *
     * @return
     */
    suspend fun execute(): Try<Any> {

        // 1. Settings
        val settings = ServerSettings(port = 5200, prefix = "/api/", docs = true, docKey = "abc123", formatJson = true, versionDefault = "v0")

        // 2. APIs ( these are Slate Kit Universal APIs )
        val apis = apis()

        // 3. Authenticator
        val auth: Auth? = null //SampleAuth()

        // 4. API host
        val routes = routes(apis)
        val apiHost = ApiServer.of( ctx, routes, auth = null)

        // Ktor response handler
        val responder = KtorResponse(settings)

        // Ktor request handler
        val handler = KtorHandler(ctx, settings, apiHost, responses = responder)

        // Ktor
        val server = embeddedServer(Netty, settings.port) {
            routing {

                // Root
                get("/") {
                    ping(call, responder)
                }

                // Your own custom path
                get(settings.prefix + "/ping") {
                    ping(call, responder)
                }

                // Your own multi-path route
                get("module1/feature1/action1") {
                    responder.json(call, Success("action 1 : " + DateTime.now().toString()).toResponse())
                }

                // Remaining outes beginning with /api/ to be handled by Slate Kit API Server
                handler.register(this)
            }
        }

        // CORS
        // server.application.install(CORS)

        // Start server
        server.start(wait = true)
        return Success(true)
    }


    fun apis(): List<GlobalVersion> {
        val apis = listOf(
                GlobalVersion("v0",
                    listOf(
                        api(SampleFiles3Api::class, SampleFiles3Api()),
                        api(SampleVersionApi::class, SampleVersionApi("0")),
                    )
                ),
                GlobalVersion("v1",
                    listOf(
                        api(SampleVersionApi::class, SampleVersionApi("1")),
                    )
                )
            )
        return apis
    }


    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    suspend fun ping(call: ApplicationCall, responses: KtorResponse) {
        val result = "Version ${ctx.info.build.version} : " + DateTime.now()
        responses.json(call, Success(result).toResponse())
    }
}



@Api(area = "samples", name = "versioning", desc = "sample api to test other features")
class SampleVersionApi(val version:String) {

    @Action()
    fun getHi(request: Request): String {
        return "version=${version}"
    }
}


@kiit.apis.Api(area = "samples", name = "files", desc = "sample api to test other features")
class SampleFiles3Api {

    val sampleCSV = "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234"
    val sampleCSVData = sampleCSV.toByteArray()

    @Action()
    fun getRequest(request: Request) : String {
        return "got inputs"
    }

    @Action(desc = "test getting content as xml")
    fun getData(request: Request): String {
        return DateTime.now().toStringMySql()
    }

    @Action(desc = "test getting content as xml")
    fun getContentCsv(): Content = Contents.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting content as xml")
    fun getContentHtml(): Content = Contents.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @Action(desc = "test getting content as xml")
    fun getContentText(): Content = Contents.text("user: kishore")


    @Action(desc = "test getting content as xml")
    fun getContentXml(): Content = Contents.xml("<user><name>kishore</name></user>")


    @Action(desc = "test getting Doc as xml")
    fun getDataCsv(): ContentData = ContentData(sampleCSVData, sampleCSV, ContentTypes.Csv)


    @Action(desc = "test getting Doc as xml")
    fun getDataImg(): ContentData {
        val bytes = File("/Users/kishorereddy/git/blend/blend-server/tests/img/cat1-test.jpeg").readBytes()
        return ContentData(bytes, null, ContentTypes.Jpeg)
    }


    @Action(desc = "test getting Doc as xml")
    fun getDocCsv(): ContentFile = ContentFiles.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @Action(desc = "test getting Doc as xml")
    fun getDocHtml(): ContentFile = ContentFiles.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @Action(desc = "test getting Doc as xml")
    fun getDocText(): ContentFile = ContentFiles.text("file1.txt", "user: kishore")
}
