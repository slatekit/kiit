package slate.test


import java.io.File

import slate.common.conf.{ConfigBase, ConfigUserDir}
import slate.common.info.Folders
import slate.common.{Files, ApiCredentials}
import slate.tests.common.MyEncryptor
import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import slate.core.common.Conf

/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
class ConfigTests  extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    //createFolders(getFolders)
    println("before")
  }


  def getFolders:Folders = {
    new Folders(
      root = Some("slatekit"),
      group = Some("samples"),
      app = "slate.tests"
    )
  }


  def ensureKey(key: Option[ApiCredentials], suffix:String = "1"):Unit = {
    assert(key.isDefined)
    assert(key.get.account == "acc" + suffix)
    assert(key.get.key     == "key" + suffix)
    assert(key.get.pass    == "pas" + suffix)
    assert(key.get.env     == "env" + suffix)
    assert(key.get.tag     == "tag" + suffix)
  }


  def ensureKeys(conf:ConfigBase): Unit = {
    assert(conf.getString("test.string") == "resources test")
    assert(conf.getBool("test.boolean_true") == true)
    assert(conf.getBool("test.boolean_false") == false)
    assert(conf.getInt("test.integer") == 20)
    assert(conf.getDouble("test.double") == 20.2)

    assert(new Conf().getStringOrElse("test.string1", "d1") == "d1")
    assert(new Conf().getBoolOrElse("test.boolean_true1", false) == false)
    assert(new Conf().getBoolOrElse("test.boolean_false1", true) == true)
    assert(new Conf().getIntOrElse("test.integer1", 123) == 123)
    assert(new Conf().getDoubleOrElse("test.double1", 1.2) == 1.2)
  }


  describe( "values" ) {

    it("can get typed values from default config") {
      val conf = new Conf()
      ensureKeys(conf)
    }
  }


  describe( "encryption" ) {

    it("can decrypt strings") {
      val conf = new Conf(enc = Some(MyEncryptor))
      ensureKeys(conf)
      assert( conf.getString("test.encrypted") == "123456789")
    }
  }


  describe( "fallbacks" ) {

    it("can fallback with parent config") {
      val fallback = new Conf(Some("env.conf"))
      val conf = Conf.loadWithFallbackConfig(Some("env.qa.conf"), fallback)

      assert(conf.getString("test.string") == "resources test 2")
      assert(conf.getBool("test.boolean_true") == false)
      assert(conf.getBool("test.boolean_false") == true)
      assert(conf.getInt("test.integer") == 21)
      assert(conf.getDouble("test.double") == 20.2)
    }
  }



  describe ( "URIs") {

    it ("can load file from file system") {
      val file = new File(System.getProperty("user.home") + "/.slatekit/conf/company.conf")
      assert( file.exists())
    }


    it ("can load file from user dir") {
      val file = new File(System.getProperty("user.home"), ".slatekit/conf/company.conf")
      assert( file.exists())
    }


    it("can get typed values from custom config") {
      val conf = new Conf(Some("env.conf"))
      ensureKeys(conf)
    }


    it("can get typed values from custom config using URI jars://") {
      val conf = new Conf(Some("jars://env.conf"))
      ensureKeys(conf)
    }


    it("can get typed values custom config using URI user://") {
      val conf = new Conf(Some("user://.slatekit/conf/company.conf"))
      ensureKeys(conf)
    }


    it("can get typed values custom config using URI file://") {
      val conf = new Conf(Some("file://" + System.getProperty("user.home") + "/.slatekit/conf/company.conf"))
      ensureKeys(conf)
    }
  }




  describe ( "referencing" ) {

    it("can reference internal file sms") {
      val conf = new Conf(Some("env.conf"))
      val key = conf.apiKey("sms1")
      ensureKey(key)
    }


    it("can reference external file sms from jar://") {
      val conf = new Conf(Some("env.conf"))
      val key = conf.apiKey("sms2")
      ensureKey(key)
    }


    it("can reference external file sms from user://") {
      val conf = new Conf(Some("env.conf"))
      val key = conf.apiKey("sms3")
      ensureKey(key, "3")
    }


    it("can reference external file sms from file://") {
      val conf = new Conf(Some("env.conf"))
      val key = conf.apiKey("sms4")
      ensureKey(key, "4")
    }


  }

  private def createFiles(): Unit = {

    Files.mkUserDir(".slatetests")
    ConfigUserDir.createCredentials(".slatetests", "email",
      new ApiCredentials("account1", "key123456", "pass123456", "dev", "dev01"), Some(MyEncryptor))

    ConfigUserDir.createCredentials(".slatetests", "sms",
      new ApiCredentials("account1", "key123456", "pass123456", "dev", "dev01"), Some(MyEncryptor))

    ConfigUserDir.createCredentials(".slatetests", "aws",
      new ApiCredentials("account1", "key123456", "pass123456", "dev", "dev01"), Some(MyEncryptor))

    ConfigUserDir.createCredentials(".slatetests", "gcm",
      new ApiCredentials("account1", "key123456", "pass123456", "dev", "dev01"), Some(MyEncryptor))
  }
}
