/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.examples


//<doc:import_required>
import slatekit.common.encrypt.Encryptor
import slatekit.common.encrypt.B64Java8
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.ResultEx
import slatekit.common.Success
//</doc:import_examples>


class Example_Encryptor : Cmd("encrypt") {


  //<doc:setup>
  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)


  // SETUP 2: Create an instance encryptor
  val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)

  //</doc:setup>


  override fun executeInternal(args: Array<String>?) : ResultEx<Any> {

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
