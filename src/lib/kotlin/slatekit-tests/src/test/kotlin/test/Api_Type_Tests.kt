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
import test.common.SampleApi
import test.common.User

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Type_Tests : ApiTestsBase() {


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


    @Test fun can_use_smart_type_phone() {

        ensureSmartString("smartStringPhone", ""   , "false - true - ")
        ensureSmartString("smartStringPhone", "abc", "false - false - abc")
        ensureSmartString("smartStringPhone", "123-456-789", "false - false - 123-456-789")
        ensureSmartString("smartStringPhone", "123-456-7890", "true - false - 123-456-7890")
    }


    @Test fun can_use_smart_type_email() {

        ensureSmartString("smartStringEmail", ""   , "false - true - ")
        ensureSmartString("smartStringEmail", "abc", "false - false - abc")
        ensureSmartString("smartStringEmail", "123@", "false - false - 123@")
        ensureSmartString("smartStringEmail", "123@abc.com", "true - false - 123@abc.com")
    }


    fun ensureSmartString(method:String, text:String, expected:String) {
        val userApi = SampleApi(ctx)
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(userApi)), auth = null )

        val r1 = apis.call("app", "tests", method, "get",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                mapOf("text" to text))

        assert(r1.success)
        assert(r1.value == expected)
    }
}