/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.examples

import slate.common.encrypt.Encryptor
import slate.common.i18n.I18nStrings
import slate.common.results.ResultSupportIn
import slate.common.{ServiceBaseIn, ServiceBase, Result}
import slate.common.logging.{LoggerBase, LoggerConsole}
import slate.core.cmds.Cmd

class Example_Service extends Cmd("types") with ResultSupportIn {

  //<doc:setup>
  /**
   * this custom service extends the Slate ServiceBase which has built in support for
   * encryption, logging, and resource strings as PUBLIC METHODS
   *
   * You should have to provide the instances of the logger, encryptor and resource strings
   */
  class CustomService(log:LoggerBase, encrypt:Encryptor, res:I18nStrings)
    extends ServiceBase {

    override val logger = Some(log)
    override val encryptor = Some(encrypt)
    override val resources = Some(res)
  }


  /**
   * this custom service extends the Slate ServiceBase which has built in support for
   * encryption, logging, and resource strings.
   *
   * all the encryption, logging, and resource string methods are PROTECTED METHODS
   *
   * You should have to provide the instances of the logger, encryptor and resource strings
   */
  class CustomServiceSupportInternal(log:LoggerBase, encrypt:Encryptor, res:I18nStrings)
    extends ServiceBaseIn {

    _log = Some(log)
    _enc = Some(encrypt)
    _res = Some(res)


    /**
     * show internal access only to log methods
     */
    def testLog():Unit = {
      info("info message")
      debug("debug message")
      warn("warn message")
      error("error message")
    }


    /**
     * show internal access only to encryption methods
     */
    def testEnc():Unit = {
      val encrypted = encrypt("transformers")
      println(encrypted)
      val decrypted = decrypt(encrypted)
      println(decrypted)
    }


    /**
     * show internal access only to encryption methods
     */
    def testRes():Unit = {
      println( translate("app.name") )
    }
  }
  //</doc:setup>


  override protected def executeInternal(args: Any) : AnyRef =
  {
    val svc1 = new CustomService(
      new LoggerConsole(),
      new Encryptor("1234567891234567", "1234567891234567"),
      new I18nStrings()
    )

    val svc2 = new CustomServiceSupportInternal(
      new LoggerConsole(),
      new Encryptor("1234567891234567", "1234567891234567"),
      new I18nStrings()
    )

    //<doc:examples>
    // Use case 1: logging support
    svc1.info("info message")
    svc1.debug("debug message")
    svc1.warn("warn message")
    svc1.error("error message")

    // Use case 2: encryption support
    val encrypted = svc1.encrypt("transformers")
    println(encrypted)
    val decrypted = svc1.decrypt(encrypted)
    println(decrypted)

    // Use case 3: resource strings
    println( svc1.translate("app.name") )

    // Use case 4: with internal only support, log methods are not publicly accessible,
    // only accessible internally in the class.
    svc2.testLog()

    // Use case 5: with internal only support, encryption methods are not publicly accessible,
    // only accessible internally in the class.
    svc2.testEnc()

    // Use case 6: with internal only support, resource string methods are not publicly accessible,
    // only accessible internally in the class.
    svc2.testRes()
    //</doc:examples>

    ok()
  }
}
