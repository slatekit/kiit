---
title: "Results"
date: 2019-11-17T23:55:43-05:00
section_header: Results
---

# Overview
**Result[T, E]** is a foundational component in **Slate Kit** for accurately modeling successes and failures using a functional approach to error handling. Result is not a brand new concept, it currently exists in various forms in other languages ( see below). In fact, the Result type in Slate Kit has been inspired by these and also by Google's GRPC codes ( See below ). The implementation here differs with the **flexibility of the Error type**, the addition of an optional **Status Code**, and support for **logical grouping of errors**. 
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>#</strong></td>
        <td><strong>Language</strong></td>
        <td><strong>Name</strong></td>
        <td><strong>Notes</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong>Kotlin</td>
        <td><a href="https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html">Result</a></td>
        <td>Error type is Exception and currently for internal use</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Swift</td>
        <td><a href="https://developer.apple.com/documentation/swift/result">Result</a></td>
        <td>Very similar conceptually</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>Rust</td>
        <td><a href="https://doc.rust-lang.org/std/result/">Result</a></td>
        <td>Branches are Ok / Err rather than Success / Failure</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong>Scala</td>
        <td><a href="https://www.scala-lang.org/api/2.12.4/scala/util/Either.html">Either</a></td>
        <td>General purpose use to represent A or B, branches are Left / Right</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong>Scala</td>
        <td><a href="https://www.scala-lang.org/api/2.12.4/scala/util/Try.html">Try</a></td>
        <td>Models successes and failures with exceptions</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong>Go</td>
        <td><a href="https://github.com/grpc/grpc/blob/master/doc/statuscodes.md">GRPC Codes</a></td>
        <td>Status codes in Slate Kit are inspired by these</td>
    </tr>
</table>
{{% break %}}

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/results#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/results#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/results#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/results#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/results#goals">Goals</a></strong></td>
        <td>Goals of this component and the problems it attempts to solve</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/results#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/results#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/results#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="arch/results#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>
{{% section-end mod="arch/results" %}}

# Status
This component is currently stable, has 0 dependencies and can be used for both **Android and Server**
{{% section-end mod="arch/results" %}}

# Install
Use the following settings in gradle for installing this component.
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-result:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="App"
    package="slatekit.result"
    jar="slatekit.result.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-result/src/main/kotlin/slatekit/results"
    gitAlias="slatekit/src/lib/kotlin/slatekit-result"
    url="arch/results"
    uses="n/a"
    exampleUrl="Example_Results.kt"
    exampleFileName="Example_Results.kt"
%}}
{{% section-end mod="arch/results" %}}

# Requires
This component has **0 dependencies** and does **NOT** use any other Slate Kit components.
{{% section-end mod="arch/results" %}}

# Sample
Here are some examples showing the usage of Result by creating, checking, and pattern matching the values.
Refer to the {{% sk-link-code component="examples" filepath="examples/Example_Results.kt" name="Example_Result.kt" %}} 
{{< highlight kotlin >}}
       
    import slatekit.results.*

    // Create success explicitly
    val start:Result<Int,Err> = Success(10 )

    // Properties
    println(start.success)     // true
    println(start.status.code) // Codes.SUCCESS.code
    println(start.status.msg)  // Codes.SUCCESS.msg

    // Safely operate on values with map/flatMap
    val addResult = start.map { it + 1 }
    val subResult = start.flatMap { Success(it - 1 ) }

    // Check values
    println( addResult.contains(11) )
    println( addResult.exists{ it == 11 } )

    // Get values
    println( addResult.getOrNull() )
    println( addResult.getOrElse { 0 })

    // On conditions
    subResult.onSuccess { println(it) } // 9
    subResult.onFailure { println(it) } // N/A

    // Pattern match on branches ( Success / Failure )
    when(addResult) {
        is Success -> println("Value is : ${addResult.value}") // 11
        is Failure -> println("Error is : ${addResult.error}") // N/A
    }

    // Pattern match on status
    when(addResult.status) {
        is Status.Succeeded  -> println(addResult.msg)
        is Status.Pending    -> println(addResult.msg)
        is Status.Denied     -> println(addResult.msg)
        is Status.Invalid    -> println(addResult.msg)
        is Status.Ignored    -> println(addResult.msg)
        is Status.Errored    -> println(addResult.msg)
        is Status.Unexpected -> println(addResult.msg)
    }
        
{{< /highlight >}}
{{% section-end mod="arch/results" %}}

# Goals
The design of this Result type invovles achieving these specific goals.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Accuracy of models</strong></td>
        <td>Accurate successes / failures, and functional error-handling with various ways to pattern match</td>
    </tr>
    <tr>
        <td><strong>2. Flexible error type</strong> </td>
        <td>Error type on the Failure branch can be anything, Exception, Err, String. Use exceptions or default types</td>                     
    </tr>
    <tr>
        <td><strong>3. Support for Status Codes</strong></td>
        <td>Logical groups of status to categories errors, these can also be converted to Http codes</td>
    </tr>
</table>
{{% section-end mod="arch/results" %}}

# Concepts
These are the main concepts / terms to know for using this component. All of these are explained further below in the details section. You can find links to the source code here.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Concept</strong></td>
        <td><strong>Usage</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Result.kt" name="Result" %}} </td>
        <td><strong>required</strong></td>
        <td>Main type for modeling successes and failures.</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Result.kt" name="Success" %}} </td>
        <td><strong>required</strong></td>
        <td>Success branch/sub-class of Result storing the value with optional status code</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Result.kt" name="Failure" %}} </td>
        <td><strong>required</strong></td>
        <td>Failure branch/sub-class of Result storing the error with optional status code.</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Err.kt" name="Err" %}}</td>   
        <td>Optional</td>
        <td>Flexible representation of errors. E.g. You can use the **Err** interface, default implementations or **Exceptions**</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}}</td>
        <td>Optional</td>
        <td>Logical error groups to reduce boiler plate and offer HTTP compatibility. E.g. **Succeeded, Denied, Invalid, etc**</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Codes.kt" name="Codes" %}}</td>
        <td>Optional</td>
        <td>Default implementations of general purpose status codes</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/Aliases.kt" name="Aliases" %}}</td>
        <td>Optional</td>
        <td>Type aliases to simplify the Result from 2 type parameters to 1. E.g. **Outcome, Try, Notice, Validated**</td>
    </tr>
    <tr>
        <td>{{% sk-link-code component="result" filepath="results/builders/Builder.kt" name="Builders" %}}</td>
        <td>Optional</td>
        <td>Convenient methods to build errors such as **Outcomes.denied**</td>
    </tr>
</table>
{{% section-end mod="arch/results" %}}

# Features
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Create</strong></td>
        <td>Create successes, failures in various ways</td>
        <td><a href="arch/results/#creation" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Get</strong></td>
        <td>Get values safely using available operations</td>
        <td><a href="arch/results/#creation" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>3. Check</strong></td>
        <td>Check for values or use pattern matching</td>
        <td><a href="arch/results/#creation" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Errors</strong></td>
        <td>Define your error types or use pre-built ones</td>
        <td><a href="arch/results/#errors" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Status</strong></td>
        <td>Use status codes for logical grouping</td>
        <td><a href="arch/results/#errors" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Aliases</strong></td>
        <td>Type aliases to default the error type</td>
        <td><a href="arch/results/#errors" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. Builders</strong> </td>
        <td>Several easy ways to build success or different category of errors</td> 
        <td><a href="arch/results/#builders" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>8. Outcome</strong></td>
        <td>A type alias for **Result[T, Err]** where Err is an error interface</td>
        <td><a href="arch/results/#outcome" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>9. Try</strong></td>
        <td>A type alias for **Result[T, Exception]** to work with Exceptions</td>
        <td><a href="arch/results/#try" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>10. HTTP Support</strong></td>
        <td>Convert Successes / Failures to compatible HTTP codes</td>
        <td><a href="arch/results/#http" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/results" %}}


## Creation {#creation}
There various ways to create successes / failures, and these will be similar to Kotlin / Swift Result type usage. 
{{< highlight kotlin >}}
      
    import slatekit.results.*
    
    // Success: Straight-forward
    val result = Success(42)

    // Success referenced as base type Result<Int, Err>
    val result1a: Result<Int, Err> = Success(42)

    // Success created with status codes / messages
    val result1b = Success(42, status = Codes.SUCCESS)
    val result1c = Success(42, msg = "Successfully processed")
    val result1d = Success(42, msg = "Successfully processed", code = 200)

    // Failure
    val result1e = Failure(Err.of("Invalid email"))

    // Failure referenced as base type Result<Int, Err>
    val result1f: Result<Int, Err> = Failure(Err.of("Invalid email"))

    // Failure created with status codes / messages
    val result1g = Failure(Err.of("Invalid email"), status = Codes.INVALID)
    val result1h = Failure(Err.of("Invalid email"), msg = "Invalid inputs")
    val result1i = Failure(Err.of("Invalid email"), msg = "Invalid inputs", code = Codes.INVALID.code)
     
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Get {#values}
Result offers the typical functional ways to safely get the value
{{< highlight kotlin >}}
      
    import slatekit.results.*

    // Create
    val result:Result<Int, Err> = Success(42)

    // Get value or default to null
    val value1:Int? = result.getOrNull()

    // Get value or default with value provided
    val value2:Int = result.getOrElse { 0 }

    // Map over the value
    val op1 = result.map { it + 1 }

    // Flat Map over the value
    val op2 = result.flatMap { Success(it + 1 ) }

    // Fold to transform both the success / failure into something else ( e.g. string here )
    val value3:String = result.fold({ "Succeeded : $it" }, {err -> "Failed : ${err.msg}" })

    // Get value if success
    result.onSuccess { println("Number = $it") }

    // Get error if failure
    result.onFailure { println("Error is ${it.msg}") }

    // Pattern match
    when(result) {
        is Success -> println(result.value)  // 42
        is Failure -> println(result.error)  // Err
    }

{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Check {#check}
There are 3 ways to check / pattern match the result type and its actual branch/values. These range from coarse to fine grained matches depending on the situation. Typically though, as with other implementations you just need to check for Success / Failure. However, you can also check the categories of errors or the specific error types.
{{< highlight kotlin >}}
     
    import slatekit.results.*

    val result:Result<Int,Err> = Success(42)
    
    // Check if the value matches the criteria
    result.exists { it == 42 } // true
    
    // Check if the value matches the one provided
    result.contains(2)        // false

    // Pattern match scenario 1: "Top-Level" on Success/Failure (Binary true / false )
    when(result) {
        is Success -> println(result.value)  // 42
        is Failure -> println(result.error)  // Err
    }

    // Pattern match scenario 2: "Mid-level" on Status ( 7 logical groups )
    // NOTE: The status property is available on both the Success/Failure branches
    when(result.status) {
        is Status.Succeeded  -> println(result.msg) // Success!
        is Status.Pending    -> println(result.msg) // Success, but in progress
        is Status.Denied     -> println(result.msg) // Security related 
        is Status.Invalid    -> println(result.msg) // Bad inputs / data
        is Status.Ignored    -> println(result.msg) // Ignored for processing
        is Status.Errored    -> println(result.msg) // Expected errors
        is Status.Unexpected -> println(result.msg) // Unexpected errors
    }

    // Pattern match scenario 3: "Low-Level" on numeric code
    when(result.status.code) {
        Codes.SUCCESS.code    -> "OK"
        Codes.QUEUED.code     -> "Pending"
        Codes.UPDATED.code    -> "User updated"
        Codes.DENIED.code     -> "Log in again"
        Codes.DEPRECATED.code -> "No longer supported"
        Codes.CONFLICT.code   -> "Email already exists"
        else                  -> "Other!!"
    }
     
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Errors {#errors}
Unlike Kotlin / Swift Result error types, the {{% sk-link-code component="result" filepath="results/Err.kt" name="Err" %}}  type on the Failure branch can be anything. It is not defaulted to **Exception**. In this way, this is conceptually similar to an Scala / Haskell Either. 
{{< highlight kotlin >}}
    
    import slatekit.results.Err
    import slatekit.results.ErrorList

    // Simple string
    val err1 = Err.of("Invalid email")

    // Exception
    val err2 = Err.of(Exception("Invalid email"))

    // Field: name / value
    val err3 = Err.of("email", "abc123@", "Invalid email")

    // String message from status code
    val err4 = Err.of(Codes.INVALID)

    // List of error strings
    val err5 = Err.of(listOf(
        "username must be at least 8 chars", 
        "username must have 1 UPPERCASE letter"), 
    "Username is invalid")

    // List of Err types
    val err6 = ErrorList(listOf(
            Err.of("email", "abc123 is not a valid email", "Invalid email"),
            Err.of("phone", "123-456-789 is not a valid U.S. phone", "Invalid phone")
    ), "Please correct the errors")

    // Create the Failure branch from the errors
    val result:Result<UUID, Err> = Failure(err6)
     
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Status {#status}
The distinguishing feature of Slate Kit Result is the introduction of a {{% sk-link-code component="result" filepath="results/Status.kt" name="Status" %}} Code component on the Result type. The status is applicable for both the Success / Failure branch. There are also default codes for most common errors. {{% sk-link-code component="result" filepath="results/Codes.kt" name="Codes" %}}. The Status Codes provide a few main benefits:
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>1. Categorization</strong></td>
        <td>Categorize errors into logical groups.</td>
        <td>E.g. Succeeded, Invalid, Denied, etc.</td>
    </tr>
    <tr>
        <td><strong>2. Reduce boiler-plate</strong></td>
        <td>Removes boiler-plate code of creating small custom error types.</td>
        <td>E.g. Outcomes.denied("No access to resource")</td>
    </tr>
    <tr>
        <td><strong>3. Conversion</strong></td>
        <td>Provides a reasonable way to conver to HTTP codes</td>
        <td>E.g. Succeeded -> HTTP 200 range</td>
    </tr>
</table>
{{< highlight kotlin >}}

    package slatekit.results

    sealed class Status {
        abstract val code: Int
        abstract val msg: String
    
        data class Succeeded(override val code: Int, override val msg: String) : Status()
        data class Pending  (override val code: Int, override val msg: String) : Status()
        data class Denied   (override val code: Int, override val msg: String) : Status()
        data class Ignored  (override val code: Int, override val msg: String) : Status()
        data class Invalid  (override val code: Int, override val msg: String) : Status()
        data class Errored  (override val code: Int, override val msg: String) : Status()
        data class Unexpected(override val code: Int, override val msg: String) : Status()
    }

{{< /highlight >}}


{{% feature-end mod="arch/results" %}}

## Aliases {#aliases}
There are a few type {{% sk-link-code component="result" filepath="results/Aliases.kt" name="Aliases" %}}  for making it easier to work with error types and to simplify the Result type from 2 type parameters down to 1. The aliases are supplemented with builder functions to easily create the result types.
{{< highlight kotlin >}}
    import slatekit.results.* 
    import slatekit.results.builders.Outcomes
    import slatekit.results.builders.Tries
    
    // Try<T> = Result<T, Exception>
    val res1 = Tries.attempt { "1".toInt() }

    // Outcome<T> = Result<T, Err>
    val res2 = Outcomes.of { "1".toInt() }

    // Notice<T> = Result<T, String>
    val res3 = Notices.notice { "1".toInt() }

    // Validated<T> = Result<T, ErrorList>
    val res4:Validated<String> = Failure(ErrorList(listOf(
            Err.of("email", "abc123 is not a valid email", "Invalid email"),
            Err.of("phone", "123-456-789 is not a valid U.S. phone", "Invalid phone")
    ), "Please correct the errors"))
      
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}


## Builders {#aliases}
Typically, errors fall into various categories, and you may want to use specific messages, or other data to build up the errors. In order to facilitate these there are {{% sk-link-code component="result" filepath="results/builders/Builder.kt" name="Builders" %}} available for convenience to construct appropriate error types.
{{< highlight kotlin >}}

    ...
    fun <T> denied(): Result<T, E> = Failure(errorFromStr(null, Codes.DENIED), Codes.DENIED)
    fun <T> denied(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.DENIED), Codes.DENIED)
    fun <T> denied(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.DENIED), Codes.DENIED)
    fun <T> denied(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.DENIED), Codes.DENIED)

    fun <T> ignored(): Result<T, E> = Failure(errorFromStr(null, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.IGNORED), Codes.IGNORED)

    fun <T> invalid(): Result<T, E> = Failure(errorFromStr(null, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.INVALID), Codes.INVALID)

    ...

{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Outcome {#outcome}
The **Outcome** is simply a type alias for **Result[T, Err]** and allows you to use Result as **Outcome[T]**. This also comes with a {{% sk-link-code component="result" filepath="results/builders/Outcomes.kt" name="Outcomes" %}} builder to construct Successes / Failures easily.
{{< highlight kotlin >}}
    import slatekit.results.* 
    import slatekit.results.builders.Outcomes

    // Outcome<Int> = Result<Int, Err>
    val res1 = Outcomes.success(1, "Created User with id 1")
    val res2 = Outcomes.denied<Int>("Not authorized to send alerts")
    val res3 = Outcomes.ignored<Int>("Not a beta tester")
    val res4 = Outcomes.invalid<Int>("Email is invalid")
    val res5 = Outcomes.conflict<Int>("Duplicate email found")
    val res6 = Outcomes.errored<Int>("Phone is invalid")
    val res7 = Outcomes.unexpected<Int>("Unable to send confirmation code")
    
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Try {#try}
The **Try** is simply a type alias for **Result[T, Exception]** and allows you to use Result as **Try[T]**. This also comes with a {{% sk-link-code component="result" filepath="results/builders/Tries.kt" name="Tries" %}} builder to construct Successes / Failures with exceptions. This is quite similar to Scala Try. Also, while functional error-handling is prioritized in Slate Kit, its not a dogmatic / absolute approach and exceptions can be used where appropriate.
{{< highlight kotlin >}}
     
    import slatekit.results.* 
    import slatekit.results.builders.Tries

    // Try<Long> = Result<Long, Exception>
    val converted1:Try<Long> = Tries.attempt { "1".toLong() }

    // DeniedException will checked and converted to Status.Denied
    val converted2:Try<Long> = Tries.attemptWithStatus<Long> {
        throw DeniedException("Token invalid")
    }

{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

## Http {#http}
Status codes not only serve to logically categories successes/failures but become a natural and easy way to convert results to platform/protocol specific errors. There is a default Err to HTTP status code converter available in {{% sk-link-code component="result" filepath="results/Codes.kt" name="Codes" %}}

{{< highlight kotlin >}}
     
    // Simulate a denied exception ( Security related )
    val denied:Outcome<Long> = Outcomes.denied("Access token has expired")
    
    // Convert it to HTTP
    // This returns back the HTTP code + original Status
    val code:Pair<Int, Status> = Codes.toHttp(denied.status)
    println(code.first) // 401
     
{{< /highlight >}}
{{% feature-end mod="arch/results" %}}

{{% section-end mod="arch/results" %}}

