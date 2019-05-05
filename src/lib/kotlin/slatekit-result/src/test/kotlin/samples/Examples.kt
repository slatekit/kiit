package samples

import slatekit.results.*
import slatekit.results.StatusCodes
import slatekit.results.builders.Outcomes


/**
 * https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/src/kotlin/util/Result.kt
 */
class Examples {

    data class Person(val name: String)


    fun tldr(){
        // CREATE: Create the success/failures in different ways:
        // 1. Using Success/Failure branch of Result with optional code and message
        // 2. Using Results.x builder methods ( success, invalid, ignored, errored, unexpected )
        // 3. Using Result.of companion function which sets the Failure type to [Err]
        // 4. Using Result.attempt companion function which sets the Failure type to [Exception]
        val r1 : Result<Int,Err> = Success(1)
        val r2 : Result<Int,Err> = Success(1, msg = "example 1", code = 1000)
        val r3 : Result<Int,Err> = Result.of { "1".toInt() }
        val r4 : Result<Int,Exception> = Result.attempt { "1".toInt() }

        // OPERATIONS: Accessing, transforming, handling values:
        // 1. map       | flatMap
        // 2. getOrElse | getOrNull
        // 3. exists    | contains
        // 4. onSuccess | onFailure
        // 5. fold      | transform

        // MAP: Use map/flatMap to convert the value into another Result
        val result = r3.map { it + 1 }.flatMap { Success(it + 2 ) }

        // GET: Get a value if success or default value/null if failure
        println( result.getOrElse { 0 } )
        println( result.getOrNull() )

        // FIELDS: Access the success / code / message fields
        println("success: ${result.success}, code: ${result.code}, msg: ${result.msg}")

        // MATCH: Use Kotlin when expression for tests
        // The .value is only available on Success, and .error is only available for Failures
        when ( result ) {
            is Success -> println( result.value )
            is Failure -> println( result.error )
        }

        // CHECK: Check for a value using a lambda or a supplied value
        println( result.exists { it == 1 } )
        println( result.contains(4) )

        // HANDLE: Run code for a success or failure branch
        result.onSuccess { println("converted from string '1' to int 1") }
    }


    fun find(name: String): Result<Int, Err> {

        // Predefined list of user names
        val users = listOf("userA" to 1, "userB" to 2, "userC" to 3)

        // Find user by first field ( name )
        val user = users.firstOrNull { it.first == name }

        // If found
        return when (user) {
            is Pair<*, *> -> Success(user.second)
            else -> Failure(Err.of("Unable to find user"))
        }
    }

    fun exec() {

        // Case 1: Success sample
        // NOTE: Examples shown with inferred and explicit type as a Result<Person,String>, Error type is string.
        val success1a = Success(Person("Superman"))
        val success1b: Result<Person, String> = Success(Person("Superman"), code = 1)
        val success1c: Result<Person, String> = Success(Person("Superman"), msg = "success")
        val success1d: Result<Person, String> = Success(Person("Superman"), code = 1, msg = "success")
        val success1e: Result<Person, String> = Success(Person("Superman"), StatusCodes.SUCCESS)

        // Case 2: Failure sample
        // NOTE: Examples shown with inferred and explicit type as a Result<Person,String>, Error type is string.
        val failure1a = Failure("Unable to get person")
        val failure1b: Result<Person, String> = Failure("Unable to get person", code = 1)
        val failure1c: Result<Person, String> = Failure("Unable to get person", msg = "User error")
        val failure1d: Result<Person, String> = Failure("Unable to get person", code = 1, msg = "User error")
        val failure1e: Result<Person, String> = Failure("Unable to get person", StatusCodes.UNAUTHORIZED)

        // Case 3: Build successes/failures using the Results.x convenience functions
        val result1a: Result<Person, Err> = Outcomes.success(Person("Superman"))
        val result1b: Result<Person, Err> = Outcomes.success(Person("Superman"), msg = "success")
        val result1c: Result<Person, Err> = Outcomes.invalid(msg = "name not supplied, can not get user")
        val result1d: Result<Person, Err> = Outcomes.errored(msg = "unable to get user ")
        val result1e: Result<Person, Err> = Outcomes.unexpected(msg = "unexpected error while getting user")
        val result1f: Result<Person, Err> = Outcomes.errored(StatusCodes.UNEXPECTED)

        // Case 4: Operations - map/flatMap, convert Result<T1,E> to Result<T2,E>
        result1a.map { user -> "DC Universe: ${user.name}" }
        result1a.flatMap { user -> Success("DC Universe: ${user.name}") }

        // Case 5: Operations - fold, convert success/failure into a value
        val msg: String = result1a.fold({ "Success: ${it.name}" }, { "Failure: $it" })

        // Case 6: Operations - handle success/failures
        result1a.onSuccess { println("Got user: ${it.name}") }
        result1a.onFailure { println("Failure:  $it") }

        // Case 7: Get values or default
        val person1: Person? = result1a.getOrNull()
        val person2: Person = result1a.getOrElse { Person("Batman") }

        // Case 8: Check values
        val isSuperman1: Boolean = result1a.exists { it.name == "Superman" }
        val isSuperman2: Boolean = result1a.contains(Person("Superman"))

        // Case 9: Build using Result.x companion methods
        val of1: Result<Person, Err> = Result.of { Person("superman") }
        val of2: Result<Person, Exception> = Result.attempt { Person("superman") }

        // Case 10: Transform
        val t1: Result<String, Err> = of1.transform( { Success(it.name)}, { Failure(Err.of("Unknown user")) })
        val t2: Result<Person, Err> = of1.withStatus( StatusCodes.SUCCESS, StatusCodes.MISSING )

        // Case 11: Converted
        val v1: Result<Int,Err> = 42.toSuccess()
        val v2: Result<Int,String> = "Not found".toFailure()
    }
}