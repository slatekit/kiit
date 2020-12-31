/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.apis.*
import slatekit.apis.routes.Api
import slatekit.apis.core.Auth
import slatekit.apis.SetupType
import slatekit.common.requests.CommonRequest
import slatekit.common.Source
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.data.DbConString
import slatekit.common.data.Connections
import slatekit.common.data.Connections.Companion.of
import slatekit.common.envs.Envs
import slatekit.common.info.*
import slatekit.common.log.LogsDefault
import slatekit.common.requests.Request
import slatekit.common.requests.Response
import slatekit.db.Db
import slatekit.entities.Entities
import slatekit.policy.hooks.Middleware
import slatekit.integration.common.AppEntContext
import slatekit.results.Try
import test.TestApp
import test.setup.MyAuthProvider
import test.setup.UserApi
import test.setup.MyEncryptor
import test.setup.User

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
                ent = Entities({ con -> Db(con) }, Connections(cfg.dbCon())),
                info = Info.of(
                        About("tests", "myapp", "sample app", "slatekit", "ny", "", "", "")
                ),
                dbs = of(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
                enc = MyEncryptor
        )
        ctx.ent.prototype<User>(User::class, serviceCtx = ctx)
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
                apis: List<Api> = listOf()): ApiServer {

        // 2. apis
        val container = ApiServer.of(ctx, apis, auth, source)
        return container
    }


    fun ensureCall(apis: List<Api> = listOf(),
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


    fun buildUserApiRegSingleton(ctx: AppEntContext): Api {
        return Api(UserApi(ctx), setup = SetupType.Annotated)
    }


    fun ensure(
            protocol: Source,
            middleware: List<Middleware> = listOf(),
            apis: List<Api>,
            user: Credentials?,
            request: Request,
            response: Response<*>,
            checkFailMsg:Boolean = false) {

        // Optional auth
        val auth = user?.let { u -> MyAuthProvider(u.name, u.roles, buildKeys()) }

        // Host
        val host = ApiServer(ctx,
                apis = apis,
                auth = auth,
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
