---
layout: start_page
title: module Utils
permalink: /scala101_raw
---

# Scala 
Here is a brief introduction to scala. ( this doesn't list all the features language semantics )

# Resources
- [http://docs.scala-lang.org/cheatsheets/](http://docs.scala-lang.org/cheatsheets/)
- [https://mbonaci.github.io/scala/](https://mbonaci.github.io/scala/)
- [http://rea.tech/java-to-scala-cheatsheet/](http://rea.tech/java-to-scala-cheatsheet/)


# Basics
Some basic features of scala. 

{: .table .table-striped .table-bordered}
|:--|:--|:--|:--|
| **area** | **desc** | **notes** | **link** |
| -          | import       |  -  | {: .btn .btn-primary} more |
| -          | package      |  -  | {: .btn .btn-primary} more |
| -          | main         |  -  | {: .btn .btn-primary} more |
| -          | vals         |  -  | {: .btn .btn-primary} more |
| -          | vars         |  -  | {: .btn .btn-primary} more |
| -          | types        |  -  | {: .btn .btn-primary} more |
| -          | strings      |  -  | {: .btn .btn-primary} more |

# Language
Common language features

{: .table .table-striped .table-bordered}
|:--|:--|:--|:--|
| **area** | **desc** | **notes** | **link** |
| -          | conditions   |  -  | {: .btn .btn-primary} more |
| -          | loops        |  -  | {: .btn .btn-primary} more |
| -          | try catch    |  -  | {: .btn .btn-primary} more |
| -          | functions    |  -  | {: .btn .btn-primary} more |
| -          | lists        |  -  | {: .btn .btn-primary} more |
| -          | maps         |  -  | {: .btn .btn-primary} more |


# Scala 
These are more specific features of the scala language 

{: .table .table-striped .table-bordered}
|:--|:--|:--|:--|
| **area** | **desc** | **notes** | **link** |
| -          | multiline strings    |  -  | {: .btn .btn-primary} more |
| -          | immuatable   |  -  | {: .btn .btn-primary} more |
| -          | option       |  -  | {: .btn .btn-primary} more |
| -          | call by name |  -  | {: .btn .btn-primary} more |
| -          | constructors |  -  | {: .btn .btn-primary} more |
| -          | traits       |  -  | {: .btn .btn-primary} more |
| -          | case classes |  -  | {: .btn .btn-primary} more |
| -          | pattern match|  -  | {: .btn .btn-primary} more |
| -          | operators    |  -  | {: .btn .btn-primary} more |
| -          | implicits    |  -  | {: .btn .btn-primary} more |
| -          | actors       |  -  | {: .btn .btn-primary} more |
| -          | method calls |  -  | {: .btn .btn-primary} more |


## Import
you can import items from a package in various ways, 1 item, multiple, and all

```scala 
  
  // 1. Import 1 item from a package 
  import slate.common.logging.Logger
  
  
  // 2. Import multiple items from a package using { ... }
  import slate.common.logging.{Logger, LogLevel}

  
  // 3. Import all items from a pacakge using "_"
  import slate.common.logging._
  
  // 4. Import 1 function from an Object 
  // NOTE: An object is essentially a Singleton class 
  // that serves as a "module" containing 1 or more functions
  import slate.common.Random.{string6}
  println( string6() )
  
  // 5. Import all functions from the Random module
  import slate.common.Random._ 
  println( string3() )
  println( string6() )
```

{: .btn .btn-primary}
back to top

## Package
declaring you code in a pacakge 

```scala 
  
  package slate.common.logging
  
  class Logger {
  
    // ....
  
  }

```

{: .btn .btn-primary}
back to top

## Main
simple main method as entry point into application

```scala 
  
  package slate.examples.sampleApp
  
  object Examples {

    def main(args: Array[String]): Unit = {
      println( "hello world" )
    }
  }
  
```

{: .btn .btn-primary}
back to top

## Vals
constants / immutable values are declared using **val**

```scala 
  
   val name = "john doe"
   val age = 35
   val account = 12345678901L
   val isActive = false
   val salary = 20.5
   val date = LocalDateTime.now()
   
```

{: .btn .btn-primary}
back to top

## Vars
variables are declared using **var**

```scala 
  
   var name = "john doe"
   var age = 35
   var account = 1234567890L
   var isActive = false
   var salary = 20.5
   var date = LocalDateTime.now()
   
```

{: .btn .btn-primary}
back to top

## Types
You can declare types implicitly using the values or explicity by specifying the type name

```scala 
	
    // Implicit
    val name1     = "john doe"
    val age1      = 35
    val account1  = 1234567890L
    val isActive1 = false
    val salary1   = 20.5
    val date1     = LocalDateTime.now()
    val tuple1    = ("john doe", 35, 123456789L, false, 20.5 )
	

    // Explicit
    val name2     :String        = "john doe"
    val age2      :Int           = 35
    val account2  :Long          = 1234567890L
    val isActive2 :Boolean       = false
    val salary2   :Double        = 20.5
    val date2     :LocalDateTime = LocalDateTime.now()
    val tuple2    :(String,Int,Long,Boolean,Double) = ("john doe", 35, 123456789L, false, 20.5 )

```

{: .btn .btn-primary}
back to top

## Strings
Strings can be interpolated

```scala 
    
    // 1. Declaring strings
    val name = "john doe"
    

    // 2. Adding strings together
    val fullName = "Clark" + ", Kent" 


    // 3. Getting a char position in a string e.g. 1st pos
    var c1 = "Batman"(0)


    // 4. Convert strings to other types
    val age     = "30".toInt
    val isActive  = "true".toBoolean
    val salary  = "20.5".toDouble


    // 5. String interpolation
    val account = 1234567890L
    val date = LocalDateTime.now()

    val info1 = s"$name $age $account $isActive $salary $date"
    println( info1 )
	

    // 6. String interpolation ( with braces )
    // Use this explicit approach when you dont have spaces
    val info2 = s"${name}${age}${account}${isActive}${salary}${date}"
    println( info2 )
	

    // 7. String interpolation with more expressions
    val info3 = s"${name} salary is now ${salary + 200} start on ${date}"
    println( info3 )
    
	
```

{: .btn .btn-primary}
back to top

## Dates
Basic Date/Time handling

```scala 
  
   // Refer to Java 8 Date/Time classes
   
```

{: .btn .btn-primary}
back to top


## Tuples
Using tuples and tuple operations

```scala 
  
   // 1. Create a tuple of 3 items of string, bool, int
   val user = ( "John", true, 3 )

   // 2. Access the individual items 
   val name = user._1 
   val active = user._2 
   val id = user._3 

   // 3. Declare a tuple explicity
   val user2: ( String, Boolean, Int) = ( "Jane", true, 2 )
   
```

{: .btn .btn-primary}
back to top

## Conditions
if conditions 

```scala 

    // 1. if only
    if ( true ) println ( "true" )

    // 2. No braces
    val code = 200
    if ( code == 200 )
      println( "ok" )
    else if  ( code == 404 )
      println( "not found")
    else
      println( "unknown" )

    // 3. If conditions are expressions!
    // This means they return a value.
    // NOTE: This helps with immutability
    // as you can assign the result of the 
    // if to a value ( in this case message )
    val message =
      if ( code == 200) {
        "ok"
      }
      else if ( code == 404 ) {
        "not found"
      }
      else {
        "unknown"
      }
    println(message)

    // 4. There is no ternary "? :" operation, but in scala
    // you do not need it as a simple if/else is sufficient
    val status = if( code == 200 ) "ok" else "unknown"
    println(status)

```

{: .btn .btn-primary}
back to top


## Loops
for loops and while loops

```scala 

    // 1. for loop inclusive ( 5 included )
    for( a <- 1 to 5){
      println( "Value of a: " + a )
    }
	

    // 2. for loop exclusive ( 5 excluded )
    for( a <- 1 until 5){
      println( "Value of a: " + a )
    }
	

    // 3. for loop with setup using values
    val start = 1
    val end = 5
    for( a <- start until end){
      println( "Value of a: " + a )
    }
	

    // 4. for loop over collection
    val words = List("a1", "b2", "c3", "d4", "e5")
    for( word <- words ){
      println( word )
    }
	

    // 5. while loop
    var a = 0
    while(a < 4){
      println( "Value of a: " + a)
      a += 1
    }

```

{: .btn .btn-primary}
back to top


## Try/Catch
try catch blocks use a pattern matching syntax for catching exceptions.

```scala 


    // Everything is an expression, which means that
    // the try/catch will return a value.
    // In this example, we return a tuple( boolean, string )
    // indicating, success, and a message.
    // NOTE: This helps with immutability
    // as you can assign the result of the try
    // to a value ( in this case the result )
    val result:(Boolean, String) =
      try
      {
        println("try catch example")
        ( true, "success" )
      }
      catch
        {
          case ex:IllegalArgumentException =>
          {
            (false, "bad argument: " + ex.getMessage )
          }
          case ex:Exception =>
          {
            (false, "unexpected  : " + ex.getMessage )
          }
        }
      finally {
        println("finally!")
      }

    // Now print the result ( tuple )
    println( result )

```


## Lists
Lists can be immutable ( readonly ) or mutable ( editable ). This shows the immutable version

```scala 
  
    // 1. Declare an empty Immutable list
    val items0 = List[String]()

    // 2. Declare an immutable list of 3 items
    // NOTE: Later in this example we will show how to
    // iterate over the items and also how to convert the
    // items from strings to numbers without manual loops
    val items = List[String]( "1", "2", "3" )

    // 3. Get the size
    println( items.size )

    // 4. Get an item by key
    println( items(1) )

    // 5. Get the first item ( head )
    println( items.head )

    // 6. Get all the items after the first
    println( items.tail )

    // 7. Get all last one
    println( items.last )

    // 8. Get the first N elements
    println( items.take(2) )

    // 9. Add item ( to the end )
    // NOTE: this is immutable so it will return a new instance
    // Also, the operation ":+" is used to append.
    // Think of the "+" as being at the end of the ":+" operator
    // indicating that the item adds to the end of the list
    val items2 = items :+ "4"
    println( items2 )

    // 10. Add item ( to the beginning )
    // NOTE: this is immutable so it will return a new instance
    // Think of the "+" as being at the front of the "+:" operator
    // and the item being added must also be at the front.
    val items3 = "0" +: items2
    println( items3 )

    // 11. Update a key ( set the ndx 1 = 10
    val items4 = items3.updated(1, "10")
    println( items4 )

    // 12. Insert at position ( lets fix the values )
    // NOTE: You have to supply a list of items as the replacements
    val items5 = items4.patch(1, Seq[String]("1"), 1)
    println(items5)

    // 13. Remove an item at index 3
    val items6 = items5.patch(2, Nil, 1)
    println( items6 )

    // 14. Convert to map
    // This is slightly tricky syntax, but whats happening is
    // that all the items in the list are converted to key/value pairs
    // and then these are supplied to the map constructor as individual
    // items.
    val map = Map(items6.map( a => a -> a ): _*)
    println(map)

    // 15. Iterate over pairs
    items6.foreach( item => println( item ))

    // 16. Convert each item in the list to an integer
    // NOTE: You don't need to iterate yourself.
    val nums = items6.map( text => text.toInt )
    println( nums )

    // 17. Finally, combine 2 lists together
    val items7 = items6 ++ List[String]("6", "7")
    println( items7 )

```


## Maps
Maps can be immutable ( readonly ) or mutable ( editable ). This shows the immutable version

```scala
	// 1. Declare empty immutable list
    val items1 = Map[String,Int]()

    // 2. Declare immutable list with pairs of items
    val items2 = Map[String,Int]( "a" -> 1, "b" -> 2, "c" -> 3)

    // 3. Get the size
    println( items2.size )

    // 4. Get an item by key
    println( items2("a") )

    // 5. Get all keys
    println( items2.keys )

    // 6. Get all values
    println( items2.values )

    // 7. Add another item ( this is immutable so it will return a new map )
    val items3 = items2 + ( "d" -> 4 )
    println( items3 )

    // 8. Add multiple items
    val items4 = items3 + ( "e" -> 5, "f" -> 6)
    println( items4 )

    // 9. Update a key
    val items5 = items4 + ( "f" -> 300 )
    println( items5 )

    // 10. Remove an item
    val items6 = items5 - "f"
    println( items6 )

    // 11. Iterate over pairs
    items6.foreach( pair => println( "key: " + pair._1 + ", val: " + pair._2 ))

```

{: .btn .btn-primary}
back to top


## Throw
Throwing of exceptions

```scala 
  
   throw new Exception("testing throwing of exception")
   
```

{: .btn .btn-primary}
back to top


## Return
Returning from function

```scala 
  
   // To return from a function that returns int
   return 1

   // To return from a function that returns "unit" 
   return Unit
   
```

{: .btn .btn-primary}
back to top


## Iterations
How to iterate over collections

```scala


    // NOTE: There are multiple ways to iterate over a collection
    // without using traditional "while" or "for" loops with variables
    
    // Case 1: Indices
    items.indices.foreach( ndx => print(" " + items(ndx)))
    println()
    
    // Case 2: Zip with index ( You get a tuple of ( value, index ) )
    items.zipWithIndex.foreach( d => println(d._1 + ":" + d._2) )
    
    // Case 3: Zip with index with case
    items.zipWithIndex.foreach{ case(item, index) => println(item + ":" + index) }
    
    // Case 4: Using a range
    4.until(6).foreach( v => print(" " + v ) )

```

{: .btn .btn-primary}
back to top


## Aggregates
How to use various map, reduce, fold, and aggregate functions

```scala

    val items = List[String]("1","2", "3")
    
    // Case 1: apply function to each
    // Prints:
    // 1
    // 2
    // 3
    items.foreach(println)
    
    // Case 2: map strings to new list of numbers
    // Result: List[Int](1, 2, 3)
    val nums = items.map( i => i.toInt )
    println(nums)
    
    // Case 3: reduce to 1 value
    // Result: "1:2:3"
    val total = items.reduce( (a, b) => a + ":" + b )
    println("reduce: " + "'" + total + "'")
    
    // Case 4a: fold to single value from the Left side
    // Result: "LEFT,1,2,3"
    val totalLeft = items.foldLeft("LEFT")( (a, b) => a + "," + b)
    println("fold left: " + totalLeft)
    
    // Case 4b: fold to single value from the Right side
    // Result:  "1,2,3,RIGHT"
    val totalRight = items.foldRight("RIGHT")( (a, b) => a + "," + b)
    println("fold right: " + totalRight)
    
    // Case 5: max
    // Result: 3
    val max = items.maxBy[Int]( a => a.toInt)
    println("max: " + max)
    
    // Case 6: min
    // Result: 1
    val min = items.minBy[Int]( a => a.toInt)
    println("min: " + min)

```

{: .btn .btn-primary}
back to top


# Scala features

## Multiline strings
multiline strings begin with """

```scala 
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
                     | """.stripMargin

    // 3. Indentation is stripped and next line starts after "#"
    val example3 = """ app.users.activate
                     # - userid: number ( required )
                     # - phone : string ( required )
                     # - email : string ( required )
                     # """.stripMargin('#')

```

{: .btn .btn-primary}
back to top

## Functions
simple definition of functions

```scala 
  
  // 1. function does not return anything ( via "Unit" type )
  def test1( name:String, account:Long, isActive:Boolean) :Unit = {
    println(name)
  }
  

  // 2. function with default values for parameters ( via = value )
  def test2( name:String, account:Long = 0L, isActive:Boolean = true) :Unit = {
    println(name)
  }
  

  // 3. function with return value ( via :<type> )
  // NOTES:
  // 1. do not need a return keyword 
  // 2. last expression is assumed to be the return value
  def test3( name:String, account:Long = 0L, isActive:Boolean = true) : String = {
    if (account == 0 )
      return s"new user : ${name} ${account} ${isActive}"

    s"existing user: ${name}"
  }
  

  // 4. function with functions as parameters,
  // Last expression is assumed to be the return value
  def test4( name:String,
             hello1:() => Unit,
             hello2:(String) => Unit,
             hello3:(String, String) => String ) : Unit = {

    // 1. first function takes 0 parameters, and returns nothing
    hello1()

    // 2. second function takes 1 paramter, and returns nothing
    hello2("hello")

    // 3. third function takes 2 parameter and returns a value
    val result = hello3("hey", "hello")
    println( result )
  }
  
  
  // call the functions  
  def testFunctions():Unit = {
    
    // 1. function with parameters
    test1 ( "john doe", 0, true )

    // 2. function with parameters with default values
    test2 ( "john doe" )

    // 3. function with parameters with default values + return value
    val result1 = test3( "john doe", 123456789123L )
    println( result1 )

    // 4. function with functions as paramters
    val result2 = test4( "john doe",
        () => { Unit },
        (greeting1) => println(greeting1 + " john"),
        (greeting2, greeting3) => greeting2 + " and " + greeting3 + " john"
    )
    println( result2 )
  }

```

{: .btn .btn-primary}
back to top

## Functions 2
simple definition of functions

```scala 
  
  // Sample function to simulate creating a new user.
  def createUser( name:String, isActive:Boolean) :Unit = { 
    println(name + ", " + isActive)
  }
  
  
  // call the functions  
  def test():Unit = {
    
    // 1. function with parameters
    createUser( "john doe", true )
  }

```

{: .btn .btn-primary}
back to top

## Functions: Named parameters
simple definition of functions

```scala 
  
  // Sample function to simulate creating a new user.
  def createUser( name:String, isActive:Boolean) :Unit = { 
    println(name + ", " + isActive)
  }
  
  
  // call the functions  
  def test():Unit = {
    
    // 1. function with parameters
    createUser ( "john doe", true )

    // 2. function with named parameters
    createUser ( name = "john doe", isActive = true)
  }

```

{: .btn .btn-primary}
back to top


## Functions: Default parameters
simple definition of functions

```scala 
  
  // Function has last 2 parameters defaulted
  def createUser( name:String, isActive:Boolean = true) :Unit = { 
    println(name + ", " + account + ", " + isActive)
  }
  
  
  // call the functions  
  def test():Unit = {
    
    // 1. function with all parameters supplied
    createUser ( "john doe", true )

    // 2. function with last parameter defaulted
    createUser ( "john doe")
  }

```

{: .btn .btn-primary}
back to top

## Functions: HOF Passing a function
simple definition of functions

```scala 
  
  // Sample function to simulate creating a new user with a callback
  // function supplied. This function takes 1 parameter of type Int
  // and returns nothing ( Unit ).
  def createUser( name:String, isActive:Boolean, onComplete:(Int)=> Unit)
  : Unit = { 
    
    println(name + ", " + isActive)
    
    // simulate saving to database....
    val id = 2
    
    // notify caller 
    onComplete( id ) 
  }
  
  
  // create user with a callback for when the user has 
  // been created with the id.
  createUser ( "john doe", true, (id) => println("id: " + id) )

```

{: .btn .btn-primary}
back to top

## Functions: Composition

```scala 

    // 1st function to generate html for h1
    val h1 = (text:String) => "<h1>" + text + "</h1>"

    // 2nd function to generate html section
    val section = (content:String) => "<div class='section'>" + content + "</div>"

    // Now lets combine section and h1 via "compose"
    // to create a custom heading
    // NOTE: section.compose(h1) = section( h1( x ) )
    val heading = section.compose(h1)

    // Generate the heading html
    val html = heading("Welcome")

    // e.g. <div class="section"><h1>Welcome</h1></div>"
    println( html )
	
```

{: .btn .btn-primary}
back to top


## Functions: Return a function

```scala 

  // Return a function that uses the salary, overtimePay in its calculation
  // and only needs 1 parameter representing the hours worked  
  def getSalaryCalculator(salary:Int, overtimePay:Int):(Int) => Int = {
    
    // NOTE: As an analogy, you have basically "dependency injected"
    // the salary and overtimePay into the function you are returning.
    val f = (hours:Int) => {
      if ( hours <= 8 ) {
        salary * hours
      }
      else {
        ( salary * 8 ) + ( overtimePay * (hours - 8) )
      }
    }
	
    // Return the function here.
    f
   }
   
   // Supply the initial inputs ( salary per hour, overtime pay per hour )
   val calc = getSalaryCalculator(10, 15)
   
   // Now you have a function that only requires the hours worked
   val pay = calc(10)
   println(pay)
  
```

{: .btn .btn-primary}
back to top


## Immutable
you can easily make items immutable via the "val" keyword. collections also exist in mutable and immutable versions.

```scala 

   import scala.collections.immutable.List 
   import scala.collections.immutable.Map 
   
   val name = "john doe"
   val age = 35
   val account = 12345678901L
   val isActive = false
   val salary = 20.5
   val date = LocalDateTime.now()
   val user = new User("john doe", "john@gmail.com", 20)         
   val items  = List[String]("a", "b", "c", "d", "e" )
   val lookup = Map[String,Int]( "a" -> 1, "b" -> 2 )
   
```

{: .btn .btn-primary}
back to top


## Option

```scala 

    // Value
    val name1     = "john doe"
    val age1      = 35
    val account1  = 12345678901L
    val isActive1 = false
    val salary1   = 20.5
    val date1     = LocalDateTime.now()

    // Values as implicit Option[T] with an actual ( Some ) value
    val name2     = Some( "john doe" )
    val age2      = Some( 35 )
    val account2  = Some( 12345678901L )
    val isActive2 = Some( false )
    val salary2   = Some( 20.5 )
    val date2     = Some( LocalDateTime.now() )

    // Values with explicit type as Option[T] with an actual ( Some ) value
    val name3     :Option[String]        = Some( "john doe" )
    val age3      :Option[Int]           = Some( 35 )
    val account3  :Option[Long]          = Some( 12345678901L )
    val isActive3 :Option[Boolean]       = Some( false )
    val salary3   :Option[Double]        = Some( 20.5 )
    val date3     :Option[LocalDateTime] = Some( LocalDateTime.now() )

    // Values with explicit type as Option[T] with no ( None ) value
    val name4     :Option[String]        = None
    val age4      :Option[Int]           = None
    val account4  :Option[Long]          = None
    val isActive4 :Option[Boolean]       = None
    val salary4   :Option[Double]        = None
    val date4     :Option[LocalDateTime] = None
	
    // Usage
    val batman:Option[String] = None
    val catwoman:Option[String] = Some("Selina Kyle")

    // Is the value there ?
    println( batman.isDefined )
    println( batman.isEmpty )

    // Discouraged ( as it could be null )
    println( batman.get )
    println( catwoman.get )

    // Encouraged ( get if present or default - to avoid null exception )
    println( batman.getOrElse("Bruce Wayne").toUpperCase)

```

{: .btn .btn-primary}
back to top


## Call by name

```scala 

  // CALL BY NAME: takes a code block using syntax : =>
  def refactor1(block: => Unit) : Unit = {
    println( "review this code in area" )
    block
  }


  // Same function using normal function
  def refactor2 (block:() => Unit ) : Unit = {
    println( "review this code in area : " + area )
    block
  }


  def showCallByName(): Unit = {
    // this block of code is wrapped up as a function and called.
    // With call by name, you DO NOT have to use syntax () => for passing a function
    refactor1 {
	
      println("refactor this code!")
    
    }


    // Without call by name, notice you MUST use syntax () => for passing a function
    refactor2 ( () => {
    
      println("refactor this code!")
    
    })
  }
	

```

{: .btn .btn-primary}
back to top


## Classes

```scala 

  // 1. You can declare class members in the constructor!
  // 2. You can declare set the access modifiers ( none = public, protected, private )
  // 3. You can declare whether they are mutable ( var ) or immutable ( val )
  class User( val name:String,
              var isActive:Boolean,
              protected val email:String,
              private val account:Int
  
             )
  {
  
    // Overloaded constructors must call the primary constructor first
    def this() = {
      this("", false, "", 0)
    }
  
  
    // Overloaded constructors must call the primary constructor first
    def this(name:String) =
    {
      this(name, false, "", 0)
    }
  
  
    // You can start executing code after the primary constructor is called.
    println("instantiated with : " + name)
    
  
    def printInfo(): Unit = {
      println( name + ", " + email + ", " + account + ", " + isActive )
    }
  }

```

{: .btn .btn-primary}
back to top


## Traits

```scala 
 
  // Traits are like interfaces with implementations
  // You can compose your clases to extend the trait.
  trait LogTrait {
  
    // debug only for classes and subclases
    protected def debug(msg:String):Unit = {
      log("debug", msg)
    }
  
  
    // info - public access 
    def info(msg:String):Unit = {
      log("info", msg)
    }
  
	
    // error - public access 
    def error(msg:String):Unit = {
      log("error", msg)
    }
  
  
    // no access in any class that uses this trait   
    private def log(level:String, msg:String):Unit = {
      println( s"${level} : ${msg}" )
    }
  }
  
  
  // Easy to now extend your class using the trait
  // Traits provide a way to compose/attach behaviour to your class easily
  class Service1 extends LogTrait {
  
    def process():Unit = {
      debug("my service 1")
      info("starting")
      error("error!")
    }
  }
  
    
  // Trait method access from external class
  class MyApp {
  
    def process():Unit = {
      val svc = new Service1()
  
      // Compile error ( protected )
      //svc.debug("my service 2")
	  
      svc.info("starting")
      svc.error("error!")
    }
  }

```

{: .btn .btn-primary}
back to top


## Case classes

```scala 

  
  // CASE Classes
  // 1. members in constructor are immutable by default
  // 2. copy support built-in
  // 3. useful for pattern-matching ( explained later )
  // 
  // Just add "case" before the class name
  case class User( name:String, isActive:Boolean, email:String, account:Int )
  {
  }

  
  val user = new User("john doe", true, "johndoe@gmail.com", 123456)

  // 1. Case class constructor members are immutable by default
  // Compile error
  // user.email = "j@gmail.com"
  
  // 2. Case classes give you automatic copy constructors
  val jane = user.copy("jane doe")
  println( jane )
  
```

{: .btn .btn-primary}
back to top


## Pattern matching

```scala 

  def testMatch(message:Any) :Unit = {

    message match {
	
      // 1. exact value match
      case 7                                => println("lucky 7")
      case "cat"                            => println("meow")
      case "hawk" | "eagle"                 => println("soar")

      // 2. data type + value match
      case User3("john"  , false, "", 1234567) => println("john ")
      case User3("jane"  , true , "", 1234567) => println("jane ")
      case User3("batman", true , "", 0      ) => println("bats ")

      // 3. data type match
      case a:Int                            => println("number " + a)
      case b:Double                         => println("double " + b)
      case s:String                         => println("text " + s)
      case u:User3                          => println("user" )

      // 4. Option
      case None                             => print( "No value supplied" )
      case Some(x)                          => print( "Some value" + x )
        
      // 5. default case
      case _                                => println("unknown")
    }
  }
  
  testMatch("dog")
  testMatch("cat")
  testMatch("hawk")
  testMatch(2)
  testMatch(3.5)
  testMatch("some text")
  testMatch(User3("john"  , false, "", 1234567))
  testMatch(User3("jane"  , true , "", 1234567))
  testMatch(User3("batman", true , "", 0      ))
  testMatch((2,3))
  
```

{: .btn .btn-primary}
back to top


## Operators
operator overloading is implemented using simple method syntax. this examples creates a custom DateTime class that is a simple wrapper on the java 8 LocalDateTime.
It implements the <, <= , >, >=, ==, != operators to be able to compare instances of this class.

```scala 

class DateTime(private val _date: LocalDateTime )  {

  val year    = _date.getYear
  val month   = _date.getMonth.getValue
  val day     = _date.getDayOfMonth
  val hours   = _date.getHour
  val minutes = _date.getMinute
  val seconds = _date.getSecond
  val raw     = _date

  // Implement the <, <= , >, >=, ==, != operators 
  // for this DateTime class.
  

  def < (dt:DateTime): Boolean = {
    compareTo(dt) == -1
  }


  def <= (dt:DateTime): Boolean = {
    compareTo(dt) <= 0
  }


  def > (dt:DateTime): Boolean = {
    compareTo(dt) == 1
  }


  def >= (dt:DateTime): Boolean = {
    compareTo(dt) >= 0
  }


  def == (dt:DateTime): Boolean = {
    compareTo(dt) == 0
  }


  def != (dt:DateTime): Boolean = {
    compareTo(dt) != 0
  }


  def compareTo(dt:DateTime) : Int = {
    val result = _date.toInstant(ZoneOffset.UTC).compareTo(dt._date.toInstant(ZoneOffset.UTC))
    result
  }
 
}

```

{: .btn .btn-primary}
back to top


## Implicits

```scala 

package slate.common 

object StringHelpers {

  implicit class StringExt(text:String) {

    /**
     * converts the text to valid url path which means:
     * 1. trim leading / trailing spaces
     * 2. remove spaces and replace with '-'
     * @return
     */
    def toUrlPath():String =
    {
      if(text == null)
        return ""
      text.trim().replaceAllLiterally(" ", "-")
    }
  }
}

 // ...
 
 import slate.common.StringHelpers.StringExts
 
 val name = " slate kit "
 val urlPath = name.toUrlPath()
 println( urlPath)

```

{: .btn .btn-primary}
back to top


## Static methods

```scala 

  object Checks {
	// Just for sample purposes
    def ifElse(condition:Boolean, a:String, b:String): String =
    {
      if(condition) a else b
    }
  }
  
  
  val userId = 2
  
  // Call using class name
  println( Checks.ifElse ( userId > 0 , "user", "no-one"  ) )
  
  // Call using imported method
  import Checks.ifElse
  println( ifElse ( userId < 10, "admin", "normal" ) )

```

{: .btn .btn-primary}
back to top


## Apply

```scala 
  
  // Represents an environment ( e.g. "qa1", "qa" environment )
  case class EnvItem( name:String, env:String, key:String ) {
  
  }
  
  
  object EnvItem
  {
    def apply( name:String, env:String) : EnvItem = {
      EnvItem(name, env, s"$name.$env")
    }
  }

  
  // Build up with class constructor ( key = "name"."mode" )
  val qa1 = new EnvItem("qa1", Env.QA, "dev1.env")
  
  // Build up with .apply ( do NOT need "new" )
  val qa2 = EnvItem("qa1", Env.QA)
```

{: .btn .btn-primary}
back to top


## Classes 2

```scala 

  // 1. You can declare class members in the constructor!
  // 2. You can declare set the access modifiers ( none = public, protected, private )
  // 3. You can declare whether they are mutable ( var ) or immutable ( val )
  class User( val name:String,
              var isActive:Boolean,
              protected val email:String,
              private val account:Int
  
             )
  {
  
    // You can start executing code after the primary constructor is called.
    println("instantiated with : " + name)

  
    // Overloaded constructors must call the primary constructor first
    def this() = {
      this("", false, "", 0)
    }
  
  
    // Overloaded constructors must call the primary constructor first
    def this(name:String) =
    {
      this(name, false, "", 0)
    }
    
  
    def printInfo(): Unit = {
      println( name + ", " + email + ", " + account + ", " + isActive )
    }
  }

```

{: .btn .btn-primary}
back to top



## Classes: Access Modifiers

```scala 

  // 1. Scala members are public by default
  // 2. protected works similar to java 
  // 3. private works similar to java 
  // 4. By setting field to "val" you make it immutable
  class User( val name:String, 
              val isActive:Boolean, 
              protected val account:String, 
              private val ssn:String )
  {
  }
  
  val p = new Person("kishore", true, "abc", "123")

  // Accessible
  println( p.name )
  println( p.isActive )
  
  // Unaccessible
  // println( p.account )
  // println( p.ssn )
}

```

{: .btn .btn-primary}
back to top


## Classes: Deriving

```scala 

  // 1. You can declare class members in the constructor!
  // 2. You can declare set the access modifiers ( none = public, protected, private )
  // 3. You can declare whether they are mutable ( var ) or immutable ( val )
  class User( val name:String, val isActive:Boolean, val roles:String )
  {
  }


  // Just a simple example showing how to extend a class.
  class Admin() extends User("Administrator", true, "admin") 
  {
  }
}

```

{: .btn .btn-primary}
back to top


## Future: Creation 

```scala 
  // NOTE: The type of action = Future[Int]
  // since the future returns value 2 in this example
  val action = Future {
  
    // Simulating a long running operation via Thread.sleep 
    // NOTE: not for use in a real system :-)
    Thread.sleep(1000)
    
    // Return sample result
    2
  }

```

{: .btn .btn-primary}
back to top


## Future: Completion 

```scala 
  // NOTE: The type of action = Future[Int]
  // since the future returns value 2 in this example
  val action = Future {
  
    // Simulating a long running operation via Thread.sleep 
    // NOTE: not for use in a real system :-)
    Thread.sleep(1000)
    
    // Return sample result
    2
  }
	
  // Now handle the result ( success/failure ) of the action
  action.onComplete {
    case Success(res) => println("result: " + res)
    case Failure(err) => println("error : " + err.getMessage)
  }

```

{: .btn .btn-primary}
back to top

## Future: Failures 

```scala 
  // NOTE: The type of action = Future[Int]
  // since the future returns value 2 in this example
  val action = Future {
  
    // Simulating a long running operation
    // via Thread.sleep ( not for use in a real system )
    Thread.sleep(1000)
    
    // Forcing a failure
    throw new Exception("Simulating failure")
  }
	
  // Now handle the result ( success/failure ) of the action
  action.onComplete {
    case Success(res) => println("result: " + res)
    case Failure(err) => println("error : " + err.getMessage)
  }

```

{: .btn .btn-primary}
back to top


## Future: Multiple 

```scala
  // To run multiple future in parallel
  // You must first declare them.
  // Again, let simulate a long running task 
  // via Thread.sleep ( not for use in a real system )
  val f1 = Future { Thread.sleep(1000); 1 }
  val f2 = Future { Thread.sleep(2000); 2 }
  val f3 = Future { Thread.sleep(3000); 3 }
  
  // Now setup a for comprehension to chain them
  // Add up the results of each future ( e.g. 6 )
  val result = for {
    r1 <- f1
    r2 <- f2
    r3 <- f3
  } yield r1 + r2 + r3 
  
  
  // Finally handle completion ( e.g. result = 6 )
  result.onComplete {
    case Success(res) => println("Multiple completed: " + res)
    case Failure(err) => println("Multiple failed   : " + err.getMessage)
  }
```

{: .btn .btn-primary}
back to top


## Future: And Then 

```scala
  // NOTE: The type of action = Future[Int]
  // since the future returns value 2 in this example
  val action = Future {
  
    // Simulating a long running operation via Thread.sleep 
    // NOTE: not for use in a real system :-)
    Thread.sleep(1000)
    
    // Return sample result
    2
  }
  
  // You can chain handling of future with "andThen" 
  // and preserve ordering.
  action.andThen {
    case Success(res) => println("Multiple completed: " + res)
    case Failure(err) => println("Multiple failed   : " + err.getMessage)
  }
	
  // return future to caller 
  action 
```

{: .btn .btn-primary}
back to top
