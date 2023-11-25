package test.common

import kiit.common.DateTime
import kiit.common.auth.*
import kiit.common.crypto.Secure
import kiit.common.ext.atUtc
import kiit.common.ext.toNumeric
import kiit.common.ext.toStringUtc
import kiit.results.builders.Outcomes
import org.junit.Assert
import org.junit.Test

class AuthTests {

    @Test
    fun can_check_id() {
        val iat = DateTime.now().atUtc()
        val exp = iat.plusDays(2).atUtc()
        val claims = mapOf<String,Any?>(
            "sub" to "uuid123",
            "reg" to "us",
            "aud" to "mobile",
            "iat" to iat.toStringUtc(),
            "exp" to exp.toStringUtc(),
            "roles" to "user,admin",
            "name" to "user123"
        )
        val token = TokenIdentity("abc.123.xyz", claims)
        Assert.assertEquals("uuid123", token.subject)
        Assert.assertEquals("user123" , token.claims().getString("name"))
        Assert.assertEquals("mobile" , token.audience)
        Assert.assertEquals("us", token.region)
        Assert.assertEquals(iat.toNumeric(), token.issuedAt.toNumeric())
        Assert.assertEquals(exp.toNumeric(), token.expiresAt.toNumeric())
        Assert.assertTrue(token.hasRole("user"))
        Assert.assertTrue(token.hasRole("admin"))
        Assert.assertFalse(token.hasRole("editor"))
    }

    @Test
    fun can_check_access() {
        val iat = DateTime.now().atUtc()
        val exp = iat.plusDays(2).atUtc()
        val claims = mapOf<String,Any?>(
            "sub" to "uuid123",
            "reg" to "us",
            "aud" to "mobile",
            "iat" to iat.toStringUtc(),
            "exp" to exp.toStringUtc(),
            "roles" to "user,admin",
            "scopes" to "profile:read,profile:write"
        )
        val token = TokenAccess("abc.123.xyz", claims)
        Assert.assertEquals("uuid123", token.subject)
        Assert.assertEquals("mobile" , token.audience)
        Assert.assertEquals("us", token.region)
        Assert.assertEquals(iat.toNumeric(), token.issuedAt.toNumeric())
        Assert.assertEquals(exp.toNumeric(), token.expiresAt.toNumeric())
        Assert.assertTrue(token.hasRole("user"))
        Assert.assertTrue(token.hasRole("admin"))
        Assert.assertTrue(token.hasRoles(listOf("user", "admin")))
        Assert.assertFalse(token.hasRole("editor"))
        Assert.assertTrue(token.hasScope("profile:read"))
        Assert.assertTrue(token.hasScope("profile:write"))
        Assert.assertFalse(token.hasScope("profile:manage"))
        Assert.assertTrue(token.hasScopes(listOf("profile:read", "profile:write")))
    }



    @Test
    fun can_build_auth() {
        val iat = DateTime.now().atUtc()
        val exp = iat.plusDays(2).atUtc()
        val id = mapOf<String,Any?>(
            "sub" to "uuid123",
            "reg" to "us",
            "aud" to "mobile",
            "iat" to iat.toStringUtc(),
            "exp" to exp.toStringUtc(),
            "roles" to "user,admin",
            "name" to "user123"
        )
        val access = mapOf<String,Any?>(
            "sub" to "uuid123",
            "reg" to "us",
            "aud" to "mobile",
            "iat" to iat.toStringUtc(),
            "exp" to exp.toStringUtc(),
            "roles" to "user,admin",
            "scopes" to "profile:read,profile:write"
        )
        val data = AuthData("json",
            id = TokenIdentity("", id),
            ac = TokenAccess("", access),
            rf = TokenRefresh("") )

        val user = User("uuid123", "user123", "user123")

        val auth = AuthClientImpl(User.guest, SecureMock(),
            { text -> Outcomes.success(data)},
            { data -> Outcomes.success(user) } )

        Assert.assertFalse(auth.isAuthenticated())
        Assert.assertEquals(User.guest, auth.getUser())

        auth.login(data, user)
        Assert.assertTrue(auth.isAuthenticated())
        Assert.assertEquals("uuid123", auth.getUser().id)
        Assert.assertEquals("user123", auth.getUser().userName)
    }

    class SecureMock : Secure {
        override fun save(name: String, data: ByteArray?): Boolean {
            return true
        }

        override fun load(name: String): ByteArray? {
            return null
        }

        override fun remove(name: String): Boolean {
            return true
        }

        override fun saveText(name: String, text: String): Boolean {
            return true
        }

        override fun loadText(name: String): String? {
            return ""
        }

    }
}