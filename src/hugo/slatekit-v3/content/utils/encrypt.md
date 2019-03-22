
# Encrypt

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Encryption using AES</td>
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
      <td>slatekit.common.encrypt</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/encrypt" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/encrypt</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Encryptor.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Encryptor.kt</a></td>
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
import slatekit.common.encrypt.Encryptor
import slatekit.common.encrypt.B64Java8


// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Try
import slatekit.results.Success



{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)


  // SETUP 2: Create an instance encryptor
  val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)

  


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


    // CASE 1: Encrypt using AES ( text is base64 encoded without newlines )
    val input = "basMoAKSKDFJrd789"
    val encrypted = TestEncryptor.encrypt(input)
    println(encrypted)


    // CASE 2: Decrypt using AES
    val decrypted = TestEncryptor.decrypt(encrypted)
    println(decrypted)


    // CASE 3: Ensure decrypted matches original
    println(input == decrypted)


    // CASE 4: Use the EncryptSupportIn trait to have built in encrypt/decrypt methods
    // NOTE: You just have to have an _enc member field
    println( encryptor.encrypt(input))
    println( encryptor.decrypt(encrypted) )
    

{{< /highlight >}}
{{% break %}}

