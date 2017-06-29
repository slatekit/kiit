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
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
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

class ApiTests {


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


    // ===================================================================
    //describe( "API Decryption" ) {
    @Test fun can_decrypt_int() {
        val encryptedText = MyEncryptor.encrypt("123")
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.decInt",
                listOf(Pair("id", encryptedText)),
                null,
                success("ok", msg = "decrypted int : 123")
        )
    }


    @Test fun can_decrypt_long() {
        val encryptedText = MyEncryptor.encrypt("123456")
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.decLong",
                listOf(Pair("id", encryptedText)),
                null,
                success("ok", msg = "decrypted long : 123456")
        )
    }


    @Test fun can_decrypt_double() {
        val encryptedText = MyEncryptor.encrypt("123.456")
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.decDouble",
                listOf(Pair("id", encryptedText)),
                null,
                success("ok", msg = "decrypted double : 123.456")
        )
    }


    @Test fun can_decrypt_string() {
        val encryptedText = MyEncryptor.encrypt("slate-kit")
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*",
                ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.decString",
                listOf(Pair("id", encryptedText)),
                null,
                success("ok", msg = "decrypted string : slate-kit")
        )
    }

    // ===================================================================
    //describe( "Authorization: using App roles on actions" ) {
    @Test fun roles_should_work_when_role_is_any() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesAny", msg="1 abc")
        )
    }


    @Test fun roles_should_fail_for_any_role_any_with_no_user() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, null, null,
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>(msg = "Unable to authorize, authorization provider not set")
        )
    }


    @Test fun roles_should_work_for_a_specific_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.rolesSpecific",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesSpecific", msg="1 abc")
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "ops"), null,
                "app.users.rolesSpecific",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>("unauthorized")
        )
    }


    @Test fun roles_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "admin"), null,
                "app.users.rolesParent",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesParent", msg="1 abc")
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.rolesParent",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>("unauthorized")
        )
    }

    // ===================================================================
    //describe( "Authorization: using Key roles on actions" ) {
    @Test fun roles_by_key_should_work_when_role_is_any() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"), null,
                "app.users.rolesAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                success("rolesAny", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_any_role_with_no_user() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, null, null,
                "app.users.rolesAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>(msg = "Unable to authorize, authorization provider not set")
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"), null,
                "app.users.rolesSpecific",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                success("rolesSpecific", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "qa"), null,
                "app.users.rolesSpecific",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "EB7EB37764AD4411A1763E6A593992BD")),
                unAuthorized<String>("unauthorized")
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "admin"), null,
                "app.users.rolesParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "54B1817194C1450B886404C6BEA81673")),
                success("rolesParent", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"), null,
                "app.users.rolesParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                unAuthorized<String>("unauthorized")
        )
    }

    // ===================================================================
    //describe( "API Container Type CLI" ) {
    @Test fun should_work_when_setup_as_protocol_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolAny", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "cli", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolCLI",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolCLI", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolParent", msg="1 abc")
        )
    }


    @Test fun should_FAIL_when_setup_as_protocol_WEB_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "cli", "web", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolWeb",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                notFound<String>(msg = "app.users.protocolWeb not found")
        )
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
        val cmd = ApiHelper.buildRequest(path, inputs, opts)
        val actual = apis.call( cmd )

        assert( actual.code == expected.code)
        assert( actual.success == expected.success)
        assert( actual.msg == expected.msg)
    }

}