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
import kiit.apis.routes.Api

// Slate Kit - Common Utilities
import slatekit.common.*
import slatekit.common.ext.toStringMySql
import slatekit.common.types.*
import slatekit.results.*

// Slate Kit - App ( provides args, help, life-cycle methods, etc )
import slatekit.requests.toResponse
import slatekit.context.Context
import slatekit.core.common.FileUtils

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
        val settings = ServerSettings(port = 5000, prefix = "/api/", docs = true, docKey = "abc123", formatJson = true)

        // 2. APIs ( these are Slate Kit Universal APIs )
        val apis = apis()

        // 3. Authenticator
        val auth: Auth? = null //SampleAuth()

        // 4. API host
        val apiHost = ApiServer.of( ctx, apis, auth = null)

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


    fun apis(): List<Api> {
        return listOf(
                Api(klass = SampleFiles3Api::class, singleton = SampleFiles3Api(), setup = SetupType.Annotated)
        )
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



@kiit.apis.Api(area = "samples", name = "files", desc = "sample api to test other features")
class SampleFiles3Api {

    val sampleCSV = "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234"
    val sampleCSVData = sampleCSV.toByteArray()


    @Action(desc = "test getting content as xml")
    fun getData(): String = DateTime.now().toStringMySql()

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
