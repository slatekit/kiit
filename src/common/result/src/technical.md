
# Result<T,E>: Model success & failure with optional status codes


## Abstract
This paper presents the design and implementation of **Result<T,E>**, a simple, familiar data structure for accurately modeling successes and failures in code. This differs from other implementations with the addition of **optional** `Status Codes`. 
Place holder

## Intro
Place holder

## Related
Result<T,E> is a **variation** of implementations existing in other langauges such as Scala's `Either<L,R>`, Scala's `Try<T>`, Rust's `Result<T,E>`, Swifts `Result<T,E>`, and Kotlin's `Result<T>`. The major difference is the addition of an optional **Status code**. For further reading, the implementation of Result in this paper will be referred to as simply **this Result**, **Result<T,E>** or **SlateKit's Result** ( having originated from the **Slate Kit** Kotlin library). This Result<T,E> is essentially a specialized `Either<L,R>` but the nomenclature is based on Scala's `Try<T>`. It looks and behaves very similarly to Rust's or Swifts `Result<T,E>` except with the addition of the optional status field that contains both an integer code and string message. GRPC and its codes implementation have inspired the use of optional and standardized `Status codes` as well. One other major point about the status code are that they are compatible with **Http Status codes**.

name | source | notes 
-----|------|-----
[Try](https://www.scala-lang.org/api/2.12.4/scala/util/Try.html)    | Scala | Modeled after Try with Success/Failure branches
[Result](https://doc.rust-lang.org/std/result/) | Rust  | Inspired some of the operations on Result
[Either](https://www.scala-lang.org/api/2.12.4/scala/util/Either.html) | Scala | Result is basically a specialized either
[GRPC](https://github.com/grpc/grpc/blob/master/doc/statuscodes.md) | Go | Inspired the setup of standardized status codes

## Model
In order to present this concept and implementation, we will be using a simple Web API as a concrete example. Many services today are built as Web APIs especially as the 1st line of contact to an external/public facing world. This example will involve a simple user registration workflow where a request to register an user is made to a UserAPI. Typically, an API ( entry point into your application ) has actions that are kept thin and delegate most of their actual work and core logic to a logic layer ( typically called a Service ). As such, the UserAPI example follows this approach by simply getting the users name and email and delegating most of the registration logic to its counter part, the UserService, which handles all the logic of user registration. The big question here is: **`How to model the success and failure of this registration flow?`** Lets represent the scenario with some simple code below and look at some of the approaches next.

**NOTE**: The code examples below are mostly in **Kotlin**. However, for simplicity, some code is pseudocode / language agnostic, and **Async/Concurrency** is exluded to avoid distractions and focus on the concepts. Also, some questions/unknowns are presented as **???**

```kotlin

// API 
class UserAPI {

    // other code, setup, etc ...

    fun register( request: HttpRequest ): HttpResponse {
        val name  : String = request.get("name")  // "user1"
        val email : String = request.get("email") // "user1@gmail.com"

        // What should the result of register be ?
        val result : ??? = service.register( name, email )

        // Assume something exists to convert result 
        // from the service.register call above to an Web / HttpResponse
        return buildHttpResponse( result ) 
    }
}

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): ??? {
        // check for valid name/email format

        // check for duplicate email in system

        // sample rule: prevent well known names security reasons
    
        // return some result
    }  
}

```


## Problem
Using the sample model above, there are several approaches/solutions to **`How to model the success and failure of this registration flow?`**. Lets start with some naive approaches and incrementally build up to modern, functional ( as in functional programming ), sophisticated ones.

### Approach 1: User
`fun register( name, email ): User?`

In this very naive approach, we simply return a true/false for success or failure.
- on success: true  -> registration successful 
- on failure: false -> registration failed 

```kotlin

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): User? {
        
        // check for valid name/email format
        if ( !validateEmail( email) ) 
            return null

        // check for duplicate email in system
        if ( !isDuplicate( email ) ) 
            return null
        
        // sample rule: prevent well known names for security reasons
        if ( isReservedUserName( name ) ) 
            return null

        // return some result
        val user = createUser(name, email)
        return user
    }
}
```

**PROBLEMS**
There are a few glaring problems here:
1. **Reason for failure**: From the perspective of the **UserAPI** and the client making the request, its impossible to know what is the reason/error for a registration failure. The logic inside there **UserService** may be handling all the failure cases appropriately, but by returning just a minimal Boolean(true/false), very little information is available to inform the client of the request the reason for the failure, so they may fix the request on a retry.
2. **Which HTTP Code to return ?**: The 3rd problem here is that since this is a Web/Http API, the UserAPI ( not shown ) has return the appropriate **HTTP Code** for the boolean response. The codes that are possible are either a) `200: Ok`, b) `400: Bad request` ( e.g. some bad / invaid data was supplied ), c) `500: Server error` in the event of a potential unexpected failure. 
3. **Nullable User Object**: Another problem here is that this returns a nullable User Object so the UserAPI will have to check if there it is null or not and operate on the non-null value. This is a somewhat trivial issue but impacts the design of the `register` method and usage of it. 

### Approach 2: Outcome<User>
In this approach, lets build a simple data structure called **Outcome**, which will serve to solve some of the problems in approach 1 above and also to lay the foundation for some concepts coming next and to segueway into the functional types `Try`, `Either`, `Result` in the next approaches. The success / failure will be represented as:
- success: Succcess( User(id = 1, username = "user1", email = "user1@gmail") )
- failure: Failure( error = "invalid email" )

```kotlin

sealed class Outcome<out T> {
    
    data class Success<out T>(val value:T) : Outcome<T>

    data class Failure(val error:String, val ex:Exception?> : Outcome<Nothing>
}

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): Outcome<User> {
        
        // check for valid name/email format
        return if ( !validateEmail( email) ) 
            Outcome.Failure("invalid email")

        // check for duplicate email in system
        else if ( !isDuplicate( email ) ) 
            Outcome.Failure("duplicate email")
        
        // sample rule: prevent well known names for security reasons
        else if ( isReservedUserName( name ) ) 
            Outcome.Failure("Reserved name")

        // return some result
        else 
            Outcome.Success( createUser(name, email) )
    }
}
```

**Notes**: 
1. `Outcome<T>` is represent either a success or failure
2. `Success<T>` is a sub-class or branch of Outcome to represent successes
3. `Failure` is a sub-class or branch of Outcome to represent failures
4. The client consuming the `Outcome` will have to check for these branches
5. This is a essentially a simple/functionally limited version of the next 3 approaches `Try`, `Either`, `Result` which also have 2 branches like `Outcome` 

#### Branches
- **Try&lt;T&gt;**      : Success&lt;T> , Failure&lt;Exception> 
- **Either&lt;A,B&gt;** : Left&lt;A>, Right&lt;B>
- **Result&lt;T,E&gt;** : Ok&lt;T>, Err&lt;E> 


### Approach 3: Try&lt;User>
Lets look at an example of using the `Try<User>` to model the success/failure. Similar to the trivial `Outcome` approach above, `Try` is implemented used 2 branches also ( Success / Failure ). The difference is that the failure branch is defaulted to store errors of type **Exception**. The success / failure will be represented as:
- success: Success( User( id = 1, .... ) )
- failure: Failure( Exception("Invalid email"))

```kotlin

// Create different types of exceptions
class InvalidParameterException(message:String): Exception(message)
class DuplicateEmailException(message:String): Exception(message)
class SecuredNameException(message:String): Exception(message)

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): Try<User> {
        return Try { 
            // check for valid name/email format
            if ( !validateEmail( email) ) 
                throw InvalidParameterException("invalid email")

            // check for duplicate email in system
            else if ( isDuplicate( email ) ) 
                throw DuplicateEmailException("duplicate email")
            
            // sample rule: prevent well known names for security reasons
            else if ( isReservedUserName( name ) ) 
                throw SecuredNameException("Reserved name")

            // return some result
            else 
                Success( createUser(name, email) )
        }
    }
}
```

**Notes**: 
1. `Try<T>`Defaults the Failure branch error value to **Exception**
2. `Try<T>` will require you to model your various errors as exceptions
3. It is generally a good practice to use exceptions for exceptional events, not for fairly common scenarios. 
4. If using Try for common scenarios, then building up the Exception via `Failure( InvalidParameterException("invaid email") )` is better than using than the more expensive ( and less idiomatic FP / functional programming approach ) approach of throwing that exceptions.


### Approach 4: Either&lt;RegError, User>
Lets look at an example of using the `Either<RegError, User>`. This approach at a high-level may look similar to using `Try`, except for differences in names. However, the major difference is that we supply the actual value of the error ( to make it what ever we want ), instead of defaulting it to **Exception** like we did in `Try`. The success / failure will be represented as:
- success: Right( User( id = 1, .... ) )
- failure: Left( "Invalid email" )

```kotlin

// Different types of Registration errors
sealed class RegError { 
    data class InvalidParameter(message:String)
    data class DuplicateEmail(message:String)
    data class SecuredName(message:String)
}

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): Either<RegError, User> {
        // check for valid name/email format
        return if ( !validateEmail( email) ) 
            Left( InvalidParameter("invalid email") )

        // check for duplicate email in system
        else if ( isDuplicate( email ) ) 
            Left( DuplicateEmail("duplicate email") )
        
        // sample rule: prevent well known names for security reasons
        else if ( isReservedUserName( name ) ) 
            Left( SecuredName("Reserved name") )

        // return some result
        else 
            Right ( createUser(name, email) )
    }
}
```

**Notes**: 
`Either<A,B>` is designed such that:
1. `Either<A,B>` is general purpose to store a **A** vs **B** relationship
2. `Either<A,B>` usage tends to model successes and failures 
3. The success branch is Right( as in correct ), and the failure branch is Left
4. Unlike `Try` we swap Exceptions for **sealed class** RegError 
5. Unlike `Try`, we avoid throwing Exceptions 


### Approach 5: Result&lt;User, RegError> ( from Rust )
Finally, lets look at an example of using the Rust like implementation of `Result<User, RegError>`. This approach is essentially the same as using `Either<RegError, User>` except for cosmetic differnences in names. The success / failure will be represented as:
- on success: Ok( User( id = 1, .... ) )
- on failure: Err( "Invalid email" )

```kotlin

// Different types of Registration errors
sealed class RegError { 
    data class InvalidParameter(message:String)
    data class DuplicateEmail(message:String)
    data class SecuredName(message:String)
}

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): Result<User, RegError> {
        // check for valid name/email format
        return if ( !validateEmail( email) ) 
            Err( InvalidParameter("invalid email") )

        // check for duplicate email in system
        else if ( isDuplicate( email ) ) 
            Err( DuplicateEmail("duplicate email") )
        
        // sample rule: prevent well known names for security reasons
        else if ( isReservedUserName( name ) ) 
            Err( ReservedName("Reserved name") )

        // return some result
        else 
            Ok ( createUser(name, email) )
    }
}
```

**Notes**: 
1. `Result<T,E>` is basically an `Either` with semantics for success/failure
2. `Result<T,E>` success branch is first and the value is represented as **T**
3. Feels more ergononmic than an `Either` simply due to naming conventions


### Overview
Using any of `Try`, `Either`, `Result` is very effective at modeling successes and failures as the solve the problems mentioned early on. But there are some problems and inefficienies that come up.

#### Fixes
1. **Reason for failure**: This is captured in the Failure branch
2. **Which HTTP Code to return ?**: If registration succeeds or fails, the UserAPI can check for the type Success branch to return a HTTP `200: Ok`. It can also check for the Failure branch exception and correctly return `400: Bad request` ( e.g. some bad / invaid data was supplied ), c) `500: Server error` 
3. **User Id not returned**: If registration succeeds, the user object is returned and the UserAPI can get the value out of the Success branch.

#### Problems
1. **Number of Error classes**: You would likely create many different types of error classes for different errors.
2. **Convert errors**: The UserAPI will have to convert the Failure( RegError ) to a compatible HTTP Code by checking for the different types of RegError. This could become unweidly overtime ( depending on how you structure your errors ), and may not scale elegantly. This is not just for registration, but likely for many other errors that will need to be modeled for other operations.


## Solution
If you code base starts to grow, you will likely have an explosion in the number of classes representing errors and the conversion of those classes to appropriate Http Codes for accuracy. The **Slate Kit Result<T,E>** is an attempt to solve these 2 problems with the addition of **Status Codes** in the **Result<T,E>** data structure.


### Status code 
A simple interface to represent a status code
```kotlin
interface Status {
    val code: Int
    val msg: String
}
```

### Status groups
Logical groups of successes and failures are created as `StatusGroup` using a sealed class
```kotlin
sealed class StatusGroup : Status {
    data class Succeeded  (val code: Int, val msg:String) :  StatusGroup()
    data class Pending    (val code: Int, val msg:String) :  StatusGroup()
    data class Denied     (val code: Int, val msg:String) :  StatusGroup()
    data class Ignored    (val code: Int, val msg:String) :  StatusGroup()
    data class Invalid    (val code: Int, val msg:String) :  StatusGroup()
    data class Errored    (val code: Int, val msg:String) :  StatusGroup()
    data class Unhandled  (val code: Int, val msg:String) :  StatusGroup()
}    
```

### Default codes
Specific status codes with numeric values and default descriptions are created. They are associated with their respective logical status group. These are defaulted / available from the implemenation but others can be used.
```kotlin
object Codes {

    // Success: 1000 + range ( partial list ) ...
    val SUCCESS         = Succeeded(1001, "Success")
    val CREATED         = Succeeded(1002, "Created")
    val UPDATED         = Succeeded(1003, "Updated")
    val PENDING         = Pending  (1008, "Pending")
    // more success codes here....

    // Invalid: 2000 + range ( partial list ) ...
    val IGNORED         = Ignored  (2001, "Ignored")         
    val BAD_REQUEST     = Invalid  (2002, "Bad Request")     
    val DENIED          = Denied   (2004, "Denied")          
    val UNAUTHORIZED    = Denied   (2009, "Unauthorized")    

    // Errors: 3000 + range ( partial list ) ...
    val NOT_FOUND       = Errored  (3001, "Not found") 
    val CONFLICT        = Errored  (3004, "Conflict")
    val ERRORED         = Errored  (3007, "Errored")         
    val UNEXPECTED      = Unhandled(3008, "Unexpected")

    // ... other code
}    
```

### Result<T,E>
This implementation of Result has an **status** field to hold options status codes that can be used to accurately track the type/category of error, while holding the actual Error value in the Failure branch.
```kotlin
sealed class Result<out T, out E> {
    /**
     * Optional status code is defaulted in the [Success] and [Failure]
     * branches using the predefined set of codes in [Codes]
     */
    abstract val status: Status

    /**
     * These are here for convenience both internally and externally
     */
    val success: Boolean get() = this is Success

    // other methods

    // map, fold, transform, and other operations, etc ...
}

/**
 * Success branch of the Result
 *
 * @param value  : Value representing the success
 * @param status : Optional status code as [Status]
 */
data class Success<out T>(
    val value: T,
    override val status: Status = Codes.SUCCESS
) : Result<T, Nothing>() {

    // overloaded constructors
}

/**
 * Failure branch of the result
 *
 * @param error  : Error representing the failure
 * @param status : Optional status code as [Status]
 */
data class Failure<out E>( 
    val error: E, 
    override val status: Status = Codes.ERRORED
) : Result<Nothing, E>() {

    // overloaded constructors
}
```

### Result Builders
Builder methods are available to create Result as an Success or Failure and with the desired error type. These allow for both convenience in building the results and also for ergonimics by providing a clear indication of what is built.

```kotlin
interface ResultBuilder<out E> {
    fun <T> success(value: T): Result<T, E> = Success(value)
    fun <T> success(value: T, msg: String): Result<T, E> = Success(value, msg)
    fun <T> success(value: T, code: Int): Result<T, E> = Success(value, code)
    fun <T> success(value: T, status: Status): Result<T, E> = Success(value, status)

    fun <T> invalid(): Result<T, E> = Failure(errorFromStr(null, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.INVALID), Codes.INVALID)

    // other code ...
}

object ResultErrBuilder : ResultBuilder<Err> {
    // implementations.
}
```

### Usage
Now with infrastructure for representing status, the custom Result&lt;T,E> implementation and the convenient builders, we can start modeling successes and failures using an even simpler and more informative approach.
```kotlin

import kiit.results.*
import kiit.results.builders.Results.*

// Service 
class UserService {

    // other code, setup, etc ...

    fun register( name: String, email: String ): Result<User, Err> {
        // check for valid name/email format
        return if ( !validateEmail( email) ) 
            invalid( "invalid email" )

        // check for duplicate email in system
        else if ( isDuplicate( email ) ) 
            errored( "duplicate email", Codes.CONFLICT )
        
        // sample rule: prevent well known names for security reasons
        else if ( isReservedUserName( name ) ) 
            denied( "can not use name" )

        // return some result
        else 
            success( createUser(name, email) )
    }
}
```

### Design 
1. A hybrid of Scala `Try<T>` with Rust `Result<T,E>`
2. Slate Kit Result<T,E> has branches Success<T> and Failure<E>
3. Enriches the Rust `Result<T,E>` data structure by adding a Status
4. Status is an interface with integer code, string msg ( message )
5. Sensible defaults for simple cases
6. Builder functions for custom cases
7. Http Support via conversion to Http codes



## Analysis
### Logical groups
Place holder 

### Representation
Place holder

### Allocation
Place holder

### Construction
Place holder

## Experiments
Place holder

## Conclusion
Place holder

## Bibliography
Place holder

## References
name | source | notes 
-----|------|-----
[Result](https://doc.rust-lang.org/std/result/) | Rust  | Works just like an Either
[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html) | Kotlin | Error type is Exception
[Result](https://github.com/apple/swift-evolution/blob/master/proposals/0235-add-result.md) | Swift | Error type is Error
[Either](https://www.scala-lang.org/api/2.12.4/scala/util/Either.html) | Scala | Either is a more generalized result

Template:
https://pdfs.semanticscholar.org/441f/ac7c2020e1c8f0d32adffca697bbb8a198a1.pdf
http://citeseer.ist.psu.edu/viewdoc/download?doi=10.1.1.115.1568&rep=rep1&type=pdf





