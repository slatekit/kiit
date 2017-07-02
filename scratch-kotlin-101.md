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
  
  // Explicit
  val name2    : String            = "john doe"
  val age2     : Int               = 35
  val account2 : Long              = 12345678901L
  val isActive2: Boolean           = false
  val salary2  : Double            = 20.5
  val date2    : LocalDateTime     = LocalDateTime.now()
  val pair2    : Pair<String, Int> = Pair("john doe", 35)
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