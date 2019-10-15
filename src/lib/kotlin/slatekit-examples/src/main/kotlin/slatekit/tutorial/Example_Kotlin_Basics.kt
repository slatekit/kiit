/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.tutorial


// 1. Import 1 item from a package


// 2. Import all items from a pacakge using "*"


// 4. Import 1 method ( string6 ) from an Object ( Random )
// NOTE: An object is essentially a Singleton class
// that serves as a "module" containing 1 or more functions
import slatekit.common.Random.string6
import slatekit.functions.cmds.Command
import slatekit.results.Success
import slatekit.results.Try
import java.time.LocalDateTime


fun main(args: Array<String>): Unit {
    println(string6())
}


/**
 * Created by kreddy on 4/4/2016.
 */
class Example_Kotlin_Basics : Command("auth") {

    override fun execute(request:CommandRequest): Try<Any> {
        showNullUsage()
        showEnums()
        showWhen()
        showAggregates()
        showLoops()
        testMaps()
        testLists()
        return Success("")
    }


    fun showVars(): Unit {
        var name = "john doe"
        var age = 35
        var account = 1234567890L
        var isActive = false
        var salary = 20.5
        var date = LocalDateTime.now()

        println(name)
        println(age)
        println(account)
        println(isActive)
        println(salary)
        println(date)
    }


    fun showVals(): Unit {
        val name = "john doe"
        val age = 35
        val account = 1234567890L
        val isActive = false
        val salary = 20.5
        val date = LocalDateTime.now()
        val user = User("john doe", true)
        val items = listOf("a", "b", "c", "d", "e")
        val lookup = mapOf("a" to 1, "b" to 2)

        println(name)
        println(age)
        println(account)
        println(isActive)
        println(salary)
        println(date)
    }

    data class User(val name:String, val active:Boolean)


    fun showTypes(): Unit {
        // Inferred
        val name1     = "john doe"
        val age1      = 35
        val account1  = 12345678901L
        val isActive1 = false
        val salary1   = 20.5
        val date1     = LocalDateTime.now()
        val pair1     = Pair("john doe", 35)
        val range1    = 1..4
        val list1     = listOf("a", "b", "c")
        val map1      = mapOf("a" to 1, "b" to 2)

        // Explicit
        val name2    : String            = "john doe"
        val age2     : Int               = 35
        val account2 : Long              = 12345678901L
        val isActive2: Boolean           = false
        val salary2  : Double            = 20.5
        val date2    : LocalDateTime     = LocalDateTime.now()
        val pair2    : Pair<String, Int> = Pair("john doe", 35)
        val range2   : IntRange          = 1..4
        val list2    : List<String>      = listOf("a", "b", "c")
        val map2     : Map<String,Int>   = mapOf("a" to 1, "b" to 2)

        // Nullable via "?"
        val name3    : String?            = "john doe"
        val age3     : Int?               = 35
        val account3 : Long?              = 12345678901L
        val isActive3: Boolean?           = false
        val salary3  : Double?            = 20.5
        val date3    : LocalDateTime?     = LocalDateTime.now()
        val pair3    : Pair<String, Int>? = Pair("john doe", 35)
        val range3   : IntRange?          = 1..4
        val list3    : List<String>?      = listOf("a", "b", "c")
        val map3     : Map<String,Int>?   = mapOf("a" to 1, "b" to 2)

        println(name2)
        println(age2)
        println(account2)
        println(isActive2)
        println(salary2)
        println(date2)
        println(pair2)
        println(range2)
    }



    data class Hero(val name:String, val universe:String, val identity:String?)

    // Shows usage of null, safe access "?" and elvis "?:"
    // operator on basic strings
    fun testNullsWithBasicType():Unit {

        // EXAMPLE 1: nullable/non-nullable basic types
        // Non-nullable string: can not assign null
        val character: String = "batman"

        // Nullable string via "?", can assign null
        val realName: String? = null

        // 1. Smart conditions
        // The compiler will know that the check for null was
        // already done so the else block accessing realName is ok
        if (realName == null) {
            println("identity: unknown")
        }
        else {
            println("identity: " + realName.toUpperCase())
        }

        // 2. safely accessing members using "?"
        println("We may NOT have the identity.")
        println("UPPERCASE: " + realName?.toUpperCase())
        println("lowercase: " + realName?.toLowerCase())

        // 3. using let to get non-null value
        // to avoid repeated use of "?" when accessing members
        realName?.let { name ->
            println("We have the identity! Access properties without constant checks via '?'")
            println("IN UPPERCASE: " + name.toUpperCase())
            println("in lowercase: " + name.toLowerCase())
        }

        // 4. get value or default using "?:"
        println("Real name or else: " + (realName ?: "unknown"))
        println("Real name uppercase or else: " + (realName?.toUpperCase() ?: "unknown"))
    }


    // Shows usage of null, safe access "?" and elvis "?:"
    // operator on class properties
    fun testNullsWithObjectType():Unit {
        val char1:Hero? = Hero("Captain America", "Marvel", "steve rogers")

        // 1. Smart conditions
        // The compiler will know that the check for null was
        // already done so the else block accessing name is ok
        if ( char1 == null ){
            println("character: not supplied")
        } else {
            println(char1.name)
        }

        // 2. safely accessing members using nested "?"
        println(char1?.name?.toUpperCase())
        println(char1?.name?.toLowerCase())

        // 3. using let to get non-null value
        // to avoid repeated use of "?" when accessing members
        char1?.let { hero ->
            println("We have the identity! Access properties without constant checks via '?'")
            println(hero.name.toUpperCase())
            println(hero.universe.toLowerCase())
        }

        // 4. get value or default using "?:"
        println("Real name or else: " + (char1 ?: "unknown" ))
        println("Real name uppercase or else: " + (char1?.name?.toUpperCase() ?: "unknown"))
    }


    fun showNullUsage():Unit {
        testNullsWithBasicType()
        testNullsWithObjectType()
    }


    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR, FATAL
    }


    enum class LogLevel2(val level:Short) {
        DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5)
    }


    fun showEnums():Unit {
        println(LogLevel.DEBUG)
        println(LogLevel.DEBUG.name)
        println(LogLevel.DEBUG.ordinal)

        println(LogLevel2.WARN)
        println(LogLevel2.WARN.name)
        println(LogLevel2.WARN.ordinal)
        println(LogLevel2.WARN.level)
    }


    fun showStrings(): Unit {

        // 1. Declaring strings
        val name = "john doe"


        // 2. Adding strings together
        val fullName = "Clark" + ", Kent"


        // 3. Getting a char position in a string e.g. 1st pos
        var c1 = "Batman"[0]


        // 4. Convert strings to other types
        val age = "30".toInt()
        val isActive = "true".toBoolean()
        val salary = "20.5".toDouble()


        // 5. String interpolation
        val account = 1234567890L
        val date = LocalDateTime.now()

        val info1 = "$name $age $account $isActive $salary $date"
        println(info1)


        // 6. String interpolation ( with braces )
        // Use this explicit approach when you dont have spaces
        val info2 = "${name}${age}${account}${isActive}${salary}${date}"
        println(info2)


        // 7. String interpolation with more expressions
        val info3 = "${name} salary is now ${salary + 200} start on ${date}"
        println(info3)
    }


    fun showStringsMultiline(): Unit {

        // 1. Space / indentation is preserved
        val example1 = """ app.users.activate
                      - userid: number ( required )
                      - phone : string ( required )
                      - email : string ( required )
                   """

        // 2. Indentation is stripped and next line starts after "|"
        val example2 = """ app.users.activate
                     | - userid: number ( required )
                     | - phone : string ( required )
                     | - email : string ( required )
                     | """.trimIndent()

        // 3. Indentation is stripped and next line starts after "#"
        val example3 = """ app.users.activate
                     # - userid: number ( required )
                     # - phone : string ( required )
                     # - email : string ( required )
                     # """.trimMargin("#")
    }


    fun showConditions(): Unit {
        // 1. if only
        if (true) println("true")

        // 2. No braces
        val code = 200
        if (code == 200)
            println("ok")
        else if (code == 404)
            println("not found")
        else
            println("unknown")

        // 3. If conditions are expressions!
        // This means they return a value
        val message =
                if (code == 200) {
                    "ok"
                }
                else if (code == 404) {
                    "not found"
                }
                else {
                    "unknown"
                }
        println(message)

        // 4. There is no ternary "? :" operation, but in kotlin,
        // you do not need it as a simple if/else is sufficient
        val status = if (code == 200) "ok" else "unknown"
        println(status)
    }


    fun showLoops(): Unit {

        // 1. for loop inclusive ( 5 included )
        for (a in 1..5) {
            println("Value of a: " + a)
        }

        // 2. for loop exclusive ( 5 excluded )
        for (a in 1..5 - 1) {
            println("Value of a: " + a)
        }

        // 3. for loop with variable
        val start = 1
        val end = 5
        for (a in start..end) {
            println("Value of a: " + a)
        }

        // 4. for loop over collection
        val words = listOf("a1", "b2", "c3", "d4", "e5")
        for (word in words) {
            println(word)
        }

        // 5. while loop
        var a = 0
        while (a < 4) {
            println("Value of a: " + a)
            a += 1
        }
    }


    fun showLoopsFunctional(): Unit {


        // NOTE: There are multiple ways to iterate over a collection
        // without using traditional "while" or "for" loops with variables
        val items = listOf("1", "2", "3")

        // Case 1: For each variable name
        items.forEach { item -> println(item) }

        // Case 2: For each using auto-supplied "it" variable
        items.forEach { println(it) }

        // Case 3: For each with lambda
        items.forEach(::println)

        // Case 4: For each with index
        items.forEachIndexed { index, s -> print(" " + items[index]) }
        println()

        // Case 5: Using a range
        4.until(6).forEach { v -> print(" " + v) }

    }


    fun showAggregates(): Unit {
        val items = listOf("1", "2", "3")

        // Case 1: apply function to each
        // Prints:
        // 1
        // 2
        // 3
        items.forEach(::println)

        // Case 2: map strings to new list of numbers
        // Result: List[Int](1, 2, 3)
        val nums = items.map { it.toInt() }
        println(nums)

        // Case 3: filter ( find items > 2 )
        val filtered = items.filter { it -> it.toInt() > 2 }
        println(filtered)

        // Case 4: reduce to 1 value
        // Result: "1:2:3"
        val total = items.reduce { a, b -> a + ":" + b }
        println("reduce: '$total'")

        // Case 5a: fold to single value from the Left side
        // Result: "LEFT,1,2,3"
        val totalLeft = items.fold("LEFT", { acc, text -> acc + "," + text })
        println("fold left: " + totalLeft)

        // Case 5b: fold to single value from the Right side
        // Result:  "1,2,3,RIGHT"
        val totalRight = items.foldRight("RIGHT", { acc, text -> acc + "," + text })
        println("fold right: " + totalRight)

        // Case 6: max
        // Result: 3
        val max = items.maxBy(String::toInt)
        println("max: " + max)

        // Case 7: min
        // Result: 1
        val min = items.minBy(String::toInt)
        println("min: " + min)
    }


    // 1. function does not return anything.
    fun test1(name: String, account: Long, isActive: Boolean): Unit {
        println(name)
    }

    // 2. function with default values for parameters
    fun test2(name: String, account: Long = 0L, isActive: Boolean = true): Unit {
        println(name)
    }

    // 3. function with return value ( do not need a return keyword )
    // Last expression is assumed to be the return value
    fun test3(name: String, account: Long = 0L, isActive: Boolean = true): String {
        if (account == 0L)
            return "user : $name $account $isActive"

        return "existing user: $name"
    }

    // 4. function with functions as parameters,
    // Last expression is assumed to be the return value
    fun test4(name: String,
              helloCallBack1: () -> Unit,
              helloCallBack2: (String) -> Unit,
              helloCallBack3: (String, String) -> String): Unit {

        // 1. first function takes 0 parameters, and returns nothing
        helloCallBack1()

        // 2. second function takes 1 paramter, and returns nothing
        helloCallBack2("hello")

        // 3. third function takes 2 parameter and returns a value
        val result = helloCallBack3("hey", "hello")
        println(result)
    }


    fun testFunctions(): Unit {

        // 1. function with parameters
        test1("john doe", 0, true)

        // 2. function with parameters with default values
        test2("john doe")

        // 3. function with parameters with default values + return value
        val result1 = test3("john doe", 123456789123L)
        println(result1)

        // 4. function with functions as paramters (without braces )
        val result2 = test4("john doe",
                { },
                { greeting1 -> println(greeting1 + " john") },
                { greeting2, greeting3 -> "$greeting2 and $greeting3 john" }
        )
        println(result2)
    }


    fun showMultilineStrings(): Unit {

        // 1. Space / indentation is preserved
        val example1 = """ app.users.activate
                      - userid: number ( required )
                      - phone : string ( required )
                      - email : string ( required )
                   """

        // 2. Indentation is stripped and next line starts after "|"
        val example2 = """ app.users.activate
                     | - userid: number ( required )
                     | - phone : string ( required )
                     | - email : string ( required )
                     | """

        // 3. Indentation is stripped and next line starts after "#"
        val example3 = """ app.users.activate
                     # - userid: number ( required )
                     # - phone : string ( required )
                     # - email : string ( required )
                     # """

        println(example1)
        println(example2)
        println(example3)
    }


    fun testMaps(): Unit {
        // 1. Declare empty immutable list
        val items1 = mapOf<String, Int>()

        // 2. Declare immutable list with pairs of items
        val items2 = mapOf<String, Int>("a" to 1, "b" to 2, "c" to 3)

        // 3. Get the size
        println(items2.size)

        // 4. Get an item by key
        println(items2["a"])

        // 5. Get all keys
        println(items2.keys)

        // 6. Get all values
        println(items2.values)

        // 7. Add another item ( this is immutable so it will return a map )
        val items3 = items2.plus("d" to 4)
        val items3b = items2.plus(Pair("d", 4))
        println(items3)

        // 8. Add multiple items
        val items4 = items3.plus(listOf("e" to 5, "f" to 6))
        val items4b = items3.plus(listOf(Pair("e", 5), Pair("f", 6)))
        println(items4)

        // 9. Update a key
        val items5 = items4.plus(Pair("f", 300))
        println(items5)

        // 10. Remove an item
        val items6 = items5 - "f"
        println(items6)

        // 11. Iterate over pairs
        items6.forEach { pair -> println("key: " + pair.key + ", val: " + pair.value) }
    }

    // https://antonioleiva.com/collection-operations-kotlin/
    fun testLists(): Unit {
        // 1. Declare an empty Immutable list
        val items0 = listOf<String>()

        // 2. Declare an immutable list of 3 items
        // NOTE: Later in this example we will show how to
        // iterate over the items and also how to convert the
        // items from strings to numbers without manual loops
        val items = listOf("1", "2", "3")

        // 3. Get the size
        println(items.size)

        // 4. Get an item by key
        println(items[1])

        // 5. Get the first item ( head )
        println(items.first())

        // 6. Get all the items after the first
        println(items.drop(1))

        // 7. Get all last one
        println(items.last())

        // 8. Get the first N elements
        println(items.take(2))

        // 9. Add item ( to the end )
        // NOTE: this is immutable so it will return a new instance
        val items2 = items + "4"
        println(items2)

        // 10. Add item ( to the beginning )
        // NOTE: this is immutable so it will return a new instance
        val items3 = listOf("0") + items2
        println(items3)

        // 11. Update a key ( set the ndx 1 = 10
        // TODO: This could be an extension function and/or there may already be a better way to do this
        val items4 = items3.mapIndexed { ndx, value -> if (ndx == 1) "10" else value }
        println(items4)

        // 12. Insert at position ( lets fix the values )
        // TODO: This could be an extension function and/or there may already be a better way to do this
        val items5 = items4.subList(0, 1) + listOf("1") + items4.subList(1, items4.size - 1)
        println(items5)

        // 13. Remove an item at index 3
        val items6 = items5.filterIndexed { ndx, _ -> ndx != 3 }
        println(items6)

        // 14. Convert to map
        // All the items in the list are converted to key/value pairs
        // and then these are used to create a map.
        val map = items6.map { value -> Pair(value, value) }.toMap()
        println(map)

        // 15. Iterate over pairs
        items6.forEach(::println)
        items6.forEach { item -> println(item) }

        // 16. Convert each item in the list to an integer
        // NOTE: You don't need to iterate yourself.
        val numsA = items6.map(String::toInt)
        val numsB = items6.map { it.toInt() }
        val numsC = items6.map { text -> text.toInt() }
        println(numsA)
        println(numsB)
        println(numsC)

        // 17. Finally, combine 2 lists together
        val items7 = items6 + listOf("6", "7")
        println(items7)
    }


    fun showPairs(): Unit {
        // 1. Create a pair of string and int
        val pair1 = Pair("a", 1)
        println(pair1.first)
        println(pair1.second)

        // 2. Create a triple of string, bool, int
        val user = Triple("John", true, 3)

        // 3. Access the individual items
        val name = user.first
        val active = user.second
        val id = user.third

        // 3. Declare a tuple explicity
        val user2: Triple<String, Boolean, Int> = Triple("Jane", true, 2)

    }


    fun showMutableItems(): Unit {
        val itemsVar: MutableList<Int> = mutableListOf(1, 2)
        val itemsVal: List<Int> = itemsVar.toList()

        val mapVar: MutableMap<String, Int> = mutableMapOf("a" to 1)
        val mapVal: Map<String, Int> = mapVar.toMap()
    }


    fun showTryCatch(): Unit {

        // Try/catch is an expression, which means that
        // the try/catch will return a value.
        // In this example, we return a tuple( boolean, string )
        // indicating, success, and a message.
        val result: Pair<Boolean, String> =
                try {
                    println("try catch example")
                    Pair(true, "success")
                }
                catch(ex: IllegalArgumentException) {
                    Pair(false, "bad argument: " + ex.message)
                }
                catch(ex: Exception) {
                    Pair(false, "unexpected  : " + ex.message)
                }
                finally {
                    println("finally!")
                }

        // Now print the result ( tuple )
        println(result)
    }


    // Sample data class for test purposes
    data class Account(val id: Long, val email: String, val active:Boolean)


    fun showWhen(): Unit {

        fun testWhen(item: Any): Unit {

            when (item) {

                // 1. exact value match
                true                       -> println("ok")
                7                          -> println("lucky 7")
                7.7                        -> println("lucky 7.7")
                "cat"                      -> println("meow")
                "dog"                      -> println("woof")
                1..3                       -> println("range")

                // 2. data class value
                Account(1, "john", true)   -> println("john ")
                Account(2, "jane", true)   -> println("jane ")
                Account(3, "flash",true)   -> println("flash")
                Pair("a", 1)               -> println("pair ")

                // 3. data type match
                is Boolean                 -> println("bool:   " + item)
                is Int                     -> println("number: " + item)
                is Double                  -> println("double: " + item)
                is String                  -> println("text:   " + item)
                is Account                 -> println("user:   " + item)
                is Pair<*,*>               -> println("pair:   " + item)

                // 4. default case
                else                       -> println("unknown")
            }
        }

        testWhen(true)
        testWhen(7)
        testWhen(7.7)
        testWhen("cat")
        testWhen("dog")
        testWhen(1..3)

        testWhen(Account(1, "john", true))
        testWhen(Account(2, "jane", true))
        testWhen(Account(3, "flash",true))
        testWhen(Pair("a", 1))

        testWhen(false)
        testWhen(30)
        testWhen(12.34)
        testWhen("abc")
        testWhen(Account(4, "batman",false))
        testWhen(Triple(1,2,3))
    }


    // CALL BY NAME: takes a code block using syntax : =>
    fun refactor1(block: () -> Unit): Unit {
        println("review this code in area")
        block()
    }


    // Same function using normal function
    fun refactor2(block: () -> Unit): Unit {
        println("review this code in area")
        block()
    }


    fun showCallByName(): Unit {
        // this block of code is wrapped up as a function and called.
        // With call by name, you DO NOT have to use syntax () => for passing a function
        refactor1 {
            println("refactor this code!")
        }


        // Without call by name, notice you MUST use syntax () => for passing a function
        refactor2 {
            println("code review example via call by name")
        }
    }
}
