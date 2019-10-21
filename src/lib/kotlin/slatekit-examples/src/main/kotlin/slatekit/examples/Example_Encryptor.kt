/**
  * <slate_header>
  * author: Kishore Reddy
  * url: www.github.com/code-helix/slatekit
  * copyright: 2016 Kishore Reddy
  * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  * desc: A tool-kit, utility library and server-backend
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.examples


//<doc:import_required>
import slatekit.common.encrypt.Encryptor
import slatekit.common.encrypt.B64Java8
//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Try
import slatekit.results.Success
//</doc:import_examples>


class Example_Encryptor : Command("encrypt") {


  //<doc:setup>
  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)


  // SETUP 2: Create an instance encryptor
  val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)

  //</doc:setup>


  override fun execute(request: CommandRequest) : Try<Any> {

    //<doc:examples>
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
    //</doc:examples>

    return Success("")
  }
}
