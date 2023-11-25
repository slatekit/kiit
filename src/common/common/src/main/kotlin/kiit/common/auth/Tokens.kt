package kiit.common.auth

import kiit.common.DateTime
import kiit.common.values.Gets
import kiit.common.values.MapReads


/*
A wrapper for an auth id token ( JWT ).
Provides properties for checking id claims

 NOTES:
 1. Intended to be used on the client side ( mobile )

 LINKS:
 1. https://auth0.com/blog/id-token-access-token-what-is-the-difference/
 2. https://www.oauth.com/oauth2-servers/openid-connect/id-tokens/
 3. https://www.oauth.com/oauth2-servers/access-tokens/self-encoded-access-tokens/

 EXAMPLE:
 {
  "iss": "https://server.example.com",
  "sub": "24400320",
  "aud": "s6BhdRkqt3",
  "nonce": "n-0S6_WzA2Mj",
  "exp": 1311281970,
  "iat": 1311280970,
  "auth_time": 1311280969
}
 */
open class TokenIdentity(val jwt:String, private val atts:Map<String,Any?>) : TokenClaims {
    private val _claims: MapReads = MapReads(atts)
    override val ttype: TokenType get() = TokenType.Identity

    override val roles: List<Role>   by lazy {
        this.claims().getString("roles").split(',').map { Role.of(it) }
    }

    override fun claims(): Gets {
        return _claims
    }
}


/*
A wrapper for an auth access token ( JWT ).
Provides small functions for checking roles/scopes and other claims.

 NOTES:
 1. Usage : Intended to be used on the server side
 2. Scope : A permission   ( e.g. account.read, account.write )
 3. Claims: Provides other functions to checking claims ( issued at, expires at and if its valid ).
 4. Role  : A type of user ( e.g. user, admin, editor ), added here to supplement scopes

 LINKS:
 1. https://auth0.com/blog/id-token-access-token-what-is-the-difference/
 2. https://www.oauth.com/oauth2-servers/openid-connect/id-tokens/
 3. https://www.oauth.com/oauth2-servers/access-tokens/self-encoded-access-tokens/

 EXAMPLE:
 {
  "iss": "https://authorization-server.com/",
  "exp": 1637344572,
  "aud": "api://default",
  "sub": "1000",
  "client_id": "https://example-app.com",
  "iat": 1637337372,
  "jti": "1637337372.2051.620f5a3dc0ebaa097312",
  "scope": "account.read,account.write"
}
 */
open class TokenAccess(val jwt:String, private val atts:Map<String,Any?>) : TokenClaims {

    private val _claims: MapReads = MapReads(atts)

    override val ttype: TokenType    get() = TokenType.Access

    override val roles: List<Role>   by lazy {
        this.claims().getString("roles").split(',').map { Role.of(it) }
    }

    val scopes: List<Scope>          by lazy {
        this.claims().getString("scopes").split(',').map { Scope.of(it) }
    }

    fun hasScope(scope:String) : Boolean {
        val scopeInfo = Scope.of(scope)
        val all = this.scopes
        return all.firstOrNull { it == scopeInfo } != null
    }

    fun hasScopes(scopes:List<String>) : Boolean {
        return scopes.map { hasScope(it) }.all { it }
    }

    fun hasRolesOrScopes(roles:List<String>, scopes:List<String>) : Boolean {
        return this.hasRoles(roles) || this.hasScopes(scopes)
    }


    override fun claims(): Gets {
        return _claims
    }
}


/*
A wrapper for an auth refresh token ( JWT ).

 NOTES:
 1. Intended to be used on the client side ( mobile )

 LINKS:
 1. https://auth0.com/docs/secure/tokens/refresh-tokens
 2. https://auth0.com/blog/refresh-tokens-what-are-they-and-when-to-use-them/
 3. https://www.oauth.com/oauth2-servers/access-tokens/refreshing-access-tokens/

 EXAMPLE:
 abc.123
 */
open class TokenRefresh(val value:String) {
}



/**
 * Represents access to JWT "claims" that are common to both Id and Access tokens.
 *
 *  NOTES:
 *  1. Added role as a named group of scope/permissions ( e. user, admin, editor, etc )
 *
 *  LINKS:
 *  1. https://auth0.com/blog/id-token-access-token-what-is-the-difference/
 *  2. https://www.oauth.com/oauth2-servers/openid-connect/id-tokens/
 *  3. https://www.oauth.com/oauth2-servers/access-tokens/self-encoded-access-tokens/
 *
 */
interface TokenClaims {
    val ttype: TokenType

    val subject: String     get() = this.claims().getString("sub")
    val audience: String    get() = this.claims().getString("aud")
    val region: String      get() = this.claims().getString("reg")
    val issuedAt: DateTime  get() = this.claims().getZonedDateTimeUtc("iat")
    val expiresAt: DateTime get() = this.claims().getZonedDateTimeUtc("exp")
    val roles: List<Role>

    fun claims() : Gets

    fun isValid(): Boolean = this.expiresAt.isAfter(DateTime.now())

    fun hasRole(role:String) : Boolean {
        return this.roles.firstOrNull { it.name == role } != null
    }

    fun hasRoles(roles:List<String>) : Boolean {
        return roles.map { this.hasRole(it) }.all { it }
    }
}