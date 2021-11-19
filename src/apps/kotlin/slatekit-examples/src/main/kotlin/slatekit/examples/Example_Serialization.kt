/**
  * <slate_header>
  * author: Kishore Reddy
  * url: www.github.com/code-helix/slatekit
  * copyright: 2015 Kishore Reddy
  * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  * desc: A tool-kit, utility library and server-backend
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slatekit.examples

//<doc:import_required>
import slatekit.results.Try
import slatekit.results.Success
import slatekit.serialization.*
//</doc:import_required>

//<doc:import_examples>


import slatekit.examples.common.User

//</doc:import_examples>



class Example_Serialization : Command("serialization") {

  override fun execute(request: CommandRequest) : Try<Any>
  {

    //<doc:setup>
    // Setup some sample data to serialize
    val user1 = User(2, "superman@metro.com", "super", "man", true, 35)
    val user2 = User(3, "batman@gotham.com" , "bat"  , "man", true, 35)
    val users = listOf(user1, user2)
    //</doc:setup>

    //<doc:examples>
    // The serializers come in very handy during the serialization of
    // entities in the ORM and is used in the CLI and HTTP server.
    // However, they are general purpose and can be used else where.
    // They are :
    // 1. Optimized for data classes
    // 2. Use reflection to get the properties to serialize
    // 3. Support recursion into nested objects
    // 4. Handle lists of type List<*> and maps of basic types Map<*,*>

    // Case 1: Serialize CSV
    val csvSerializer = SerializerCsv()
    val csvData = csvSerializer.serialize(users)
    println("CSV ====================")
    println(csvData)


    // Case 2: Serialize Properties files
    val propsSerializer = SerializerProps()
    val propsData = propsSerializer.serialize(users)
    println("HCON ====================")
    println(propsData)


    // Case 3: Serialize JSON
    val jsonSerializer = SerializerJson()
    val jsonData = jsonSerializer.serialize(users)
    println("JSON ====================")
    println(jsonData)

    //</doc:examples>
    return Success("")
  }

}

/*
//<doc:output>

{{< highlight yaml >}}
2, "superman@metro.com", "super", "man", true, 35
3, "batman@gotham.com", "bat", "man", true, 35
{{< /highlight >}}

{{< highlight yaml >}}
id = 2
email = superman@metro.com
firstName = super
lastName = man
isMale = true
age = 35


id = 3
email = batman@gotham.com
firstName = bat
lastName = man
isMale = true
age = 35
{{< /highlight >}}

{{< highlight yaml >}}
[
  {"id" : 2, "email" : "superman@metro.com", "firstName" : "super", "lastName" : "man", "isMale" : true, "age" : 35},
  {"id" : 3, "email" : "batman@gotham.com", "firstName" : "bat", "lastName" : "man", "isMale" : true, "age" : 35}
]
{{< /highlight >}}
//</doc:output>
*/
