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
package test

import org.junit.Test
import slatekit.apis.*
import slatekit.apis.containers.ApiContainerCLI
import slatekit.apis.core.Auth
import slatekit.apis.core.Errors
import slatekit.apis.support.ApiHelper
import slatekit.common.ApiKey
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.SUCCESS
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.integration.AppApi
import slatekit.integration.VersionApi
import slatekit.test.common.MyAuthProvider
import slatekit.tests.common.UserApi
import test.common.MyEncryptor
import test.common.User

/**
 * Created by kishorereddy on 6/12/17.
 */

open class ApiTestsBase {


    val ctx:AppContext = buildCtx()


    fun buildCtx(): AppContext {

        val ctx = AppContext (
                arg  = Args.default(),
                env  = Env("local", Dev),
                cfg  = Config(),
                log  = LoggerConsole(),
                ent  = Entities(DbLookup()),
                inf  = About("myapp", "sample app", "product group 1", "slatekit", "ny", "", "", "", "1.1.0", "", ""),
                dbs  = defaultDb(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
                enc  = MyEncryptor
        )
        ctx.ent.register<User>(false, User::class, serviceCtx = ctx)
        return ctx
    }


    fun buildKeys():List<ApiKey> {
        val keys = listOf(
                ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
                ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
                ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
                ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
                ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
                ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
        )
        return keys
    }


    fun getApis(protocol: String = "cli",
                auth    : Auth? = null,
                apis    : List<ApiReg>? = null,
                errors  : Errors? = null): ApiContainer {

        // 2. apis
        val container = ApiContainerCLI(ctx, auth, apis, errors)
        return container
    }


    fun ensureCall( apis      : List<ApiReg>? = null,
                    protocolCt: String,
                    protocol  : String,
                    authMode  : String,
                    user      : Pair<String,String>?,
                    errors    : Errors?,
                    path      : String,
                    inputs    : List<Pair<String,Any>>?,
                    opts      : List<Pair<String,Any>>?,
                    expected  : Result<String>):Unit
    {
        val apis = if(user != null) {
            val keys = buildKeys()
            val auth = MyAuthProvider(user.first, user.second, keys)
            val apis = getApis(protocolCt, auth, apis, errors)
            apis
        }
        else {
            val apis = getApis(protocolCt, apis = apis, errors = errors)
            apis
        }
        val cmd = ApiHelper.buildCliRequest(path, inputs, opts)
        val actual = apis.call( cmd )

        assert( actual.code == expected.code)
        assert( actual.success == expected.success)
        assert( actual.msg == expected.msg)
    }
}



class Api_Core_Tests : ApiTestsBase() {



    @Test fun can_register_api() {
        val apis = getApis(
                apis = listOf(ApiReg(UserApi(ctx)))
        )
        assert(apis.getMappedAction("app", "users", "activate").success)
        assert(apis.getMappedAction("app", "users", "testTypes").success)
    }


    @Test fun can_register_manually() {
        val apis = getApis(
                apis = listOf(ApiReg(UserApi(ctx)))
        )
        apis.register(AppApi(ctx))
        apis.register(ApiReg(VersionApi(ctx)))

        assert(apis.getMappedAction("app", "users", "activate").success)
        assert(apis.getMappedAction("app", "users", "testTypes").success)
        assert(apis.getMappedAction("sys", "app", "host").success)
        assert(apis.getMappedAction("sys", "version", "java").success)
    }


    @Test fun can_register_after_initial_setup() {
        val keys = buildKeys()
        val auth = MyAuthProvider("kishore", "ops", keys)

        val apis = ApiContainerCLI(ctx,
                apis = listOf(ApiReg(UserApi(ctx))),
                auth = auth
        )
        apis.register(AppApi(ctx))
        apis.register(ApiReg(VersionApi(ctx)))

        val result1 = apis.call("app", "users", "rolesNone", "get",
                mapOf("api-key" to "5020F4A237A443B4BEDC37D8A08588A3"),
                mapOf("code" to 123, "tag" to "abc"))
        assert(result1.value == "rolesNone")

        val result2 = apis.call("sys", "version", "java", "get",
                mapOf("api-key" to "5020F4A237A443B4BEDC37D8A08588A3"),
                mapOf())
        assert(result2.value == "1.8.0_91")

    }


    @Test fun can_check_action_does_NOT_exist() {
        val apis = getApis(
                apis = listOf(ApiReg(UserApi(ctx)))
        )
        assert(!apis.contains("app.users.fakeMethod"))
    }


    @Test fun can_check_action_exists() {
        val apis = getApis(
                apis = listOf(ApiReg(UserApi(ctx)))
        )
        assert(apis.contains("app.users.activate"))
    }


    @Test fun can_execute_public_action() {
        ensureCall( listOf(ApiReg(UserApi(ctx))),
                    "*", "*",
                    ApiConstants.AuthModeAppRole, null, null,
                    "app.users.rolesNone",
                    listOf(
                            Pair("code", "1"),
                            Pair("tag", "abc")
                    ),
                    null,
                    success("rolesNone", msg = "1 abc")
                  )
    }

    // ===================================================================
    //describe( "API Data-types" ) {
    @Test fun can_execute_with_type_raw_request() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                    "*", "*",
                    ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                    "app.users.argTypeRequest",
                    listOf(Pair("id", "2")),
                    null,
                    success("ok", msg = "raw request id: 2")
        )
    }


    @Test fun can_get_list() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeListInt",
                listOf(
                        Pair("items", listOf<Int>(1,2,3) )
                ),
                null,
                success("ok", msg = ",1,2,3")
        )
    }


    @Test fun can_get_list_via_conversion() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeListInt",
                listOf(
                        Pair("items", "1,2,3")),
                null,
                success("ok", msg = ",1,2,3")
        )
    }


    @Test fun can_get_map() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeMapInt",
                listOf(Pair("items", mapOf("a" to 1, "b" to 2))),
                null,
                success("ok", msg = ",a=1,b=2")
        )
    }


    @Test fun can_get_map_via_conversion() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeMapInt",
                listOf(Pair("items", "a=1,b=2")),
                null,
                success("ok", msg = ",a=1,b=2")
        )
    }
}