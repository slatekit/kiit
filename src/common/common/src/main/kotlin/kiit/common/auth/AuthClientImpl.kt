package kiit.common.auth

import kiit.common.crypto.Secure
import kiit.results.Failure
import kiit.results.Outcome
import kiit.results.Success
import kiit.results.builders.Outcomes
import kiit.results.then

open class AuthClientImpl(
    private val guest: User,
    private val secure: Secure,
    private val convert: (String) -> Outcome<AuthData>,
    private val build: (AuthData) -> Outcome<User>
) : AuthClient<User> {


    private var current: User = guest
    private var authData: AuthData = AuthData.empty
    private val AUTH_KEY = "auth"

    override fun identity(): TokenIdentity {
        return return this.authData.id
    }

    override fun access(): TokenAccess {
        return this.authData.ac
    }

    override fun refresh(): TokenRefresh {
        return this.authData.rf
    }

    override fun login(): Boolean {
        val text = this.secure.loadText(AUTH_KEY)
        val loaded = Outcomes.of(text != null, text)
        val converted = loaded.then(convert)
        val built = converted.then { data -> this.build(data).map { Pair(data, it) } }
        return when(built) {
            is Failure -> false
            is Success -> login(built.value.first, built.value.second)
        }
    }

    override fun login(data: AuthData) : Boolean {
        val converted = this.build(data)
        return when(converted) {
            is Failure -> false
            is Success -> login(data, converted.value)
        }
    }

    override fun logout(): Boolean {
        this.current = this.guest
        return this.secure.remove(AUTH_KEY)
    }

    override fun isAuthenticated(): Boolean {
        return this.current != this.guest
    }

    override fun getUser(): User {
        return this.current
    }

    override fun update() {
        // nothing for now
    }

    override fun login(data: AuthData, user: User): Boolean {
        this.current = user
        this.authData = data
        return true
    }
}