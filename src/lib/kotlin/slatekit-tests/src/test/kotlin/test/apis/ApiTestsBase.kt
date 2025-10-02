/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import kiit.apis.*
import kiit.apis.core.Auth
import kiit.apis.executor.MetaHandler
import kiit.apis.routes.Areas
import kiit.apis.setup.ApiSetup
import kiit.apis.setup.routes
import kiit.requests.CommonRequest
import kiit.common.Source
import kiit.common.args.Args
import kiit.common.conf.Config
import kiit.common.crypto.Encryptor
import kiit.common.data.DbConString
import kiit.common.data.Connections
import kiit.common.data.Connections.Companion.of
import kiit.common.data.DbCon
import kiit.common.envs.Envs
import kiit.common.info.*
import kiit.common.log.LogsDefault
import kiit.requests.Request
import kiit.requests.Response
import kiit.db.Db
import kiit.entities.Entities
import kiit.policy.hooks.Middleware
import kiit.connectors.entities.AppEntContext
import kiit.results.Try
import kiit.serialization.deserializer.Deserializer
import org.json.simple.JSONObject
import test.TestApp
import test.setup.MyAuthProvider
import test.setup.MyEncryptor
import kotlin.reflect.KClass

/**
 * Created by kishorereddy on 6/12/17.
 */

open class ApiTestsBase {


    val ctx: AppEntContext = buildCtx()


    fun buildCtx(): AppEntContext {
        val cls = TestApp::class.java
        val cfg = Config(cls)
        val ctx = AppEntContext(
                app = cls,
                args = Args.empty(),
                envs = Envs.defaults().select("loc"),
                conf = cfg,
                logs = LogsDefault,
                ent = Entities({ con -> Db(con) }, Connections.of(DbCon.empty)),
                info = Info.of(
                        About("tests", "myapp", "sample app", "slatekit", "ny", "", "", "")
                ),
                dbs = of(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
                enc = MyEncryptor
        )
        //ctx.ent.prototype<User>(User::class, serviceCtx = ctx)
        return ctx
    }


    fun buildKeys(): List<ApiKey> {
        val keys = listOf(
                ApiKey("user", "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"),
                ApiKey("po", "0F66CD55079C42FF85C001846472343C", "user,po"),
                ApiKey("qa", "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"),
                ApiKey("dev", "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"),
                ApiKey("ops", "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"),
                ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
        )
        return keys
    }


    fun getApis(source: Source,
                auth: Auth? = null,
                apis: List<ApiSetup> = listOf()): ApiServer {

        // 2. apis
        val container = ApiServer.of(ctx, routes(apis), auth, source)
        return container
    }


    fun ensureCall(apis: List<ApiSetup> = listOf(),
                   protocolCt: String,
                   protocol: String,
                   authMode: String,
                   user: Pair<String, String>?,
                   path: String,
                   inputs: List<Pair<String, Any>>?,
                   opts: List<Pair<String, Any>>?,
                   expected: Try<String>) {
        val apis = if (user != null) {
            val keys = buildKeys()
            val auth = MyAuthProvider(user.first, user.second, keys)
            val apis = getApis(Source.CLI, auth, apis)
            apis
        } else {
            val apis = getApis(Source.CLI, apis = apis)
            apis
        }
        val cmd = CommonRequest.cli(path, inputs, opts)
        val actual = runBlocking {
            apis.executeAttempt(cmd)
        }

        Assert.assertTrue(actual.code == expected.code)
        Assert.assertTrue(actual.success == expected.success)
        Assert.assertTrue(actual.desc == expected.desc)
    }


//    fun buildUserApiRegSingleton(ctx: AppEntContext): Api {
//        return Api(UserApi(ctx), setup = SetupType.Annotated)
//    }


    fun checkCall(
        protocol: Source,
        middleware: List<Middleware> = listOf(),
        routes: Areas,
        user: Credentials?,
        request: Request,
        response: Response<*>,
        checkFailMsg:Boolean = false,
        decoder: Deserializer<JSONObject>? = null,
        metas: List<Pair<KClass<*>, MetaHandler>> = listOf(),
        ) {

        // Optional auth
        val auth = user?.let { u -> MyAuthProvider(u.name, u.roles, buildKeys()) }

        // Host
        val host = ApiServer(ctx,
                routes = routes,
                auth = auth,
                deserializer = decoder,
                metas = metas,
                settings = Settings(protocol))

        // Get result
        val actual = runBlocking {
            host.executeAttempt(request)
        }

        // Compare here.
        Assert.assertTrue(actual.code == response.code)
        Assert.assertTrue(actual.success == response.success)
        Assert.assertTrue(actual.desc == response.desc)
        actual.onSuccess {
            Assert.assertTrue(it == response.value)
        }
        if(!response.success && checkFailMsg){
            actual.onFailure {
                val expected = response.err?.message ?: ""
                val message = it.message ?: ""
                Assert.assertTrue(message.contains(expected))
            }
        }
    }
}
