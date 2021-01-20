# About
More information on this component at https://www.slatekit.com/arch/results/

![image](https://www.slatekit.com/assets/app/media/arch/slatekit-result.png)


# Result&lt;T,E>
Models successes and failures with optional status codes. 
This has **a few significant differences** from other implementations of `Result` in other languages ( Rust, Kotlin, Swift ), and other generic alternatives like `Either` ( Scala, Haskell ). See the **Design/Concepts** sections below for more info. 

# Install
The current default implementation is in Kotlin
```groovy
repositories {
    maven { url = 'http://dl.bintray.com/codehelixinc/slatekit' }
}

dependencies {
    compile 'com.slatekit:slatekit-results:0.9.8'
}
```

# Design
These were some of the design goals while initially building and incrementally improving the Result type.

num | category | desc
-----|------|------
1 | Generic      | Generic modeling of successes / failures in code ( e.g. specialized **Either** )
2 | Status Codes | **Optional** support for status codes ( integer code + string message )
3 | Custom Error | Set a custom error type (E) or use our **Err** trait or **Exception**
4 | Aliases      | Aliases for Result to default the error type ( **Notice**, **Outcome**, **Try** )
5 | Full-Stack   | Usable across any layer of an application ( Client, API, Service, Utils )
6 | Defaults     | Sensible defaults for simple usecases with support for more complex ones
7 | Functional   | Functional design for safe operations and chaining ( map, flatMap, fold, etc )
8 | Builders     | Several convenient builder functions available in the **Results** class
9 | HTTP Support | Designed to be easily convertable / compatible with http status codes


# Usage
Some examples of using the type. Refer to the Kotlin directory, [Sample App](kotlin/results/src/doc_sample.md), [Sample Source Code](kotlin/results/src/test/kotlin/samples/SampleApp.kt)

```kotlin
    // CREATE: Create the success/failures in different ways:
    // 1. Using Success/Failure branch of Result with optional code and message
    // 2. Using Results.x builder methods ( success, invalid, ignored, errored, unexpected )
    // 3. Using Result.of companion function which sets the Failure type to [Err]
    // 4. Using Result.attempt companion function which sets the Failure type to [Exception]
    val r1 : Result<Int,Err> = Success(1, msg = "example 1", code = 1000)
    val r2 : Result<Int,Err> = Results.success(1)
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
```

# Concepts
This library is fairly straight-forward and small, but here are core concepts and terms.

### Core
These are the core classes you need to use this library

name | usage | desc
------|-----|-----
[Result](kotlin/results/src/main/kotlin/slatekit/results/Result.kt)  | core | Main type modeling successes and failures ( via the Success/Failure sub-classes below ) 
[Success](kotlin/results/src/main/kotlin/slatekit/results/Result.kt) | core | The success branch of Result which stores the value and optional code(int), message(string)
[Failure](kotlin/results/src/main/kotlin/slatekit/results/Result.kt) | core | The failure branch of Result which stores the error and optional code(int), message(string)
[Err](kotlin/results/src/main/kotlin/slatekit/results/Err.kt)    | core | Marker interface to model any errors
[Status](kotlin/results/src/main/kotlin/slatekit/results/Status.kt)    | core | Interface(s) representing a status code with fields code, msg

### Optional
These are provided as `extras` for convenience, default implementations of interfaces, and builder functions

name | usage | desc
------|-----|-----
[Aliases](kotlin/results/src/main/kotlin/slatekit/results/Aliases.kt) | alias | Aliases for Result to reduce the 2 parameter types to 1 with defaulted error type. Notice&lt;T> is Result&lt;T,String>, Outcome&lt;T> is Result&lt;T,Err>, Try&lt;T> is Result&lt;T,Exception>
[Results](kotlin/results/src/main/kotlin/slatekit/results/Results.kt) | optional |Object with functions to build either a Success or Failure using the different groups of status/error codes from the StatusCode sealed class. These are here for convenience if you want to use the predefined set of Status/Err types.
[Status](kotlin/results/src/main/kotlin/slatekit/results/Status.kt)  | optional | Default implementation of Status codes as a Sealed class with sub-classes ( Succeeded, Invalid, Ignored, Errored, Unhandled ) to represent a status and logical groups of different types of Statuses
[Codes](kotlin/results/src/main/kotlin/slatekit/results/Codes.kt)   | optional |Default set of status codes from this library ( which are convertable/compatible with http status codes )



# Credits
Result was inspired by the following concepts, approaches and implementations.

name | source | notes 
-----|------|-----
[Try](https://www.scala-lang.org/api/2.12.4/scala/util/Try.html)    | Scala | Modeled after Try with Success/Failure branches
[Result](https://doc.rust-lang.org/std/result/) | Rust  | Inspired some of the operations on Result
[Either](https://www.scala-lang.org/api/2.12.4/scala/util/Either.html) | Scala | Result is basically a specialized either
[GRPC](https://github.com/grpc/grpc/blob/master/doc/statuscodes.md) | Go | Inspired the setup of standardized status codes

# History
Result started a long time ago and has slowly transformed into a pivotal component in day to day usage.

date | source | notes 
-----|------|-----
~2012 | C#     | Originated as a BoolResult&lt;T> ( combined a boolean success, string message, with a value )
~2015 | Scala  | Recreated as a Result&lt;T> with added integer code
~2016 | Scala  | Redesigned as Result&lt;T,Exception> after Scala Try with default error type of exception
~2017 | Kotlin | Recreated as a Result&lt;T,E> to make the Error type customizable 
~2017 | Kotlin | Enhanced support for status codes with ( defaults and compatibility with Http codes )
~2018 | Kotlin | Refined the design / API using Rust Result and Scala Either as guidelines
~2018 | Kotlin | Refined the design / API to support sensible defaults
~2019 | Kotlin | Refined the design / API to to support different types of Errors with defaults
~2019 | Kotlin | Extracted Result&lt;T,E> from www.slatekit.com to make it a 1st level resuable component

# Upcoming
This data type will soon be available in other languages in this repo.

lang | packaging | version | notes 
-----|------|-----|-----
Kotlin | gradle  | n/a   | available ( default implementation above )
Node   | npm     | n/a   | coming soon 


# References
The Result type (IMO) is becoming a standard as it is now available in many languages in one form or another.

name | source | notes 
-----|------|-----
[Result](https://doc.rust-lang.org/std/result/) | Rust  | Works just like an Either
[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html) | Kotlin | Error type is Exception
[Result](https://github.com/apple/swift-evolution/blob/master/proposals/0235-add-result.md) | Swift | Error type is Error
[Either](https://www.scala-lang.org/api/2.12.4/scala/util/Either.html) | Scala | Either is a more generalized result


# Technical 
A technical paper will be available soon that discusses the research / analysis / design and implementation considerations while working on this project.
Template: https://pdfs.semanticscholar.org/441f/ac7c2020e1c8f0d32adffca697bbb8a198a1.pdf

