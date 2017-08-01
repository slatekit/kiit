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
import slatekit.apis.helpers.ApiHelper
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
import slatekit.common.results.ResultFuncs.success
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext
import slatekit.test.common.MyAuthProvider
import slatekit.tests.common.UserApi
import test.common.MyEncryptor
import test.common.User

/**
 * Created by kishorereddy on 6/12/17.
 */

open class ApiTestsBase {


    val ctx:AppEntContext = buildCtx()


    fun buildCtx(): AppEntContext {

        val ctx = AppEntContext (
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


    fun buildUserApiRegSingleton(ctx: AppEntContext):ApiReg {
        return ApiReg(UserApi(ctx))
    }
}


// 1. setup ( singleton | annotations | area | declared )
// 2. rest
// 3. security
// 4. types ( inputs   )
// 5. types ( outputs  )
// 6. types ( advanced )
// 7. errors
// 8. files
// 9. middleware
// 10. protocol


class Api_Core_Tests : ApiTestsBase() {



    @Test fun can_execute_public_action() {
        ensureCall( listOf(buildUserApiRegSingleton(ctx)),
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
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                    "*", "*",
                    ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                    "app.users.argTypeRequest",
                    listOf(Pair("id", "2")),
                    null,
                    success("ok", msg = "raw request id: 2")
        )
    }


    @Test fun can_get_list() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
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
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
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
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeMapInt",
                listOf(Pair("items", mapOf("a" to 1, "b" to 2))),
                null,
                success("ok", msg = ",a=1,b=2")
        )
    }


    @Test fun can_get_map_via_conversion() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.argTypeMapInt",
                listOf(Pair("items", "a=1,b=2")),
                null,
                success("ok", msg = ",a=1,b=2")
        )
    }
}