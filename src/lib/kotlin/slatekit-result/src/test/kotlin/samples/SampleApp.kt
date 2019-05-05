package samples

import slatekit.results.*
import slatekit.results.StatusCodes
import slatekit.results.builders.*
import java.util.concurrent.atomic.AtomicLong


// Simple User model and registration status for sample purposes
enum class UserStatus(val value: Int) {
    Pending(0),
    Active(1),
    Blocked(2),
    None(3);
}

data class User(val id: Long,
                val userName: String,
                val email: String,
                val status: UserStatus)


data class UserError(val msg: String) : Err


// Mimic an Http Response for sample purposes
// NOTE: This could any HttpResponse
// 1. Kotlin Ktor
// 2. Java Spark
// 3. Java Jersey
data class HttpResponse(val code: Int, val msg:String, val value: Any)


// Extension method on Result<T,E> to convert to compatible HttpResponse
fun <T, E> Result<T, E>.toHttpResponse(): HttpResponse {
    // Convert to http code
    val code: Int = StatusCodes.toHttp(this.status).first

    // Serialize ( toString for sample app - but use some library like Jackson )
    val content = when (this) {
        is Success -> this.value.toString()
        is Failure -> this.error.toString()
    }
    return HttpResponse(code, this.status.msg, content)
}


/**
 * Sample User API. This example provided shows how to:
 *
 * 1. Easily convert a Result<T,E> to a HttpResponse via .toHttpResponse extension function
 * 2. Keep your API / Controller super simple as 1 liners keeping your core logic in services
 * 3. Because the Result<T,E> has a Status code compatible w/ http,
 *    it is easy model Successes / Failures in your Service layer and have them properly
 *    propagated to your API layer and subsequently to the client.
 */
class UserApi(private val service: UserService) {

    /**
     * Your controller method now becomes just 1 line.
     * Result<T,E> keeps this clean by ACCURATELY representing both
     * successes and failures and making them easily convertible to HTTP
     */
    fun register(name: String, email: String): HttpResponse {
        return service.register(User(0, name, email, UserStatus.None)).toHttpResponse()
    }


    fun activate(id: Long): HttpResponse {
        return service.activate(id).toHttpResponse()
    }
}


/**
 * Sample Service to handle User Registration and management
 */
class UserService(private val repo: UserRepo) {

    /**
     * Sample method to register a User.
     * This highlights different ways of building accurate failures
     */
    fun register(user: User): Result<User, Err> {
        val exResult = registerWithExceptions(user)
        val erResult = exResult.toOutcome()
        return erResult
    }


    /**
     * Sample method to register a User.
     */
    fun registerWithErrorType(user: User): Result<User, UserError> {
        // Case 1: Create using [Failure] branch of Result
        // [Err] is an empty marker interface so you can create errors of any type.
        // A convenient Err.of method is supplied to build an [Err] from a string.
        if (user.status == UserStatus.Pending)
            return Success(user, "registration already pending")

        // Case 2: Create using [Failure] branch of Result
        // [Err] is an empty marker interface so you can create errors of any type.
        // A convenient Err.of method is supplied to build an [Err] from a string.
        if (user.id > 0)
            return Failure(UserError("already registered"), StatusCodes.IGNORED)

        // Case 3: Create using builder methods in [Results]
        // Same as Failure(Err.of("user name not supplied"), StatusCodes.INVALID)
        if (user.userName.isEmpty())
            return Failure(UserError("user name not supplied"), StatusCodes.INVALID)

        // Case 4: Create using builder + explicit status code
        // Many of the builder methods have optional parameters
        if (repo.exists(user.email))
            return Failure(UserError("duplicate user name"), StatusCodes.CONFLICT)

        // Case 5: Sample rule: prevent registration via API of special emails.
        // This doesn't allocate any new object for [Err] or [Status]
        // Same as errored(StatusCodes.UNAUTHORIZED, StatusCodes.UNAUTHORIZED)
        if (user.email.toLowerCase().contains("@justice-league.com"))
            return Failure(UserError("requires special registration"), StatusCodes.DENIED)

        val userWithId = repo.create(user)
        // or success(userWithId) where status code = SUCCESS
        return Success(userWithId, StatusCodes.CREATED)
    }


    /**
     * Sample method to register a User.
     * Try<User> = Result<User, Exception>
     *
     * NOTE: Idiomatic Kotlin ( via when/expressions ) is
     * avoided here to focus on the concept for people
     * unfamiliar with kotlin
     */
    fun registerWithExceptions(user: User): Try<User> {
        return Try.attemptWithStatus {
            // Case 1: Create using [Failure] branch of Result
            // [Err] is an empty marker interface so you can create errors of any type.
            // A convenient Err.of method is supplied to build an [Err] from a string.
            if (user.status == UserStatus.Pending)
                Success(user, "registration pending", StatusCodes.PENDING.code)

            // Case 2: Create using [Failure] branch of Result
            // [Err] is an empty marker interface so you can create errors of any type.
            // A convenient Err.of method is supplied to build an [Err] from a string.
            if (user.id > 0)
                throw IgnoredException("already registered")

            // Case 3: Create using builder methods in [Results]
            // Same as Failure(Err.of("user name not supplied"), StatusCodes.INVALID)
            if (user.userName.isEmpty())
                throw InvalidException("user name not supplied")

            // Case 4: Create using builder + explicit status code
            // Many of the builder methods have optional parameters
            if (repo.exists(user.email))
                throw ErroredException("duplicate user name", StatusCodes.CONFLICT)

            // Case 5: Sample rule: prevent registration via API of special emails.
            // This doesn't allocate any new object for [Err] or [Status]
            // Same as errored(StatusCodes.UNAUTHORIZED, StatusCodes.UNAUTHORIZED)
            if (user.email.toLowerCase().contains("@justice-league.com"))
                throw DeniedException("requires special registration")

            val userWithId = repo.create(user)
            // or success(userWithId) where status code = SUCCESS
            Success(userWithId, StatusCodes.CREATED)
        }
    }


    /**
     * Sample method to register a User.
     * Outcome<User> = Result<User, Err>
     *
     * NOTE: Idiomatic Kotlin ( via when/expressions ) is
     * avoided here to focus on the concept for people
     * unfamiliar with kotlin
     */
    fun registerWithOutcome(user: User): Outcome<User> {
        // Case 1: Create using [Failure] branch of Result
        // [Err] is an empty marker interface so you can create errors of any type.
        // A convenient Err.of method is supplied to build an [Err] from a string.
        if (user.status == UserStatus.Pending)
            return Outcomes.pending(user, "registration already pending")

        // Case 2: Create using [Failure] branch of Result
        // [Err] is an empty marker interface so you can create errors of any type.
        // A convenient Err.of method is supplied to build an [Err] from a string.
        if (user.id > 0)
            return Outcomes.ignored("already registered")

        // Case 3: Create using builder methods in [Results]
        // Same as Failure(Err.of("user name not supplied"), StatusCodes.INVALID)
        if (user.userName.isEmpty())
            return Outcomes.invalid("user name not supplied")

        // Case 4: Create using builder + explicit status code
        // Many of the builder methods have optional parameters
        if (repo.exists(user.email))
            return Outcomes.errored("duplicate user name", StatusCodes.CONFLICT)

        // Case 5: Sample rule: prevent registration via API of special emails.
        // This doesn't allocate any new object for [Err] or [Status]
        // Same as errored(StatusCodes.UNAUTHORIZED, StatusCodes.UNAUTHORIZED)
        if (user.email.toLowerCase().contains("@justice-league.com"))
            return Outcomes.denied("requires special registration")

        val userWithId = repo.create(user)
        // or success(userWithId) where status code = SUCCESS
        return Outcomes.success(userWithId, StatusCodes.CREATED)
    }


    /**
     * Use the Outcome<T> alias for Result<T,Err>
     */
    fun activate(id: Long): Outcome<User> {

        val user = repo.getById(id)
        if (user == null)
            return Outcomes.invalid("User with id : $id not found")

        // Ensure currently in pending state
        if (user.status != UserStatus.Pending)
            return Outcomes.invalid("Not in pending activation state")

        // Sample rule: prevent name update via API if justice league.
        // Could also return errored(StatusCodes.UNAUTHORIZED)
        if (user.email.toLowerCase().contains("@justice-league.com"))
            return Outcomes.denied()

        repo.update(user.copy(status = UserStatus.Active))
        // or success(userWithId) where status code = SUCCESS
        return Outcomes.success(user, StatusCodes.UPDATED)
    }
}


/**
 * User Repo ( mimic a database for sample purposes )
 */
class UserRepo {
    // Mimic database auto-inc key
    private val nextId = AtomicLong(0)

    // Mimic a database for sample purposes
    private val users = mutableListOf(
            User(nextId.incrementAndGet(), "batman", "batman@justice-league.com", UserStatus.Active),
            User(nextId.incrementAndGet(), "superman", "superman@justice-league.com", UserStatus.Active),
            User(nextId.incrementAndGet(), "wonderwoman", "wonderwoman@justice-league.com", UserStatus.Active)
    )

    fun exists(email: String): Boolean = users.firstOrNull { it.email == email } != null

    fun getById(id: Long): User? = users.firstOrNull { it.id == id }

    fun create(user: User): User {
        val id = nextId.incrementAndGet()
        val userWithId = user.copy(id = id, status = UserStatus.Pending)
        users.add(userWithId)
        return userWithId
    }

    fun update(user: User) {
        val index = users.indexOfLast { it.email == user.email }
        users.removeAt(index)
        users.add(user)
    }
}





