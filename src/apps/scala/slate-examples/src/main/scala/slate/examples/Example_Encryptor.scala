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
package slate.examples


//<doc:import_required>
import slate.common.encrypt.{Encryptor}
import slate.common.results.ResultSupportIn

//</doc:import_required>

//<doc:import_examples>
import slate.common.{Ensure, Strings, Result}
import slate.common.encrypt.{EncryptSupportIn}
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Encryptor extends Cmd("mobile") with EncryptSupportIn  with ResultSupportIn{

  implicit val svc = this


  //<doc:setup>
  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor extends Encryptor("wejklhviuxywehjk", "3214maslkdf03292"){
  }

  // SETUP 2: Create an instance encryptor
  val encryptor = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")

  //</doc:setup>


  override protected def executeInternal(args: Any): AnyRef = {

    //<doc:examples>
    // CASE 1: Encrypt using AES ( text is base64 encoded without newlines )
    val input = "basMoAKSKDFJrd789"
    val encrypted = TestEncryptor.encrypt(input)
    println(encrypted)


    // CASE 2: Decrypt using AES
    val decrypted = TestEncryptor.decrypt(encrypted)
    println(decrypted)


    // CASE 3: Ensure decrypted matches original
    Ensure.isTrue(Strings.isMatch(input, decrypted), "Encryption / decrypting does not work")


    // CASE 4: Use the EncryptSupportIn trait to have built in encrypt/decrypt methods
    // NOTE: You just have to have an _enc member field
    _enc = Some(TestEncryptor)
    println( encrypt(input))
    println( decrypt(encrypted) )

    //</doc:examples>

    ok()
  }
}