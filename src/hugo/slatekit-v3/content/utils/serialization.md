
# Serialization

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Serializers for data classes to generate CSV, Props, HOCON, JSON files</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.serialization</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/serialization" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/serialization</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Serialization.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Serialization.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.serialization.*


// optional 
import slatekit.core.cmds.Cmd
import slatekit.examples.common.User




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



    // Setup some sample data to serialize
    val user1 = User(2, "superman@metro.com", "super", "man", true, 35)
    val user2 = User(3, "batman@gotham.com" , "bat"  , "man", true, 35)
    val users = listOf(user1, user2)
    


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


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

    

{{< /highlight >}}
{{% break %}}


## Output


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
