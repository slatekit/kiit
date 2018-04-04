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

import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import test.setup.SampleTypes3Api
import test.setup.MyEncryptor

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Type_Tests : ApiTestsBase() {


    // ===================================================================
    //describe( "API Decryption" ) {
    @Test fun can_decrypt_int() {
        val encryptedText = MyEncryptor.encrypt("123")
        val api = SampleTypes3Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )
        val r1 = apis.call("samples", "types3", "getDecInt", "get", mapOf(), mapOf("id" to encryptedText))
        assert(r1.success)
        assert(r1.value == "decrypted int : 123")
    }


    @Test fun can_decrypt_long() {
        val encryptedText = MyEncryptor.encrypt("123456")
        val api = SampleTypes3Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )
        val r1 = apis.call("samples", "types3", "getDecLong", "get", mapOf(), mapOf("id" to encryptedText))
        assert(r1.success)
        assert(r1.value == "decrypted long : 123456")
    }


    @Test fun can_decrypt_double() {
        val encryptedText = MyEncryptor.encrypt("123.456")
        val api = SampleTypes3Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )
        val r1 = apis.call("samples", "types3", "getDecDouble", "get", mapOf(), mapOf("id" to encryptedText))
        assert(r1.success)
        assert(r1.value == "decrypted double : 123.456")
    }


    @Test fun can_decrypt_string() {
        val encryptedText = MyEncryptor.encrypt("slate-kit")
        val api = SampleTypes3Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )
        val r1 = apis.call("samples", "types3", "getDecString", "get", mapOf(), mapOf("id" to encryptedText))
        assert(r1.success)
        assert(r1.value == "decrypted string : slate-kit")
    }


    @Test fun can_use_smart_type_phone() {

        ensureSmartString("getSmartStringPhone", ""   , "false - true - ")
        ensureSmartString("getSmartStringPhone", "abc", "false - false - abc")
        ensureSmartString("getSmartStringPhone", "123-456-789", "false - false - 123-456-789")
        ensureSmartString("getSmartStringPhone", "123-456-7890", "true - false - 123-456-7890")
    }


    @Test fun can_use_smart_type_email() {

        ensureSmartString("getSmartStringEmail", ""   , "false - true - ")
        ensureSmartString("getSmartStringEmail", "abc", "false - false - abc")
        ensureSmartString("getSmartStringEmail", "123@", "false - false - 123@")
        ensureSmartString("getSmartStringEmail", "123@abc.com", "true - false - 123@abc.com")
    }


    fun ensureSmartString(method:String, text:String, expected:String) {
        val api = SampleTypes3Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)),allowIO = false,  auth = null )
        val r1 = apis.call("samples", "types3", method, "get", mapOf(), mapOf("text" to text))

        assert(r1.success)
        assert(r1.value == expected)
    }
}