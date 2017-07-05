---
layout: start_page
title: module Utils
permalink: /scratch-kotlin-101
---
 
# package 
```kotlin 

  package slatekit.common.log

  class Logger {
  
      // ...
	  
  }
  
```


# main 
```kotlin 
 
  package slatekit.samples.sampleapp1

  fun main(args: Array<String>): Unit {
      println("hello world")
  }

```


# import
```kotlin

  // 1. Import 1 item from a package 
  import slatekit.common.log.Logger
  
  
  // 2. Import all items from a pacakge using "*"
  import slatekit.common.log.*
  
  
  // 3. Import 1 method ( string6 ) from an Object ( Random )
  // NOTE: An object is essentially a Singleton class 
  import slatekit.common.Random.string6
  
 
  fun main(args:Array<String>):Unit {
      println(string6())
  }


```

# vars
```kotlin

  var name = "john doe"
  var age = 35
  var account = 1234567890L
  var isActive = false
  var salary = 20.5
  var date = LocalDateTime.now()
  
```

# vals
```kotlin

  val name = "john doe"
  val age = 35
  val account = 1234567890L
  val isActive = false
  val salary = 20.5
  val date = LocalDateTime.now()
  
```

# types
```kotlin
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

```
 

# strings
```kotlin

  // 1. Declaring strings
  val name = "john doe"
  
  
  // 2. Adding strings together
  val fullName = "Clark" + ", Kent"
  
  
  // 3. Getting a char position in a string e.g. 1st pos
  var c1 = "Batman"[0]
  
  
  // 4. Convert strings to other types
  val age     = "30".toInt()
  val isActive  = "true".toBoolean()
  val salary  = "20.5".toDouble()
  
  
  // 5. String interpolation
  val account = 1234567890L
  val date = LocalDateTime.now()
  
  val info1 = "$name $age $account $isActive $salary $date"
  println( info1 )
  
  
  // 6. String interpolation ( with braces )
  // Use this explicit approach when you dont have spaces
  val info2 = "${name}${age}${account}${isActive}${salary}${date}"
  println( info2 )
  
  
  // 7. String interpolation with more expressions
  val info3 = "${name} salary is now ${salary + 200} start on ${date}"
  println( info3 )
```

# strings multiline
```kotlin

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
```


# enums 
```kotlin 

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

```

# lists 
```kotlin
 
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
  // TODO: This could be an extension function and/or there 
  // may already be a better way to do this
  val items4 = items3.mapIndexed { ndx, value -> if (ndx == 1) "10" else value }
  println(items4)
 
  // 12. Insert at position ( lets fix the values )
  // TODO: This could be an extension function and/or there 
  // may already be a better way to do this
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
  val numsA = items6.map( String::toInt )
  val numsB = items6.map{ it.toInt() }
  val numsC = items6.map { text -> text.toInt() }
  println(numsA)
  println(numsB)
  println(numsC)
  
  // 17. Finally, combine 2 lists together
  val items7 = items6 + listOf("6", "7")
  println(items7)
  
```

# maps 
```kotlin 

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
  val items3 = items2.plus( "d" to 4 )
  val items3b = items2.plus(Pair("d", 4))
  println(items3)
  
  // 8. Add multiple items
  val items4 = items3.plus(listOf( "e" to 5, "f" to 6))
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
    
```

# pairs
```kotlin
  // 1. Create a pair of string and int
  val pair1 = Pair("a", 1)
  println( pair1.first )
  println( pair1.second )
  
  // 2. Create a triple of string, bool, int
  val user = Triple( "John", true, 3 )
  
  // 3. Access the individual items
  val name = user.first
  val active = user.second
  val id = user.third
  
  // 3. Declare a tuple explicity
  val user2: Triple<String, Boolean, Int> = Triple( "Jane", true, 2 )
```

# nulls
```kotlin

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
    
```

# loops 
```kotlin 
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
```

## when 
```kotlin 

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

```

# try-catch
```kotlin 
  // Try/catch is an expression, which means that
  // the try/catch will return a value.
  // In this example, we return a Pair( boolean, string )
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
```

# iterations
```kotlin 
   val items = listOf("1","2", "3")

  // Case 1: For each with variable name
  items.forEach { item -> println(item) }
  
  // Case 2: For each with auto-supplied "it" variable
  items.forEach { println(it) }
  
  // Case 3: For each with lambda
  items.forEach( ::println )
  
  // Case 4: For each with index
  items.forEachIndexed { index, s -> print(" " + items[index]) }
  println()
  
  // Case 5: Using a range
  4.until(6).forEach{ v -> print(" " + v ) }

```

# aggregates
```kotlin 
  val items = listOf("1","2", "3")
  
  // Case 1: apply function to each
  // Prints:
  // 1
  // 2
  // 3
  items.forEach( ::println )
  
  // Case 2: map strings to new list of numbers
  // Result: List[Int](1, 2, 3)
  val nums = items.map{ it.toInt() }
  println(nums)
  
  // Case 3: filter ( find items > 2 )
  val filtered = items.filter { it -> it.toInt() > 2 }
  println( filtered )
  
  // Case 4: reduce to 1 value
  // Result: "1:2:3"
  val total = items.reduce{ a, b -> a + ":" + b }
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
```


## Functions
```kotlin 

  // Sample function to calculate a salary.
  // NOTE: Single line function body can be written using an "=" sign
  fun salary(rate:Double, hours:Int):Double = rate * hours
  
  
  // Test the functions
  fun test(): Unit {
  
      // function with parameters
      val r1 = salary(10.50, 8)
      println(r1)
  }

```

## Functions: Named params
```kotlin  

  // Sample function to calculate a salary.
  // NOTE: Single line function body can be written using an "=" sign
  fun salary(rate:Double, hours:Int):Double = rate * hours
  
  
  // Test the functions
  fun test(): Unit {
  
      // call with named parameters
      val r2 = salary(rate = 10.50, hours = 8 )
      println(r2)
  }

```


## Functions: Default parameters
```kotlin  

  // Sample function to create a user.
  // NOTES:
  // 1. Parameters can have default values
  // 2. Unit is a type that does not represent any value
  fun createUser(name: String, isActive: Boolean = true): Unit {
      println(name)
  }
  
  
  // Test the functions
  fun test(): Unit {
  
      // function with parameters with default values
      createUser("john doe")
  }

```


## Functions: HOF: Passing a function 
```kotlin  

   // Sample function to simulate creating a new user with a callback
   // function supplied. This function takes 1 parameter of type Int
   // and returns nothing ( Unit ).
   fun createUser( name:String, isActive:Boolean, onComplete:(Int) -> Unit) : Unit {
       
       println(name + ", " + isActive)
       
       // simulate saving to database....
       val id = 2
       
       // notify caller
       onComplete( id )
   }
  
  
  // Test the functions
  fun test(): Unit {
      
      // function with function parameter
      createUser("john doe", true, { id -> println("created with id : $id") } )
  }

```


## Functions: HOF: Returning a function 
```kotlin  
  // Return a function that uses the rate per hour, and
  // overtime rate per hour to determine the salary.
  // This function only needs 1 parameter representing the hours worked
  fun getSalaryCalculator(rate: Double, overtimeRate: Double): (Int) -> Double {
      
      // NOTE: As an analogy, you have basically "dependency injected"
      // the salary and overtimePay into the function you are returning.
      val f = { hours: Int ->            
          if (hours <= 8)
              rate * hours
          else
              rate * 8 + (overtimeRate * (hours - 8))
      }
      return f
  }
  
  
  // Test the functions
  fun test(): Unit {
      
      // Supply the initial inputs ( rate per hour, rate overtime per hour )
      val calc = getSalaryCalculator(10.0, 15.0)

      // Now you have a function that only requires the hours worked
      val pay = calc(10)
      println(pay)
  }
```

## Functions: Composition
```kotlin 

  // Simple utility function to "compose" 2 functions
  // into a new function that calls both.
  fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
      return { x -> f(g(x)) }
  }


  fun composition(): Unit {

      // 1st function to generate html for h1
      val h1 = { text: String -> "<h1>" + text + "</h1>" }
      
      // 2nd function to generate html section
      val section = { content: String -> "<div class='section'>" + content + "</div>" }
      
      // Now lets combine section and h1
      val heading = compose( section, h1 )
      
      // Generate the heading html
      val html = heading("Welcome")
      
      // e.g. <div class="section"><h1>Welcome</h1></div>"
      println(html)
}

```

## Functions: Lambda
```kotlin 
  
  /**
   * Indicates that a refactoring is required
   * @param tag
   * @param msg
   * @param callback
   */
  fun REFACTOR(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
      // log to console or file
  }
	
	
  REFACTOR("ACCOUNTS", "Improve error handling", {
      // Some code to cleanup here.
  })
  
  
  // With parameters inside (...) first followed by {}
  REFACTOR("ACCOUNTS", "Improve error handling") {
      // Some code to cleanup here
  }
  
  // With defaults e.g. "" empty string.
  REFACTOR {
      // Some code to cleanup here
  }

  // With defaults and no code block
  REFACTOR()

```

## Extensions
```kotlin

  /**
   * Truncate the string to the length supplied
   */
  fun String.truncate(len:Int):String =
      if(this.length > len)
          this.substring(0, len)
      else
          this
  
  
  /**
   * Returns all items after the first
   */
  fun <T> List<T>.tail(): List<T> = this.drop(1)
  
  
  fun testExtensions():Unit {
      // Prints "abc!"
      println("abcd".truncate(3) + "!")
  
      // Prints ["b", "c"]
      println(listOf("a", "b", "c").tail())
  }
  
```

## Operators
```kotlin
  /**
   * @param start: 1
   * @param end  : 4
   */
  class MyRange(val start:Int, val end:Int ){
  
      // Override inc ++
      operator fun inc():MyRange = MyRange(start, end + 1)
  
  
      // Override dec --
      operator fun dec():MyRange = MyRange(start, end - 1 )
  
  
      // Override plus +
      operator fun plus(num:Int):MyRange = MyRange(start, end + num )
  
  
      // Override minus - ( get the smallest range )
      operator fun minus(num:Int):MyRange = MyRange(start, end - num )
  
  
      // Override times *
      operator fun times(num:Int):MyRange = MyRange(start, end * num )
  
  
      // Override div Point * Point
      operator fun div(num:Int):MyRange = MyRange(start, end / num )
  
  
      // Override "in" 3 in myRange
      operator fun contains(x:Int):Boolean = x in start..end
  
  
      // Override index access [x] = true | false
      operator fun get(x:Int):Boolean = contains(x)
  
  
      // Override compare to ( <, <=, >, >=, ==, != )
      operator fun compareTo(other:MyRange):Int {
          val xCompare = start.compareTo(other.start)
          if (xCompare != 0) return xCompare
          return end.compareTo(other.end)
      }
  
  
      override fun toString(): String {
          return "$start : $end"
      }
  }
  
  
  fun testOperator():Unit {
  
      // Test ++, --
      var r1 = MyRange(1,4)
      println( r1++ )
      println( r1-- )
  
      // Test +, -, *, /
      println( r1 + 2 )
      println( r1 - 2 )
      println( r1 * 2 )
      println( r1 / 2 )
  
      // Test in/contains
      println( 3 in r1 )
      println( 10 in r1 )
  
      // Test get
      println(r1[3])
      println(r1[10])
  
      // Test <, <=, >, >=
      println(MyRange(1, 3) >  MyRange(1, 2))
      println(MyRange(1, 3) >= MyRange(1, 2))
      println(MyRange(1, 3) <  MyRange(1, 4))
      println(MyRange(1, 3) <= MyRange(1, 4))
  }

```


## Type Alias
```kotlin

  // The type alias here becomes the shorthand for the function signature
  typealias ErrorHandler = (Int, String, Exception) -> Unit
  
  
  fun onError(code:Int, msg:String, ex:Exception):Unit {
     println("$code $msg ${ex.message}")
  }
  
  
  // onError callback explicitly defined with function signature
  fun createUser1(email:String, onError:(Int, String, Exception) -> Unit):Unit {
      try {
          // create user here
      } catch (ex:Exception) {
          onError(500, "unexpected error creating user", ex )
      }
  }
  
  
  // onError callback is shorter and cleaner using type alias
  fun createUser2(email:String, onError:ErrorHandler):Unit {
      try {
          // create user here
      } catch (ex:Exception) {
          onError(500, "unexpected error creating user", ex )
      }
  }
  
  
  fun testTypeAlias():Unit {
      // Same code at the call site
      createUser1("batman@gotham", ::onError)
      createUser2("batman@gotham", ::onError)
  }
```

### TODO 
```kotlin 
 
  // Here the TODO function from standard library returns "Nothing"
  // which allows the code to compile even though the function returns 
  // a type of Pair<Int,String>
  fun testTODO(email:String, subject:String, content:String ):Pair<Int,String> {
  
      return if(email.isNullOrEmpty()){
          TODO("figure out error handling/logging here")
      }
      else {
          // send email
          Pair(200, "success")
      }
  }

```


## Member reference 
```kotlin 

  import kotlin.reflect.KProperty

  data class Employee(val id:Int, val email:String)
  
  
  fun printMember(prop: KProperty<*>):Unit = println(prop.name)
  
  
  // You can reference the properties of Employee class using "::"
  // This facilitates type-safe meta programming. E.g. you can use
  // this approach for mapping properties to a table, or building
  // queries in an ORM. 
  fun testMemberReference():Unit {
      printMember( Employee::id    )
      printMember( Employee::email )
  }


```

## Annotations
```kotlin 

  @Target(AnnotationTarget.CLASS)
  annotation class Api (val name     : String = "",
                        val desc     : String = "")
  
  					  
  @Target(AnnotationTarget.FUNCTION)
  annotation class Get(
          val path     : String = ""
  )
  
  
  @Target(AnnotationTarget.VALUE_PARAMETER)
  annotation class Arg(val desc:String)
  
  
  data class Movie(val name:String, val year:Int)
  
  
  // Use Api annotation on the class to designate a Web API 
  @Api("/movies", desc = "api to get movie info")
  class MovieApi {
 
      // Use Get annotation on a method to designate an http get request
      // Use the Arg annotation on a parameter to describe parameter
      @Get("blockbusters")
      fun blockbusters(@Arg("year released")
                       year:Int): List<Movie> {
        return listOf(
                Movie("Wonder Woman", 2017),
                Movie("Spider-Man"  , 2017)
        )
    }
  }

```

## Deprecation
```kotlin 

  @Deprecated("deprecation warning", level = DeprecationLevel.WARNING)
  fun sampleFunc1():Unit {
  }
  
  
  @Deprecated("deprecation hidden", level = DeprecationLevel.HIDDEN)
  fun sampleFunc2():Unit {
  }
  
  
  @Deprecated("deprecation error", level = DeprecationLevel.ERROR)
  fun sampleFunc3():Unit {
  }
  
  
  fun testDeprecation():Unit {
      // deprecations with level warning are shown with strike-through
      sampleFunc1()
      
      // deprecations with level hidden are not available / resolved
      // so the following usage is a compile error 
      sampleFunc2()
      
      // depracation with level error is a compile level deprecation error
      sampleFunc3()
  }
```

## Classes
```kotlin

  // 1. You can declare class members in the constructor
  // 2. You can declare and set the access modifiers ( none = public, protected, private )
  // 3. You can declare whether they are mutable ( var ) or immutable ( val )
  open class User(val name: String,
                  var isActive: Boolean,
                  protected val email: String,
                  private val account: Int
  
  ) {
      init {
          // optional initialization after primary constructor.
          println("instantiated with : " + name)
      }
  
  
      // Overloaded constructors must call the primary constructor first
      constructor() : this("", false, "", 0)
  
  
      // Overloaded constructors must call the primary constructor first
      constructor(name: String) : this(name, false, "", 0)
  
  
      fun printInfo(): Unit {
  
          println("$name, $email, $account, $isActive")
      }
  }
```

## Subclassing
```kotlin 

  // SUB-CLASSING:
  // 1. You must declare your class as "open" to sub-class it
  // 2. You must declare a method as "open" to override it
  open class UserAccount( val name:String, val isActive:Boolean, val roles:String )
  {
      open fun info():String = "USER: $name $isActive $roles"
  }
  
  
  // Just a simple example showing how to extend a class.
  class AdminAccount(name:String) : UserAccount(name, true, "admin")
  {
      override fun info():String = "ADMIN: $name $isActive $roles"
  }
  
```


## Data classes 
```kotlin 

  // Mark class with "data".
  // USE CASES:
  // 1. Data classes are useful when you typically want to just hold data
  // 2. They are also useful in pattern-matching.
  //
  // AUTOMATIC SUPPORT
  // Data classes have the "equals()", "toString()" and "copy(...)"
  // methods automatically created by the compiler.
  data class Role(val name:String, val desc:String, val features:List<String>)
  
  
  fun testRoles():Unit {
  
      // Create role with name, desc, and feature list
      val role1 = Role("editors", "Create / Edit content", listOf("blogs", "comments"))
      println(role1)
  
      // Copy role1 with existing features but just change name and desc.
      val role2 = role1.copy("moderators", "moderate content")
      println(role2)
  }
  
```

## Interfaces
```kotlin

  // Interfaces can have implementations
  interface AccountSupport {
  
      fun activate():Unit {
          // do some processing and notify
          notify("account activated")
      }
  
  
      fun deactivate():Unit {
          // do some processing and notify
          notify("account deactivated")
      }
  
  
      fun notify(state:String):Unit {
          // e.g. send to phone / log / audit etc.
          // just println for sample purposes
          println(state)
      }
  }
  
  
  // Just a simple example showing how to extend a class.
  class ModeratorAccount(name:String)
      : UserAccount(name, true, "moderators")
      , AccountSupport
  {
      override fun info():String = "MOD: $name $isActive $roles"
  }
  
  
  fun testInterfaces():Unit {
      val mod = ModeratorAccount("john")
      mod.activate()
      mod.deactivate()
  }

```


## Static methods
```kotlin

  // You can put "static" methods on a class inside a companion object    
  open class UserAccount( val name:String, val isActive:Boolean, val roles:String )
  {
      open fun info():String = "USER: $name $isActive $roles"
   
  
      companion object {
          // This is essentially a static field.
          val guest = UserAccount("guest", false, "")
      }
  }
  
  
  // This is essentially a singleton class
  object Accounts {
      val guest = UserAccount("guest", false, "")
  }
  
  
  fun testStatic():Unit {
      val guestFromCompanion = UserAccount.guest
      val guestFromExternalCls = Accounts.guest
  }

```