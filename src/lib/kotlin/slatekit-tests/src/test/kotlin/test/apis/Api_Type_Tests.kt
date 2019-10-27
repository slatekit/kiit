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
import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.apis.setup.Setup
import slatekit.results.getOrElse
import test.setup.SampleTypes3Api
import test.setup.MyEncryptor
import test.setup.StatusEnum

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Type_Tests : ApiTestsBase() {


    // ===================================================================
    //describe( "API Decryption" ) {
    @Test fun can_decrypt_int() {
        val encryptedText = MyEncryptor.encrypt("123")
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)), auth = null, allowIO = false )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getDecInt", Verb.Read, mapOf(), mapOf("id" to encryptedText))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "decrypted int : 123")
    }


    @Test fun can_decrypt_long() {
        val encryptedText = MyEncryptor.encrypt("123456")
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)), auth = null, allowIO = false )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getDecLong", Verb.Read, mapOf(), mapOf("id" to encryptedText))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "decrypted long : 123456")
    }


    @Test fun can_decrypt_double() {
        val encryptedText = MyEncryptor.encrypt("123.456")
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)), auth = null, allowIO = false )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getDecDouble", Verb.Read, mapOf(), mapOf("id" to encryptedText))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "decrypted double : 123.456")
    }


    @Test fun can_decrypt_string() {
        val encryptedText = MyEncryptor.encrypt("slate-kit")
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)), auth = null, allowIO = false )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getDecString", Verb.Read, mapOf(), mapOf("id" to encryptedText))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "decrypted string : slate-kit")
    }


    @Test fun can_use_smart_type_phone() {

        ensureSmartString("getSmartStringPhone", ""   , false, "")
        ensureSmartString("getSmartStringPhone", "abc", false, "")
        ensureSmartString("getSmartStringPhone", "123-456-789", false, "")
        ensureSmartString("getSmartStringPhone", "123-456-7890", true,"123-456-7890")
    }


    @Test fun can_use_smart_type_email() {

        ensureSmartString("getSmartStringEmail", ""   , false, "")
        ensureSmartString("getSmartStringEmail", "abc", false, "")
        ensureSmartString("getSmartStringEmail", "123@", false, "")
        ensureSmartString("getSmartStringEmail", "123@abc.com", true,"123@abc.com")
    }


    @Test fun can_use_enum_by_name() {
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)),allowIO = false,  auth = null )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getEnum", Verb.Read, mapOf(), mapOf(Pair("status", StatusEnum.Active.name)))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }


    @Test fun can_use_enum_by_number() {
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)),allowIO = false,  auth = null )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getEnum", Verb.Read, mapOf(), mapOf(Pair("status", StatusEnum.Active.value)))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }


    @Test fun can_use_enum_value() {
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)),allowIO = false,  auth = null )
        val r1 = runBlocking {
            apis.call("samples", "types3", "getEnumValue", Verb.Read, mapOf(), mapOf(Pair("status", StatusEnum.Pending.value)))
        }
        Assert.assertTrue(r1.success)
        r1.onSuccess { it ->
            val actual = it as StatusEnum
            Assert.assertEquals(actual.value, StatusEnum.Pending.value)
        }
        r1.onFailure { throw Exception("unexpected value") }
    }


    fun ensureSmartString(method:String, text:String, success:Boolean, expected:String) {
        val api = SampleTypes3Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)),allowIO = false,  auth = null )
        val r1 = runBlocking {
            apis.call("samples", "types3", method, Verb.Read, mapOf(), mapOf("text" to text))
        }

        Assert.assertEquals(success, r1.success)
        if(success) {
            Assert.assertEquals(expected, r1.getOrElse { "" })
        }
    }
}
